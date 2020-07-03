/*
 * Copyright (C) 2018 Thinh Pham
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
package com.openitvn.picam.proc;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class NetworkSession implements Closeable {
    
    final Socket socket;
    
    public NetworkSession(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress();
    }
    
    public synchronized NetworkPackage readPackage() throws IOException {
        InputStream is = socket.getInputStream();
        NetworkPackage pack = new NetworkPackage();
        byte[] tmp = new byte[4];
        if (is.read(tmp) == 4) { // header
            String header = new String(tmp);
            if (is.read(tmp) == 4) { // length
                pack.header = header;
                int len = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getInt();
                pack.buffer = NetworkHelper.readPacket(is, len); // content
            }
        }
        return pack;
    }
    
    public void writePackage(String header) throws IOException {
        writePackage(header, new byte[0]);
    }
    
    public void writePackage(String header, byte[] data) throws IOException {
        if (header == null || header.length() != 4) {
            throw new IOException(String.format("Invalid header (%1$s).", header));
        }
        if (data == null) {
            data = new byte[0];
        }
        byte[] tmp = new byte[8 + data.length];
        ByteBuffer bb = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(header.getBytes()); // header
        bb.putInt(data.length); // length
        System.arraycopy(data, 0, tmp, 8, data.length); // content
        OutputStream os = socket.getOutputStream();
        os.write(tmp);
        os.flush();
    }
    
    public void writePackage(String header, float[] data) throws IOException {
        if (header == null || header.length() != 4) {
            throw new IOException(String.format("Invalid header (%1$s).", header));
        }
        if (data == null) {
            data = new float[0];
        }
        int cap = data.length * 4; 
        byte[] tmp = new byte[8 + cap];
        ByteBuffer bb = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(header.getBytes());
        bb.putInt(cap);
        for (float e : data) {
            bb.putFloat(e);
        }
        OutputStream os = socket.getOutputStream();
        os.write(tmp);
        os.flush();
    }
}
