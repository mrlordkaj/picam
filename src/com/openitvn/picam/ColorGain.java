/*
 * Copyright (C) 2019 Thinh Pham
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

/**
 *
 * @author Thinh Pham
 */
public class ColorGain {
    
    public boolean active;
    public int blue, red;
    
    public ColorGain() {
        this(128, 128);
    }
    
    public ColorGain(int b, int r) {
        this(true, b, r);
    }
    
    public ColorGain(boolean a, int b, int r) {
        this.active = a;
        this.blue = b;
        this.red = r;
    }
}
