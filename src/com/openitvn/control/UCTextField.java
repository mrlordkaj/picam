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
package com.openitvn.control;

import com.openitvn.helper.StringHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 *
 * @author Thinh Pham
 */
public class UCTextField extends JTextField {
    
    static {
        UCKeyboard.addFocusOverrideClass(UCTextField.class);
    }
    
    private final JLabel prompt;
    
    public String getPrompt() {
        return prompt.getText();
    }
    
    public void setPrompt(String text) {
        prompt.setText(text);
    }
    
    public Color getPromptColor() {
        return prompt.getForeground();
    }
    
    public void setPromptColor(Color color) {
        prompt.setForeground(color);
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (prompt != null)
            prompt.setFont(font);
    }
    
    public UCTextField() {
        this(null, null, 0);
    }
    
    public UCTextField(String text) {
        this(null, text, 0);
    }
    
    public UCTextField(Document doc, String text, int cols) {
        super(doc, text, cols);
        prompt = new JLabel("enter text here");
        prompt.setForeground(Color.GRAY);
        prompt.setFont(super.getFont());
        super.setLayout(new BorderLayout());
        super.add(prompt);
    }
    
    @Override
    public void paint(Graphics g) {
        paintComponent(g);
        paintBorder(g);
        if (getText().length() == 0)
            paintChildren(g);
    }
    
    public String getSearchRegex() {
        String regex = StringHelper.escapeRegex(getText());
        return String.format("(?i)(%1$s)", regex);
    }
}