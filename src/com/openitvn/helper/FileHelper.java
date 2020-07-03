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

import java.io.File;


/**
 *
 * @author Thinh Pham
 */
public abstract class FileHelper {
    
    private static final String ARCHIVE_SPLITTER = "@"; // separator character between archive and entry path
    
    public static String combinePath(String[] paths) {
        return String.join(ARCHIVE_SPLITTER, paths);
    }
    
    public static String getFileName(String path) {
        String[] dirs = path.split("[\\/\\\\]");
        return dirs[dirs.length-1];
    }
    
    public static String getFileExt(String path) {
        int extPos = path.lastIndexOf('.') + 1;
        return (extPos > 0) ? path.substring(extPos) : "";
    }
    
    public static String cropFileExt(String path) {
        int extPos = path.lastIndexOf('.');
        return (extPos > 0) ? path.substring(0, extPos) : path;
    }
    
    public static String getParentPath(String path) {
        return new File(path).getParent();
    }
    
    public static String sizeToString(long size) {
        float newSize = size / 1024f;
        if (newSize >= 1024 * 1024)
            return String.format("%1$.2f GB", newSize / (1024 * 1024));
        if (newSize > 1024)
            return String.format("%1$.2f MB", newSize / 1024);
        return String.format("%1$.2f KB", newSize);
    }
}
