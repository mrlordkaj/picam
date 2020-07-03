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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Thinh Pham
 */
public final class UCRangeSlider extends JComponent {
    
    //<editor-fold desc="Min, Max, Low, High" defaultstate="collapsed">
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
    
    public float getHighValue() {
        return high;
    }
    
    public void setHighValue(float val) {
        high = pHigh = Math.max(min, Math.min(max, val));
        updateText();
        repaint();
    }
    
    public float getLowValue() {
        return low;
    }
    
    public void setLowValue(float val) {
        low = pLow = Math.max(min, Math.min(max, val));
        updateText();
        repaint();
    }
    
    private void updateText() {
        text.setText(String.format(fraFmt, low) + " : " + String.format(fraFmt, high));
    }
    //</editor-fold>
    
    //<editor-fold desc="Step, Fraction" defaultstate="collapsed">
    private String fraFmt;
    
    public float getStep() {
        return step;
    }
    
    public void setStep(float step) {
        this.step = step;
        setFractionRange(fractionRange);
    }
    
    public int getFractionRange() {
        return fractionRange;
    }
    
    public void setFractionRange(int range) {
        fractionRange = range;
        if (fractionRange < 0) {
            String str = Float.toString(step);
            range = str.endsWith(".0") ? 0 : Math.min(str.length()-str.lastIndexOf('.')-1, 5);
        }
        fraFmt = "%1$." + range + "f";
        updateText();
    }
    //</editor-fold>
    
    //<editor-fold desc="Diabled, Background, Progress Colors" defaultstate="collapsed">
    private final Color disableColor = new Color(240, 240, 240);
    private Color backgroundColor = Color.WHITE;
    private Color progressColor = Color.BLUE;
    
    public Color getProgressColor() {
        return progressColor;
    }
    
    public void setProgressColor(Color color) {
        progressColor = color;
    }
    
    @Override
    public Color getBackground() {
        return backgroundColor;
    }
    
    @Override
    public void setBackground(Color bg) {
        text.setBackground(bg);
        backgroundColor = bg;
    }
    //</editor-fold>
    
    //<editor-fold desc="Event, Listeners" defaultstate="collapsed">
    private final ArrayList<ChangeListener> listeners = new ArrayList<>();
    
    public void addChangeListener(ChangeListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private class EventAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            // start adjust value by slide button
            if (isEnabled()) {
                prevX = e.getX();
                dragging = true;
                text.requestFocus();
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            // adjusting value by slide button
            if (dragging && isEnabled()) {
                Object src = e.getSource();
                int newX = e.getX();
                if (btnMax.equals(src)) {
                    notifyHighChanged(high + (newX - prevX) * step);
                } else if (btnMin.equals(src)) {
                    notifyLowChanged(low + (newX - prevX) * step);
                }
                prevX = newX;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            // finish adjust value by slide button
            if (dragging) {
                dragging = false;
                for (ChangeListener l : listeners)
                    l.stateChanged(new ChangeEvent(this));
            }
        }
    }
    //</editor-fold>
    
    private final JTextField text;
    private final JLabel btnMin, btnMax;
    private int prevX;
    private float max, min, high, low, pHigh, pLow;
    private float step = 1;
    private int fractionRange;
    private boolean dragging;
    
    public UCRangeSlider() {
        this(-Float.MAX_VALUE, Float.MAX_VALUE, 0, 0);
    }
    
    public UCRangeSlider(float min, float max) {
        this(min, max, min, max);
    }
    
    public UCRangeSlider(float min, float max, float low, float high) {
        this.min = min;
        this.max = max;
        this.low = pLow = Math.max(min, Math.min(max, low));
        this.high = pHigh = Math.max(min, Math.min(max, high));
        EventAdapter evtAdapt = new EventAdapter();
        btnMax = newSlideButton(evtAdapt);
        btnMin = newSlideButton(evtAdapt);
        text = new JTextField();
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setEditable(false);
        text.setOpaque(false);
        setBorder(text.getBorder());
        setLayout(new BorderLayout());
        add(text, BorderLayout.CENTER);
        add(btnMin, BorderLayout.WEST);
        add(btnMax, BorderLayout.EAST);
        text.setBorder(null);
        setFractionRange(-1);
        updateText();
    }
    
    private JLabel newSlideButton(EventAdapter e) {
        JLabel btn = new JLabel();
        btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/control/img/icon_harrow.png")));
        btn.setPreferredSize(new Dimension(20, 0));
        btn.setHorizontalAlignment(JLabel.CENTER);
        btn.setOpaque(true);
        btn.addMouseListener(e);
        btn.addMouseMotionListener(e);
        return btn;
    }
    
    @Override
    public void paint(java.awt.Graphics g) {
        boolean enable = isEnabled();
        g.setColor(enable ? backgroundColor : disableColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
        if (enable) {
            float range = (max - min) / text.getWidth();
            int pos = (int)((low - min) / range) + btnMin.getWidth();
            int end = (int)((high - min) / range) + btnMin.getWidth();
            g.setColor(progressColor);
            g.fillRect(pos + 1, 0, end - pos, 2);
        }
    }
    
    private void notifyHighChanged(float val) {
        if (val >= low) {
            boolean changed = val != pHigh;
            setHighValue(val);
            if (changed) {
                for (ChangeListener l : listeners)
                    l.stateChanged(new ChangeEvent(this));
            }
        }
    }
    
    private void notifyLowChanged(float val) {
        if (val <= high) {
            boolean changed = val != pLow;
            setLowValue(val);
            if (changed) {
                for (ChangeListener l : listeners)
                    l.stateChanged(new ChangeEvent(this));
            }
        }
    }
    
    public boolean isValueAdjusting() {
        return dragging;
    }
    
    @Override
    public void setFont(Font font) {
        text.setFont(font);
    }
    
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        text.setEnabled(b);
        btnMin.setEnabled(b);
        btnMax.setEnabled(b);
    }
}