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

import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public class NetworkPackage {
    
    String header;
    ByteBuffer buffer;
    
    public String getHeader() {
        return header;
    }
    
    public ByteBuffer getBuffer() {
        return buffer;
    }
    
    public byte[] getData() {
        return buffer.array();
    }
    
    public int getSize() {
        return buffer.capacity();
    }
    
    public boolean isEmpty() {
        return header == null;
    }
}
