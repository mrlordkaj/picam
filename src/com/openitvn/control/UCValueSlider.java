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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Thinh Pham
 */
public class UCValueSlider extends JPanel {
    
    public static final int POSITION_WEST = 0,
                            POSITION_EAST = 1;
    public static final int POSITION_LEFT = 2,
                            POSITION_RIGHT = 3;
    
    // controls
    private JLabel button;
    private JTextField textbox;
    
    // events
    private final ArrayList<ChangeListener> changeListeners = new ArrayList<>();
    
    // properties
    protected float max, min, value;
    protected float step = 1;
    protected int fractionRange;
    protected boolean rotation;
    protected Color backgroundColor = Color.WHITE;
    protected Color progressColor = Color.BLUE;
    private int unitPosition = POSITION_EAST;
    private String unitString;
    
    // temporaries
    private int prevX;
    protected float prevValue;
    private String fractionFormat;
    private boolean dragging;
    
    public UCValueSlider() {
        this(-Float.MAX_VALUE, Float.MAX_VALUE, 0);
    }
    
    public UCValueSlider(float min, float max) {
        this(min, max, min);
    }
    
    public UCValueSlider(float min, float max, float value) {
        initComponents();
        // set default values
        this.min = min;
        this.max = max;
        this.value = prevValue = Math.max(min, Math.min(max, value));
        setFractionRange(-1);
    }
    
    private void initComponents() {
    	setOpaque(false);
        setPreferredSize(new Dimension(120, 20));
        setLayout(new BorderLayout(0, 0));

        button = new JLabel(new ImageIcon(UCValueSlider.class.getResource("/com/openitvn/control/img/ico16_slide_arrow.png")));
        MouseAdapter buttonMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                // start adjust value by slide button
                if (isEnabled()) {
                    prevX = evt.getX();
                    dragging = true;
                    textbox.requestFocus();
                }
            }
            @Override
            public void mouseDragged(MouseEvent evt) {
                // adjusting value by slide button
                if (dragging && isEnabled()) {
                    int newX = evt.getX();
                    float newVal = value + (newX - prevX) * step;
                    if (rotation) {
                        if (newVal > value && newVal > max)
                            newVal -= (max - min);
                        else if (newVal < value && newVal < min)
                            newVal += (max - min);
                    }
                    notifyValueChanged(newVal);
                    prevX = newX;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                // finish adjust value by slide button
                if (dragging) {
                    dragging = false;
                    fireValueChanged();
                }
            }
        };
        button.addMouseMotionListener(buttonMouseAdapter);
        button.addMouseListener(buttonMouseAdapter);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(20, 0));
        add(button, BorderLayout.EAST);

        textbox = new JTextField(new UCRegexDocument(UCRegexDocument.REGEX_FLOAT), null, 0);
        textbox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                // finish editing value by textbox
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        // apply new value
                        notifyValueChanged(Float.parseFloat(textbox.getText()));
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // revert to previous value
                        setValue(prevValue);
                        break;
                }
            }
        });
        textbox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                // forward focus event of textbox to this
                evt.setSource(UCValueSlider.this);
                for (FocusListener l : getFocusListeners()) {
                    l.focusGained(evt);
                }
            }
            @Override
            public void focusLost(FocusEvent evt) {
                // apply new value by textbox when lost focus
                notifyValueChanged(Float.parseFloat(textbox.getText()));
                // forward focus event of textbox to this
                evt.setSource(UCValueSlider.this);
                for (FocusListener l : getFocusListeners()) {
                    l.focusLost(evt);
                }
            }
        });
        textbox.setHorizontalAlignment(JTextField.CENTER);
        textbox.setOpaque(false);
        add(textbox, BorderLayout.CENTER);
    	
        // switch textbox's border to this
        super.setBorder(textbox.getBorder());
        textbox.setBorder(null);
    }
    
    @Override
    public void paint(Graphics g) {
        if (isEnabled()) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paint(g);
        extendedPaint(g);
    }
    
    protected void extendedPaint(Graphics g) {
        if (unitString != null && unitString.length() > 0) {
            FontMetrics fm = g.getFontMetrics();
            int width;
            int height = fm.getHeight();
            int top = (textbox.getHeight() + height) >> 1;
            int left = 0;
            switch (unitPosition) {
                case POSITION_EAST:
                    width = fm.stringWidth(unitString);
                    left = textbox.getWidth() - width - 5;
                    break;
                case POSITION_LEFT:
                    width = fm.stringWidth(textbox.getText());
                    left = (textbox.getWidth() - width) >> 1;
                    left -= fm.stringWidth(unitString) + 5;
                    break;
                case POSITION_RIGHT:
                    width = fm.stringWidth(textbox.getText());
                    left = (textbox.getWidth() + width) >> 1;
                    break;
            }
            g.setFont(textbox.getFont());
            g.setColor(Color.LIGHT_GRAY);
            Border border = getBorder();
            if (border != null) {
                top -= border.getBorderInsets(textbox).top;
            }
            g.drawString(unitString, left + 4, top + 1);
        }
        if (isEnabled()) {
            int width = textbox.getWidth();
            g.setColor(progressColor);
            g.fillRect(1, 0, (int)((value - min) / (max - min) * width), 2);
        }
    }
    
    private void notifyValueChanged(float newValue) {
        boolean changed = newValue != prevValue;
        setValue(newValue);
        if (changed) {
            fireValueChanged();
        }
    }
    
    private void fireValueChanged() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : changeListeners) {
            l.stateChanged(evt);
        }
    }
    
    public boolean isValueAdjusting() {
        return dragging;
    }
    
    //<editor-fold desc="Event Listeners" defaultstate="collapsed">
    public void addChangeListener(ChangeListener l) {
        if (!changeListeners.contains(l))
            changeListeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }
    //</editor-fold>
    
    //<editor-fold desc="Extended Properties" defaultstate="collapsed">
    public float getMinimum() {
        return min;
    }
    
    public void setMinimum(float min) {
        this.min = min;
        repaint();
    }
    
    public float getMaximum() {
        return max;
    }
    
    public void setMaximum(float max) {
        this.max = max;
        repaint();
    }
    
    public int getRound() {
        return Math.round(value);
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = prevValue = Math.max(min, Math.min(max, value));
        updateText();
        repaint();
    }
    
    private void updateText() {
        textbox.setText(String.format(fractionFormat, value));
    }
    
    public final float getStep() {
        return step;
    }
    
    public final void setStep(float step) {
        this.step = step;
        setFractionRange(fractionRange);
    }
    
    public final int getFractionRange() {
        return fractionRange;
    }
    
    public final void setFractionRange(int range) {
        fractionRange = range;
        if (fractionRange < 0) {
            String str = Float.toString(step);
            range = str.endsWith(".0") ? 0 : Math.min(str.length()-str.lastIndexOf('.')-1, 5);
        }
        fractionFormat = "%1$." + range + "f";
        updateText();
    }
    
    public final boolean isRotation() {
        return rotation;
    }
    
    public final void setRotation(boolean rotation) {
        this.rotation = rotation;
    }
    
    public Color getProgressColor() {
        return progressColor;
    }
    
    public void setProgressColor(Color color) {
        progressColor = color;
    }
    
    public int getUnitPosition() {
        return unitPosition;
    }
    
    public void setUnitPosition(int v) {
        unitPosition = v;
    }
    
    public void setUnit(String v) {
        unitString = v;
    }
    
    public String getUnit() {
        return unitString;
    }
    //</editor-fold>
    
    //<editor-fold desc="Overridden Properties" defaultstate="collapsed">
    @Override
    public Color getBackground() {
        return backgroundColor;
    }
    
    @Override
    public void setBackground(Color bg) {
        backgroundColor = bg;
    }
    
    @Override
    public void setFont(Font font) {
        if (textbox != null)
            textbox.setFont(font);
    }
    
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        textbox.setEnabled(b);
        button.setEnabled(b);
    }
    
    @Override
    public String getToolTipText() {
        return textbox.getToolTipText();
    }
    
    @Override
    public void setToolTipText(String tip) {
        textbox.setToolTipText(tip);
        button.setToolTipText(tip);
    }
    
    @Override
    public void requestFocus() {
        textbox.requestFocus();
    }
    
    @Override
    public boolean hasFocus() {
        return textbox.hasFocus();
    }
    //</editor-fold>
}
