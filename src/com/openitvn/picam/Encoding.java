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

import com.openitvn.helper.StringHelper;

/**
 *
 * @author Thinh Pham
 */
public enum Encoding {
    
    I420    ("I420", "YUV 12 bpp"),
    RGB16   ("RGB2", "RGB 16 bpp"),
    BGR24   ("BGR3", "BGR 24 bpp"),
    H264    ("H264", "H.264"),
    MJPEG   ("MJPG", "MJPEG");
    
    public static Encoding fromId(int id) {
        for (Encoding e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }
    
    public final int id;
    public final String text;
    
    private Encoding(String fourCC, String text) {
        this.id = StringHelper.makeFourCC(fourCC);
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
