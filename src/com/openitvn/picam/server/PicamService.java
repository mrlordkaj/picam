/*
 * Copyright (C) 2017 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.picam.server;

import com.openitvn.picam.PicamConfig;
import com.openitvn.picam.Encoding;
import com.openitvn.picam.FreeIMU;
import com.openitvn.picam.PicamVideo;
import com.openitvn.picam.VideoListener;
import com.openitvn.picam.VideoProcessor;
import com.openitvn.picam.proc.BlobDetector;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.proc.NetworkHelper;
import com.openitvn.picam.proc.NetworkPackage;
import com.openitvn.picam.proc.NetworkSession;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class PicamService implements VideoListener, Runnable {
    
    static {
        PicamConfig.initSystem();
        PicamConfig.load();
    }
    
    private final FreeIMU imu = new FreeIMU();
    private final PicamVideo video = new PicamVideo();
    private final LensCalibrator calib = new LensCalibrator();
    private final BlobDetector detect = new BlobDetector();
    private final ServerSocket server;
    private NetworkSession session;
    private Process raspivid;
    
    private PicamService() throws IOException {
        video.addPicamListener(PicamService.this);
        server = new ServerSocket(NetworkHelper.PORT_COMMAND);
    }
    
    private void stopAllServices() {
        if (raspivid != null) {
            raspivid.destroy();
            raspivid = null;
        }
        video.setProcessor(null);
        video.stop();
    }
    
    private void waitConnect(boolean printMessage) {
        // clean any current session
        if (session != null) {
            session.close();
            session = null;
        }
        if (printMessage) {
            // this message is not neccessary after run ping command
            System.out.println("service ready on port "+NetworkHelper.PORT_COMMAND);
        }
        try {
            Socket client = server.accept();
            session = new NetworkSession(client);
            InputStream is = client.getInputStream();
            OutputStream os = client.getOutputStream();
            byte[] tmp;
            is.read(tmp = new byte[4]); // read fourcc
            switch (new String(tmp)) {
                case NetworkHelper.FOURCC_PING:
                    cmdPing(is, os);
                    break;
                    
                case NetworkHelper.FOURCC_CONNECT:
                    cmdLink(is, os);
                    break;
                    
                case NetworkHelper.FOURCC_INFO:
                    cmdInfo(is, os);
                    break;
                    
                case NetworkHelper.FOURCC_MPU_OPEN:
                    cmdOpenMPU(is, os);
                    break;
                    
                case NetworkHelper.FOURCC_MPU_CLOSE:
                    cmdCloseMPU(is, os);
                    break;
                    
                case NetworkHelper.FOURCC_STREAM:
                    cmdStream(is, os);
                    break;
                    
                default:
                    os.write(NetworkHelper.STATUS_FAILURE);
                    os.flush();
                    waitConnect(false);
                    break;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            waitConnect(true);
        }
    }
    
    private void cmdPing(InputStream is, OutputStream os) throws IOException {
        // check latency, only available when socket free
        boolean sendName = is.read() == NetworkHelper.OPTION_YES;
        os.write(NetworkHelper.STATUS_SUCCESS);
        if (sendName) {
            String name = PicamConfig.hostName;
            os.write(name == null ? 0 : name.length());
            if (name != null)
                os.write(name.getBytes());
        }
        os.flush();
        waitConnect(false);
    }
    
    private void cmdInfo(InputStream is, OutputStream os) throws IOException {
        // receive width, height
        ByteBuffer bb = NetworkHelper.readPacket(is, 4);
        int width = bb.getShort();
        int height = bb.getShort();
        // load default profile
        File file = new File(String.format("%1$s/profile_%2$dx%3$d.xml", PicamConfig.WORK_PATH, width, height));
        try (FileInputStream fis = new FileInputStream(file)) {
            PicamProfile.load(video, calib, detect, fis);
            System.out.println("loaded profile: " + file.getName());
        } catch (IOException ex) {
            video.setResolution(width, height);
            video.setRawMode(Encoding.I420);
            System.err.println("no profile found: " + file.getName());
        }
        // send calibration state, plus camera matrix and distort coefficients when calibrated
        if (video.isCalibrated()) {
            os.write(NetworkHelper.STATUS_SUCCESS);
            bb = ByteBuffer.allocate(14 * 4).order(ByteOrder.LITTLE_ENDIAN); // 9 intrinsic + 5 distCoeffs
            for (double v : video.getIntrinsic()) {
                bb.putFloat((float)v);
            }
            for (double v : video.getDistCoeffs()) {
                bb.putFloat((float)v);
            }
            os.write(bb.array());
        } else {
            os.write(NetworkHelper.STATUS_FAILURE);
        }
        os.flush();
        waitConnect(false);
    }
    
    private void cmdOpenMPU(InputStream is, OutputStream os) throws IOException {
        imu.start();
        waitConnect(false);
    }
    
    private void cmdCloseMPU(InputStream is, OutputStream os) {
        imu.stop();
        waitConnect(false);
    }
    
    private void cmdStream(InputStream is, OutputStream os) throws IOException {
        // read parameters
        ByteBuffer bb = NetworkHelper.readPacket(is, 13*4);
        // check camera avaible
        if (video.isRunning()) {
            // send deny message
            os.write(NetworkHelper.STATUS_FAILURE);
            System.err.println("camera isn't available");
        } else {
            int port = bb.getInt();             // port
            video.setWidth(bb.getInt());        // width
            video.setHeight(bb.getInt());       // height
            video.setSensorMode(bb.getInt());   // sensor mode
            video.setShutterSpeed(bb.getInt()); // shutter speed
            video.setFrameRate(bb.getInt());    // framerate
            video.setBitrate(bb.getInt());      // bitrate
            bb.getInt();                        // profile
            bb.getInt();                        // level
            bb.getInt();                        // intra-refresh
            bb.getInt();                        // intra-period
            bb.getInt();                        // quantisation
            boolean useRaspivid = bb.getInt() != 0; // use raspivid;
            // other settings
            video.setRawMode(Encoding.I420);
            video.setEncoding(Encoding.H264);
            // send accept message
            os.write(NetworkHelper.STATUS_SUCCESS);
            System.out.println("received stream request: "+session+":"+port);
            if (useRaspivid) {
                System.out.println(">> raspivid");
                try {
                    String cmd = "raspivid -t 0"+
                            " -w "+video.getWidth()+
                            " -h "+video.getHeight()+
                            " -md "+video.getSensorMode()+
                            " -ss "+video.getShutterSpeed()+
                            " -fps "+video.getFrameRate()+
                            " -b "+video.getBitrate()+
                            " -l -o tcp://0.0.0.0:"+port;
                    raspivid = Runtime.getRuntime().exec(cmd);
                    raspivid.waitFor();
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
            } else {
                System.out.println(">> picam");
                video.startStream(port);
            }
            System.out.println("<< " + (useRaspivid ? "raspivid" : "picam"));
        }
        waitConnect(true);
    }
    
    private void cmdLink(InputStream is, OutputStream os) throws IOException {
        // send result
        System.out.println("connected " + session);
        os.write(NetworkHelper.STATUS_SUCCESS);
        // receive width, height
        ByteBuffer bb = NetworkHelper.readPacket(is, 4);
        int width = bb.getShort();
        int height = bb.getShort();
        // load default profile
        File file = new File(String.format("%1$s/profile_%2$dx%3$d.xml", PicamConfig.WORK_PATH, width, height));
        try (FileInputStream fis = new FileInputStream(file)) {
            PicamProfile.load(video, calib, detect, fis);
            System.out.println("loaded profile: " + file.getName());
        } catch (IOException ex) {
            video.setResolution(width, height);
            video.setRawMode(Encoding.I420);
            System.err.println("no profile: " + file.getName());
        }
        // send calibration state, plus camera matrix and distort coefficients when calibrated
        if (video.isCalibrated()) {
            os.write(NetworkHelper.STATUS_SUCCESS);
            bb = ByteBuffer.allocate(14 * 4).order(ByteOrder.LITTLE_ENDIAN); // 9 intrinsic + 5 distCoeffs
            for (double v : video.getIntrinsic())
                bb.putFloat((float) v);
            for (double v : video.getDistCoeffs())
                bb.putFloat((float) v);
            os.write(bb.array());
        } else {
            os.write(NetworkHelper.STATUS_FAILURE);
        }
        os.flush();
        // after link created, start new thread
        // which executes run() method below
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        try {
            NetworkPackage pkg;
            while (!(pkg = session.readPackage()).isEmpty()) {
                switch (pkg.getHeader()) {
                    case NetworkHelper.FOURCC_BLOB:
                        System.out.println("started blob tracking service");
                        video.setProcessor(detect);
                        video.startVideo();
                        break;
                        
                    case NetworkHelper.FOURCC_STREAM:
                        System.out.println("started camera streaming service");
                        String cmd = String.format(
                                "raspivid -t 0 -fps 90 -w %1$d -h %2$d -md %3$d -b %4$d -l -o tcp://0.0.0.0:%5$d",
                                video.getWidth(),
                                video.getHeight(),
                                video.getSensorMode(),
                                1000000, // bitrate
                                NetworkHelper.PORT_STREAM);
                        raspivid = Runtime.getRuntime().exec(cmd);
                        break;
                        
                    case NetworkHelper.FOURCC_STOP:
                        stopAllServices();
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        stopAllServices();
        System.out.println("disconnected to " + session);
        waitConnect(true);
    }
    
    @Override
    public void videoStarted(PicamVideo vid) {
        
    }
    
    @Override
    public void videoStopped(PicamVideo vid) {
        
    }
    
    @Override
    public synchronized void frameChanged(final PicamVideo vid) {
        try {
            VideoProcessor proc = vid.getProcessor();
            if (detect.equals(proc)) {
                float[] blobData = detect.getXYData();
                session.writePackage(NetworkHelper.FOURCC_BLOB, blobData);
            }
        } catch (IOException ex) {
            EventQueue.invokeLater(new Runnable() {
                @Override
            	public void run() {
                    vid.setProcessor(null);
                    vid.stop();
            	}
            });
        }
    }
    
    public static void main(String[] args) {
        try {
            PicamService service = null;
            int verbose = 1; // notice
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-gui":
                    case "--gui":
                        VideoPlay.main(args);
                        break;

                    case "-sv":
                    case "--service":
                        if (service == null)
                            service = new PicamService();
                        break;

                    case "-vb":
                    case "--verbose":
                        verbose = Integer.parseInt(args[++i]);
                        break;
                }
            }
            if (service != null) {
                service.video.setVerbose(verbose);
                service.waitConnect(true);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}