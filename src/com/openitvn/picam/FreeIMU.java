/*
 * Copyright (C) 2020 Thinh Pham
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

import com.openitvn.picam.proc.NetworkHelper;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class FreeIMU {
    
    private final long ptr;
    private native long _create();
    private native void _destroy(long ptr);
    private native void _start(long ptr, int port) throws IOException;
    private native void _stop(long ptr);
    
    public FreeIMU() {
        ptr = _create();
    }
    
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    public void finalize() throws Throwable {
        _destroy(ptr);
        super.finalize();
    }
    
    public boolean start() {
        try {
            _start(ptr, NetworkHelper.PORT_MPU6050);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return false;
        }
    }
    
    public void stop() {
        _stop(ptr);
    }
}
