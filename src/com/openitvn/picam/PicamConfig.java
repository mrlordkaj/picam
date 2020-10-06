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
public final class PicamConfig extends Preference {
    
    private static PicamConfig instance;

    public static PicamConfig getInstance() {
        if (instance == null) {
            instance = new PicamConfig();
        }
        return instance;
    }
    
    public static final String APP_NAME = "Picam Service Solution";
    public static final String APP_VERSION = "1.0.0";
    public static final String WORK_ROOT = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
    public static final String WORK_PATH = WORK_ROOT+"/picam";
    
    public static void initSystem() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) { }
        System.load(System.getProperty("user.dir")+"/libpicam.so");
    }
    
    public String hostName = null;
    public boolean drawInfo = true;
    public boolean drawMono = false;
    public boolean drawBlob = true;
    
    private PicamConfig() {
        super("picam/application.cfg");
    }
}
