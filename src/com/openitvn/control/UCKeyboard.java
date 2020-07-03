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
package com.openitvn.control;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 *
 * @author Thinh Pham
 */
public abstract class UCKeyboard {
    
    private static final ArrayList<Class> FOCUS_OVERRIDE_TYPES = new ArrayList<>();
    private static final KeyboardFocusManager KEYBOARD_MANAGER = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    
    static {
        FOCUS_OVERRIDE_TYPES.add(JTextField.class);
        FOCUS_OVERRIDE_TYPES.add(JTextArea.class);
        FOCUS_OVERRIDE_TYPES.add(JTextPane.class);
        FOCUS_OVERRIDE_TYPES.add(JEditorPane.class);
    }
    
    public static void addFocusOverrideClass(Class c) {
        if (!FOCUS_OVERRIDE_TYPES.contains(c))
            FOCUS_OVERRIDE_TYPES.add(c);
    }
    
    public static boolean isFocusOverridden() {
        Component com = KEYBOARD_MANAGER.getFocusOwner();
        for (Class type : FOCUS_OVERRIDE_TYPES) {
            if (type.isInstance(com))
                return true;
        }
        return false;
    }
    
    public static void addKeyEventDispatcher(KeyEventDispatcher d) {
        KEYBOARD_MANAGER.addKeyEventDispatcher(d);
    }
    
    public static void removeKeyEventDispatcher(KeyEventDispatcher d) {
        KEYBOARD_MANAGER.removeKeyEventDispatcher(d);
    }
}