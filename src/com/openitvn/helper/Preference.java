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
package com.openitvn.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Thinh Pham
 */
public class Preference {
    
    private static final HashMap<String, HashMap<String, String>> PROFILE_NODES = new HashMap<>();
    
    private final File file;
    private HashMap<String, String> properties;
    
    public Preference(String name) {
        String dir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        file = new File(dir + "/" +name);
        String node = file.getAbsolutePath();
        if ((properties = PROFILE_NODES.get(node)) == null) {
            properties = new HashMap<>();
            PROFILE_NODES.put(node, properties);
            try (FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split("\\s*=\\s*");
                    if (s.length == 2)
                        properties.put(s[0], s[1]);
                }
            } catch (IOException ex) {
                System.err.println("Unable to read config file '" + file + "'.");
            }
        }
    }
    
    public void save() {
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw)) {
            for (Entry<String, String> e : properties.entrySet())
                bw.append(e.getKey()).append("=").append(e.getValue()).append('\n');
        } catch (IOException ex) {
            System.err.println("Unable to write config '" + file + "'.");
        }
    }
    
    public void removeKey(String key) {
        properties.remove(key);
    }
    
    public void putBoolean(String key, boolean val) {
        properties.put(key, Boolean.toString(val));
    }
    
    public boolean getBoolean(String key, boolean def) {
        String val = properties.get(key);
        return val == null ? def : Boolean.parseBoolean(val);
    }
    
    public void putInt(String key, int val) {
        properties.put(key, Integer.toString(val));
    }
    
    public int getInt(String key, int def) {
        String val = properties.get(key);
        return val == null ? def : Integer.parseInt(val);
    }
    
    public void putString(String key, String val) {
        properties.put(key, val);
    }
    
    public String getString(String key, String def) {
        String val = properties.get(key);
        return val == null ? def : val;
    }
}
