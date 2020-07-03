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
package com.openitvn.picam;

import com.openitvn.helper.Preference;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Thinh Pham
 */
public abstract class PicamConfig {
    
    public static final String APP_NAME = "Picam Service Solution";
    public static final String APP_VERSION = "1.0.0";
    public static final String WORK_ROOT = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
    public static final String WORK_PATH = WORK_ROOT+"/picam";
    
    static final Preference CONFIG_PROFILE = new Preference("picam/application.cfg");
    
    public static String hostName;
    public static boolean drawInfo;
    public static boolean drawMono;
    public static boolean drawBlob;
    
    public static void load() {
        Preference p = CONFIG_PROFILE;
        hostName = p.getString("hostName", null);
        drawInfo = p.getBoolean("drawInfo", true);
        drawMono = p.getBoolean("drawMono", false);
        drawBlob = p.getBoolean("drawBlob", true);
    }
    
    public static void save() {
        Preference p = CONFIG_PROFILE;
        p.putString("hostName", hostName);
        p.putBoolean("drawInfo", drawInfo);
        p.putBoolean("drawMono", drawMono);
        p.putBoolean("drawBlob", drawBlob);
        p.save();
    }
    
    public static void initSystem() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) { }
        System.load(System.getProperty("user.dir")+"/libpicam.so");
    }
}
