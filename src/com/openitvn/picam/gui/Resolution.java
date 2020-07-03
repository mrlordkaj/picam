/*
 * Copyright (C) 2019 Thinh
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
package com.openitvn.picam.gui;

import com.openitvn.control.UCStyleConfig;
import com.openitvn.control.UCValueSlider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Thinh
 */
public class Resolution extends javax.swing.JPanel implements
        FocusListener, MouseListener {
    
    public Resolution() {
        initComponents();
    }
    
    public UCValueSlider getWidthSlider() {
        return sliWidth;
    }
    
    public int getWidthValue() {
        return sliWidth.getRound();
    }
    
    public void setWidthValue(int v) {
        sliWidth.setValue(v);
    }
    
    public UCValueSlider getHeightSlider() {
        return sliHeight;
    }
    
    public int getHeightValue() {
        return sliHeight.getRound();
    }
    
    public void setHeightValue(int v) {
        sliHeight.setValue(v);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sliWidth = new com.openitvn.control.UCValueSlider(32, 1920, 640);
        jLabel6 = new javax.swing.JLabel();
        sliHeight = new com.openitvn.control.UCValueSlider(32, 1080, 480);
        jPanel1 = new javax.swing.JPanel();
        btnPreset = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(218, 24));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        sliWidth.setStep(2.0F);
        sliWidth.addFocusListener(this);
        add(sliWidth);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("x");
        jLabel6.setMinimumSize(new java.awt.Dimension(16, 0));
        jLabel6.setPreferredSize(new java.awt.Dimension(16, 14));
        add(jLabel6);

        sliHeight.setStep(2.0F);
        add(sliHeight);

        jPanel1.setMaximumSize(new java.awt.Dimension(8, 32767));
        jPanel1.setMinimumSize(new java.awt.Dimension(8, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        add(jPanel1);

        btnPreset.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnPreset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/ico16_list.png"))); // NOI18N
        btnPreset.setToolTipText("Pick Preset");
        btnPreset.setMaximumSize(new java.awt.Dimension(24, 16));
        btnPreset.setMinimumSize(new java.awt.Dimension(24, 16));
        btnPreset.setPreferredSize(new java.awt.Dimension(24, 16));
        btnPreset.addMouseListener(this);
        add(btnPreset);
    }

    // Code for dispatching events from components to event handlers.

    public void focusGained(java.awt.event.FocusEvent evt) {
    }

    public void focusLost(java.awt.event.FocusEvent evt) {
        if (evt.getSource() == sliWidth) {
            Resolution.this.sliWidthFocusLost(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == btnPreset) {
            Resolution.this.btnPresetMouseReleased(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void sliWidthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliWidthFocusLost
        sliWidth.setValue(alignUp(sliWidth.getRound(), 32));
    }//GEN-LAST:event_sliWidthFocusLost

    private void btnPresetMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPresetMouseReleased
        JPopupMenu pop = new JPopupMenu();
        pop.add(new ResolutionItem("QVGA",   320,  240));
        pop.add(new ResolutionItem("VGA ",   640,  480));
        pop.add(new ResolutionItem("PAL ",   768,  576));
        pop.add(new ResolutionItem("SVGA",   800,  600));
        pop.add(new ResolutionItem("XGA ",  1024,  768));
        pop.add(new ResolutionItem("UXGA",  1600, 1200));
        pop.add(new JPopupMenu.Separator());
        pop.add(new ResolutionItem("HD 720 " ,  1280,  720));
        pop.add(new ResolutionItem("HD 1080",  1920, 1080));
        pop.show(btnPreset, btnPreset.getWidth(), 0);
    }//GEN-LAST:event_btnPresetMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnPreset;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private com.openitvn.control.UCValueSlider sliHeight;
    private com.openitvn.control.UCValueSlider sliWidth;
    // End of variables declaration//GEN-END:variables

    public static int alignDown(int p, int n) {
        return p & ~(n-1);
    }
    
    public static int alignUp(int p, int n) {
        return alignDown(p+n-1, n);
    }

    private final class ResolutionItem extends JMenuItem implements ActionListener {
        private final int width, height;
        private ResolutionItem(String code, int width, int height) {
            super(String.format("%1$s (%2$d x %3$d)", code, width, height));
            this.width = width;
            this.height = height;
            setFont(UCStyleConfig.FONT_MONO_12);
            addActionListener(ResolutionItem.this);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            sliWidth.setValue(width);
            sliHeight.setValue(height);
            sliWidth.requestFocus();
        }
    }
}
