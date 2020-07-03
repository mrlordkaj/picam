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

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Thinh Pham
 */
public abstract class FormHelper {
    
    /**
     * Moves a form to center of another form.
     */
    public static void setToCenter(Component child, Container parent) {
        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - child.getWidth()) / 2;
            int y = parent.getY() + (parent.getHeight() - child.getHeight()) / 2;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            child.setLocation(x, y);
        }
    }
    
    /**
     * Moves a form to center of their parent form.
     */
    public static void centerToParent(Component child) {
        setToCenter(child, child.getParent());
    }
}