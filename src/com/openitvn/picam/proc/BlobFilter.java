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

/**
 *
 * @author Thinh Pham
 */
public class BlobFilter {
    
    public enum Type {
        
        HueMid          (0, "hue_mid"),
        SaturationMin   (1, "sat_min"),
        SaturationMax   (2, "sat_max"),
        BrightnessMin   (3, "bri_min"),
        BrightnessMax   (4, "bri_max"),
        BoundingMin     (5, "bou_min"),
        BoundingMax     (6, "bou_max"),
        ContrastMin     (7, "con_min"),
        ContrastMax     (8, "con_max");

        public static Type fromCode(String code) {
            for (Type v : values()) {
                if (v.code.equals(code)) {
                    return v;
                }
            }
            return null;
        }

        public final int id;
        public final String code;

        private Type(int id, String code) {
            this.id = id;
            this.code = code;
        }
    }
    
    public final Type type;
    public boolean active;
    public float value;
    
    public BlobFilter(Type type) {
        this(type, false, 0);
    }
    
    public BlobFilter(Type type, boolean active, float value) {
        this.type = type;
        this.active = active;
        this.value = value;
    }
    
    public BlobFilter(String type) {
        this(type, false, 0);
    }
    
    public BlobFilter(String type, boolean active, float value) {
        this(Type.fromCode(type), active, value);
    }
}
