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
package com.openitvn.picam.proc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public abstract class NetworkHelper {
    
    public static final String  FOURCC_PING      = "ping",
                                FOURCC_INFO      = "info",
                                FOURCC_STREAM    = "strm",
                                FOURCC_CONNECT   = "conn",
                                FOURCC_DISCONN   = "dcon",
                                FOURCC_BLOB      = "blob",
                                FOURCC_PATTERN   = "patt",
                                FOURCC_STOP      = "stop",
                                FOURCC_EXECUTE   = "exec";
    
    public static final byte PACKAGE_BLOB = 20;
    
    public static final String FOURCC_PUT = "put\0";
    public static final String FOURCC_GET = "get\0";
    public static final String FOURCC_END = "end\0";
    
    public static final byte STATUS_SUCCESS =  0; // success
    public static final byte STATUS_FAILURE = -1; // generic error
    public static final byte OPTION_YES     =  1;
    public static final byte OPTION_NO      =  0;
    
    public static final int PORT_COMMAND   = 2110; // socket port for command executive
    public static final int PORT_STREAM    = 2111;
    
    public static boolean readPacket(InputStream is, byte[] dst) {
        try {
            int remain = dst.length;
            int read = 0;
            while (remain > 0) {
                int n = is.read(dst, read, Math.min(is.available(), remain));
                if (n >= 0) {
                    read += n;
                    remain -= n;
                } else {
                    break;
                }
            }
            return remain == 0;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static boolean readPacket(InputStream is, byte[] dst, int pos, int len) throws IOException {
        int remain = Math.min(dst.length, len);
        while (remain > 0) {
            int n = Math.min(is.available(), remain);
            if (n >= 0) {
                is.read(dst, pos, n);
                pos += n;
                remain -= n;
                continue;
            }
            return false;
        }
        return true;
    }
    
    public static ByteBuffer readPacket(InputStream is, int len) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(len).order(ByteOrder.LITTLE_ENDIAN);
        readPacket(is, bb.array());
        return bb;
    }
    
    public static void readPacket(InputStream is, ByteBuffer bb) throws IOException {
        readPacket(is, bb.array());
        bb.rewind();
    }
}