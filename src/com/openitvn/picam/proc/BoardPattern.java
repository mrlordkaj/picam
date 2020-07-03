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

/**
 *
 * @author Thinh Pham
 */
public enum BoardPattern {
    
    Chessboard  (0, "Checker Chessboard", "chessboard",   9,  6),
    AsymCircle  (1, "Asymmetric Circles", "asym_circle",  4, 11);
    
    public static BoardPattern fromId(int id) {
        for (BoardPattern v : values()) {
            if (v.type == id) {
                return v;
            }
        }
        return AsymCircle;
    }
    
    public static BoardPattern fromCode(String code) {
        for (BoardPattern v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return AsymCircle;
    }
    
    public final int type;
    public final String title;
    public final String code;
    public int width, height;
    public float squareSize = 0.1f;
    public float floorDistance = 0;
    
    private BoardPattern(int type, String title, String code, int width, int height) {
        this.type = type;
        this.title = title;
        this.code = code;
        this.width = width;
        this.height = height;
    }
    
    public int getPointCount() {
        return width * height;
    }
    
    @Override
    public String toString() {
        return title;
    }
}
