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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Thinh Pham
 */
public abstract class Preference {

    private final File file;

    protected Preference(String name) {
        String dir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        file = new File(dir + "/" + name);
    }

    private boolean isSupported(Field field) {
        int mod = field.getModifiers();
        return Modifier.isPublic(mod) && Modifier.isStatic(mod) && !Modifier.isFinal(mod);
    }

    protected final void read() {
        try (FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {
            Class<?> clazz = getClass();
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\\s*=\\s*");
                if (s.length == 2) {
                    try {
                        Field field = clazz.getField(s[0]);
                        if (isSupported(field)) {
                            switch (field.getType().getSimpleName()) {
                                case "boolean":
                                    field.setBoolean(clazz, Boolean.parseBoolean(s[1]));
                                    break;
                                case "int":
                                    field.setInt(clazz, Integer.parseInt(s[1]));
                                    break;
                                case "String":
                                    field.set(clazz, s[1]);
                                    break;
                                default:
                                    System.err.printf("Unsupported %s (%s).\n",
                                            field.getType().getSimpleName(),
                                            getClass().getName());
                                    break;
                            }
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                            | SecurityException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.printf("Unable to read config file %s.\n", file);
        }
    }

    public final void write() {
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw)) {
            Class<?> clazz = getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (isSupported(field)) {
                    bw.append(field.getName()).append("=");
                    try {
                        switch (field.getType().getSimpleName()) {
                            case "boolean":
                                bw.append(Boolean.toString(field.getBoolean(clazz)));
                                break;
                            case "int":
                                bw.append(Integer.toString(field.getInt(clazz)));
                                break;
                            case "String":
                                bw.append(field.get(clazz).toString());
                                break;
                            default:
                                System.err.printf("Unsupported %s (%s).\n",
                                            field.getType().getSimpleName(),
                                            getClass().getName());
                                break;
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace(System.err);
                    }
                    bw.append("\n");
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to write config '" + file + "'.");
        }
    }

    public void printProperties() {
        for (Field field : getClass().getDeclaredFields()) {
            if (isSupported(field)) {
                try {
                    System.out.printf("%s %s = %s;\n", field.getType().getSimpleName(), field.getName(), field.get(this));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
