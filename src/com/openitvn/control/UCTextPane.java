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

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Thinh Pham
 */
public class UCTextPane extends JTextPane {
    
    static {
        UCKeyboard.addFocusOverrideClass(UCTextPane.class);
    }
    
    private boolean autoWrap = false;
    
    public void setAutoWrap(boolean b) {
        autoWrap = b;
        getParent().revalidate();
    }
    
    public boolean isAutoWrap() {
        return autoWrap;
    }
    
    private final StyledDocument doc = getStyledDocument();
    private final SimpleAttributeSet attr = new SimpleAttributeSet();
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return autoWrap ?
                super.getScrollableTracksViewportWidth() :
                getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }
    
    public void insertColoredText(int pos, String text, Color color) throws BadLocationException  {
        StyleConstants.setForeground(attr, color);
        doc.insertString(pos, text, attr);
    }
    
    public void appendText(String text) {
        try {
            doc.insertString(doc.getLength(), text, attr);
        } catch (BadLocationException ex) { }
    }
    
    public void setInsertColor(Color color) {
        StyleConstants.setForeground(attr, color);
    }
}