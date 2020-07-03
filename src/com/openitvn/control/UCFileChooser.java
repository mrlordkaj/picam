/*
 * Copyright (C) 2016 Thinh Pham
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
package com.openitvn.control;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Thinh Pham
 */
public class UCFileChooser extends javax.swing.JFileChooser {
    
    public UCFileChooser() {
        super();
    }
    
    public UCFileChooser(String curDir) {
        super(curDir);
    }
    
    public UCFileChooser(File curDir) {
        super(curDir);
    }

    @Override
    public void approveSelection() {
        if (getDialogType() == SAVE_DIALOG) {
            File file = getSelectedFile();
            if (!isAcceptAllFileFilterUsed()) {
                String[] exts = ((FileNameExtensionFilter)getFileFilter()).getExtensions();
                if (exts.length > 0) {
                    boolean validExt = false;
                    for (String ext : exts) {
                        if (file.getName().endsWith(ext)) {
                            validExt = true;
                            break;
                        }
                    }
                    if (!validExt) {
                        file = new File(file.getAbsolutePath() + "." + exts[0]);
                        setSelectedFile(file);
                    }
                }
            }
            if (file.exists() && JOptionPane.showConfirmDialog(this,
                    "The file "+file.getName()+" already exists, overwrite?",
                    "Overwrite",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }
}
