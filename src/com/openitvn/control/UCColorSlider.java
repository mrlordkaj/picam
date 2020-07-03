/*
 * Copyright (C) 2019 Thinh Pham
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author Thinh Pham
 */
public class UCColorSlider extends UCValueSlider {
    
    private final JLabel colorBox;
        
    public UCColorSlider() {
        super(0, 1, 0.28f);
        colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(20, 0));
        colorBox.setOpaque(true);
        super.add(colorBox, BorderLayout.WEST);
        setFractionRange(2);
        setStep(0.002f);
        setRotation(true);
        computeColors(value);
    }
    
    @Override
    protected void extendedPaint(Graphics g) { }

    @Override
    public void setValue(float val) {
        computeColors(val);
        super.setValue(val);
    }

    private void computeColors(float val) {
        colorBox.setBackground(new Color(Color.HSBtoRGB(val, 1, 1)));
    }
}
