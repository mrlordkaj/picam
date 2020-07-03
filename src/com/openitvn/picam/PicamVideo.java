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
package com.openitvn.picam;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public final class PicamVideo implements VideoInterface {
    
    private final long ptr;
    private native long _create();
    private native void _destroy(long ptr);
    private native void _startVideo(long ptr) throws IOException;
    private native void _startStream(long ptr, int port) throws IOException;
    private native boolean _isRunning(long ptr);
    private native void _stop(long ptr);
    private native int _getWidth(long ptr);
    private native void _setWidth(long ptr, int v);
    private native int _getHeight(long ptr);
    private native void _setHeight(long ptr, int v);
    private native int _getRawMode(long ptr);
    private native void _setRawMode(long ptr, int v);
    private native int _getFramerate(long ptr);
    private native void _setFramerate(long ptr, int v);
    private native int _getSensorMode(long ptr);
    private native void _setSensorMode(long ptr, int v);
    private native int _getShutterSpeed(long ptr);
    private native void _setShutterSpeed(long ptr, int v);
    private native int _getFlicker(long ptr);
    private native void _setFlicker(long ptr, int v);
    private native int _getSharpness(long ptr);
    private native void _setSharpness(long ptr, int v);
    private native int _getContrast(long ptr);
    private native void _setContrast(long ptr, int v);
    private native int _getBrightness(long ptr);
    private native void _setBrightness(long ptr, int v);
    private native int _getSaturation(long ptr);
    private native void _setSaturation(long ptr, int v);
    private native int _getISO(long ptr);
    private native void _setISO(long ptr, int v);
    private native boolean _isVideoStab(long ptr);
    private native void _setVideoStab(long ptr, boolean v);
    private native int _getExposure(long ptr);
    private native void _setExposure(long ptr, int v);
    private native int _getMeterMode(long ptr);
    private native void _setMeterMode(long ptr, int v);
    private native int _getEVComp(long ptr);
    private native void _setEVComp(long ptr, int v);
    private native int _getAWBMode(long ptr);
    private native void _setAWBMode(long ptr, int v);
    private native int[] _getAWBGains(long ptr);
    private native void _setAWBGains(long ptr, int r, int b);
    private native int _getImageFx(long ptr);
    private native void _setImageFx(long ptr, int v);
    private native int[] _getColorFx(long ptr);
    private native void _setColorFx(long ptr, boolean a, int u, int v);
    private native int _getDRCLevel(long ptr);
    private native void _setDRCLevel(long ptr, int v);
    private native int _getFrameData(long ptr, byte[] dst, int pos, int len);
    private native int _getFrameData(long ptr, short[] dst, int pos, int len);
    private native void _setProcessor(long ptr, long procPtr);
    private native void _setCalibration(long ptr, double[] intrinsic, double[] distCoeffs);
    private native void _unsetCalibration(long ptr);
    private native boolean _isCalibrated(long ptr);
    private native double[] _getIntrinsic(long ptr);
    private native double[] _getDistCoeffs(long ptr);
    private native int _getEncoding(long ptr);
    private native void _setEncoding(long ptr, int v);
    private native int _getBitrate(long ptr);
    private native void _setBitrate(long ptr, int v);
    private native int _getVerbose(long ptr);
    private native void _setVerbose(long ptr, int v);
    
    private BufferedImage frameImg;
    private boolean frameNeedUpdate; // tell need update frame or not
    
    public PicamVideo() {
        ptr = _create();
    }
    
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    public void finalize() throws Throwable {
        _destroy(ptr);
        super.finalize();
    }
    
    public void startVideo() throws IOException {
        // create image data holder
        int w = _getWidth(ptr);
        int h = _getHeight(ptr);
        switch (getRawMode()) {
            case I420:
                frameImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                break;
            case RGB16:
                frameImg = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_565_RGB);
                break;
            case BGR24:
                frameImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
                break;
        }
        
        // start in native, callback all listeners
        _startVideo(ptr);
        for (VideoListener l : listeners)
            l.videoStarted(this);
    }
    
    public void startStream(int port) throws IOException {
        _startStream(ptr, port);
    }
    
    public boolean isRunning() {
        return _isRunning(ptr);
    }
    
    public void stop() {
        _stop(ptr);
        for (VideoListener l : listeners)
            l.videoStopped(this);
    }
    
    /**
     * Callback from native when reached a new frame.
     */
    synchronized void frameUpdated() {
        frameNeedUpdate = true;
        for (VideoListener l : listeners)
            l.frameChanged(this);
    }
    
    public synchronized BufferedImage getFrameImage() {
        if (frameNeedUpdate) {
            switch (getRawMode()) {
                case I420:
                case BGR24:
                    byte[] bb = ((DataBufferByte)frameImg.getRaster().getDataBuffer()).getData();
                    _getFrameData(ptr, bb, 0, bb.length);
                    break;

                case RGB16:
                    short[] sb = ((DataBufferUShort)frameImg.getRaster().getDataBuffer()).getData();
                    _getFrameData(ptr, sb, 0, sb.length);
                    break;
            }
            frameNeedUpdate = false;
        }
        return frameImg;
    }
    
    public int getFrameData(byte[] dst, int pos, int len) {
        return _getFrameData(ptr, dst, pos, len);
    }
    
    //<editor-fold desc="Calibration" defaultstate="collapsed">
    public PicamVideo setCalibration(double[] intrinsic, double[] distCoeffs) {
        _setCalibration(ptr, intrinsic, distCoeffs);
        return this;
    }
    
    public PicamVideo clearCalibration() {
        _unsetCalibration(ptr);
        return this;
    }
    
    public boolean isCalibrated() {
        return _isCalibrated(ptr);
    }
    
    public double[] getIntrinsic() {
        return _getIntrinsic(ptr);
    }
    
    public double[] getDistCoeffs() {
        return _getDistCoeffs(ptr);
    }
    //</editor-fold>
    
    //<editor-fold desc="Listeners / Processor" defaultstate="collapsed">
    private final ArrayList<VideoListener> listeners = new ArrayList<>();
    private VideoProcessor processor;
    
    public PicamVideo addPicamListener(VideoListener l) {
        if (l != null && !listeners.contains(l))
            listeners.add(l);
        return this;
    }
    
    public PicamVideo removePicamListener(VideoListener l) {
        listeners.remove(l);
        return this;
    }
    
    public PicamVideo setProcessor(VideoProcessor proc) {
        if (proc != processor) {
            removePicamListener(processor);
            if (proc == null) {
                _setProcessor(ptr, 0);
                addPicamListener(proc);
            } else {
                _setProcessor(ptr, proc.getPointer());
                proc.videoStarted(this);
            }
            processor = proc;
        }
        return this;
    }
    
    public VideoProcessor getProcessor() {
        return processor;
    }
    //</editor-fold>
    
    //<editor-fold desc="Video Parameters" defaultstate="collapsed">
    @Override
    public int getWidth() {
        return _getWidth(ptr);
    }
    
    @Override
    public PicamVideo setWidth(int v) throws IllegalArgumentException {
        _setWidth(ptr, v);
        return this;
    }
    
    @Override
    public int getHeight() {
        return _getHeight(ptr);
    }
    
    @Override
    public PicamVideo setHeight(int v) throws IllegalArgumentException {
        _setHeight(ptr, v);
        return this;
    }
    
    public PicamVideo setResolution(int w, int h) throws IllegalArgumentException {
        _setWidth(ptr, w);
        _setHeight(ptr, h);
        return this;
    }
    
    @Override
    public Encoding getRawMode() {
        return Encoding.fromId(_getRawMode(ptr));
    }
    
    @Override
    public PicamVideo setRawMode(Encoding v) throws IllegalArgumentException {
        _setRawMode(ptr, v.id);
        return this;
    }
    
    @Override
    public int getFrameRate() {
        return _getFramerate(ptr);
    }
    
    @Override
    public PicamVideo setFrameRate(int v) throws IllegalArgumentException {
        _setFramerate(ptr, v);
        return this;
    }
    
    @Override
    public int getShutterSpeed() {
        return _getShutterSpeed(ptr);
    }
    
    @Override
    public PicamVideo setShutterSpeed(int v) throws IllegalArgumentException {
        _setShutterSpeed(ptr, v);
        return this;
    }
    
    @Override
    public int getSensorMode() {
        return _getSensorMode(ptr);
    }
    
    @Override
    public PicamVideo setSensorMode(int v) throws IllegalArgumentException {
        _setSensorMode(ptr, v);
        return this;
    }
    
    @Override
    public int getISO() {
        return _getISO(ptr);
    }
    
    @Override
    public PicamVideo setISO(int v) throws IllegalArgumentException {
        _setISO(ptr, v);
        return this;
    }
    
    @Override
    public int getEVComp() {
        return _getEVComp(ptr);
    }
    
    @Override
    public PicamVideo setEVComp(int v) throws IllegalArgumentException {
        _setEVComp(ptr, v);
        return this;
    }
    
    @Override
    public boolean isVideoStab() {
        return _isVideoStab(ptr);
    }
    
    @Override
    public PicamVideo setVideoStab(boolean v) throws IllegalArgumentException {
        _setVideoStab(ptr, v);
        return this;
    }
    
    @Override
    public int getSharpness() {
        return _getSharpness(ptr);
    }
    
    @Override
    public PicamVideo setSharpness(int v) throws IllegalArgumentException {
        _setSharpness(ptr, v);
        return this;
    }
    
    @Override
    public int getContrast() {
        return _getContrast(ptr);
    }
    
    @Override
    public PicamVideo setContrast(int v) throws IllegalArgumentException {
        _setContrast(ptr, v);
        return this;
    }
    
    @Override
    public int getBrightness() {
        return _getBrightness(ptr);
    }
    
    @Override
    public PicamVideo setBrightness(int v) throws IllegalArgumentException {
        _setBrightness(ptr, v);
        return this;
    }
    
    @Override
    public int getSaturation() {
        return _getSaturation(ptr);
    }
    
    @Override
    public PicamVideo setSaturation(int v) throws IllegalArgumentException {
        _setSaturation(ptr, v);
        return this;
    }
    
    @Override
    public Parameter getFlicker() {
        return Parameter.getFlicker(_getFlicker(ptr));
    }
    
    @Override
    public PicamVideo setFlicker(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_FLICKER_AVOID)
            _setFlicker(ptr, v.id);
        return this;
    }
    
    @Override
    public Parameter getExposure() {
        return Parameter.getExposure(_getExposure(ptr));
    }
    
    @Override
    public PicamVideo setExposure(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_EXPOSURE_MODE)
            _setExposure(ptr, v.id);
        return this;
    }
    
    @Override
    public Parameter getMeterMode() {
        return Parameter.getMetering(_getMeterMode(ptr));
    }
    
    @Override
    public PicamVideo setMeterMode(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_EXP_METERING_MODE)
            _setMeterMode(ptr, v.id);
        return this;
    }
    
    @Override
    public Parameter getDRCLevel() {
        return Parameter.getDRC(_getDRCLevel(ptr));
    }
    
    @Override
    public PicamVideo setDRCLevel(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_DYNAMIC_RANGE_COMPRESSION)
            _setDRCLevel(ptr, v.id);
        return this;
    }
    
    @Override
    public Parameter getAWBMode() {
        return Parameter.getAWB(_getAWBMode(ptr));
    }
    
    @Override
    public PicamVideo setAWBMode(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_AWB_MODE)
            _setAWBMode(ptr, v.id);
        return this;
    }
    
    @Override
    public ColorGain getAWBGains() {
        int[] g = _getAWBGains(ptr);
        return new ColorGain(g[1], g[0]);
    }
    
    @Override
    public PicamVideo setAWBGains(int r, int b) throws IllegalArgumentException {
        _setAWBGains(ptr, r, b);
        return this;
    }
    
    public PicamVideo setAWBGains(ColorGain v) throws IllegalArgumentException {
        _setAWBGains(ptr, v.red, v.blue);
        return this;
    }
    
    @Override
    public Parameter getImageFx() {
        return Parameter.getImageFx(_getImageFx(ptr));
    }
    
    @Override
    public PicamVideo setImageFx(Parameter v) throws IllegalArgumentException {
        if (v.type == Parameter.MMAL_PARAMETER_IMAGE_EFFECT)
            _setImageFx(ptr, v.id);
        return this;
    }
    
    @Override
    public ColorGain getColorFx() {
        int[] d = _getColorFx(ptr);
        return new ColorGain(d[0] != 0, d[1], d[2]);
    }
    
    @Override
    public PicamVideo setColorFx(boolean a, int u, int v) throws IllegalArgumentException {
        _setColorFx(ptr, a, u, v);
        return this;
    }
    
    public PicamVideo setColorFx(ColorGain v) throws IllegalArgumentException {
        _setColorFx(ptr, v.active, v.blue, v.red);
        return this;
    }
    //</editor-fold>
    
    //<editor-fold desc="Encode Parameters" defaultstate="collapsed">
    public Encoding getEncoding() {
        return Encoding.fromId(_getEncoding(ptr));
    }
    
    public PicamVideo setEncoding(Encoding v) {
        _setEncoding(ptr, v.id);
        return this;
    }
    
    public int getBitrate() {
        return _getBitrate(ptr);
    }
    
    public PicamVideo setBitrate(int v) {
        _setBitrate(ptr, v);
        return this;
    }
    //</editor-fold>
    
    public int isVerbose() {
        return _getVerbose(ptr);
    }
    
    public PicamVideo setVerbose(int verbose) {
        _setVerbose(ptr, verbose);
        return this;
    }
}