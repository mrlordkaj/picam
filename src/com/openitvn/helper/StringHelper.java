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
package com.openitvn.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Thinh Pham
 */
public abstract class StringHelper {
    
    /**
     * Creates fourCC from 4-chars string.
     */
    public static int makeFourCC(String str) throws IllegalArgumentException {
        if (str.length() == 4) {
            byte[] b = str.getBytes();
            return b[0] | b[1] << 8 | b[2] << 16 | b[3] << 24;
        }
        throw new IllegalArgumentException("Input string must have 4 characters.");
    }
    
    /**
     * Creates a shorter string.
     */
    public static String shorterString(String str, int len) {
        if (str.length() > len) {
            int startCrop = str.length() - len + 3;
            return "..." + str.substring(startCrop);
        }
        return str;
    }
    
    /**
     * Creates an escaped regex.
     */
    public static String escapeRegex(String regex) {
//        regex = regex
//                .replace("\\", "\\\\")
//                .replace("(", "\\(")
//                .replace(")", "\\)")
//                .replace("[", "\\[")
//                .replace("]", "\\]")
//                .replace("{", "\\{")
//                .replace("}", "\\}")
//                .replace(".", "\\.")
//                .replace("*", "\\*")
//                ;
        return regex.replaceAll("([\\\\\\(\\)\\[\\]\\(\\)\\.\\,\\*])", "\\\\$1");
    }
    
    /**
     * Creates MD5 checksum string from data.
     */
    public static String makeMD5Checksum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return String.format("%032X", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }
}