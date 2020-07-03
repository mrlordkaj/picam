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
package com.openitvn.picam.gui;

import com.openitvn.picam.proc.BoardPattern;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.PicamVideo;
import com.openitvn.picam.server.VideoPlay;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Thinh Pham
 */
public class Calibrate extends javax.swing.JDialog implements
        java.awt.event.ActionListener,
        java.awt.event.ItemListener,
        javax.swing.event.ChangeListener {
    
    private final PicamVideo video;
    private final LensCalibrator calib;
    
    public Calibrate(VideoPlay gui, PicamVideo video, LensCalibrator calib) {
        super(gui, false);
        this.video = video;
        this.calib = calib;
        initComponents();
        cboBoardType.addItemListener(Calibrate.this);
        cboBoardType.setSelectedItem(calib.getBoardPattern());
        sliFocal.addChangeListener(Calibrate.this);
        btnStart.addActionListener(Calibrate.this);
        btnCancel.addActionListener(Calibrate.this);
//        chkSnapshot.addItemListener(Calibrate.this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (btnStart.equals(src)) {
            dispose();
            BoardPattern pat = (BoardPattern)cboBoardType.getSelectedItem();
            pat.width = Math.round(sliCols.getValue());
            pat.height = Math.round(sliRows.getValue());
            int snapshot = Math.round(sliSnapshot.getValue());
            double fx = sliFocal.getValue();
            int cx = Math.round(sliCenterX.getValue());
            int cy = Math.round(sliCenterY.getValue());
            double[] intrinsic = new double[] {
                fx,  0, cx,
                 0, fx, cy,
                 0,  0,  1,
            };
            int flags = LensCalibrator.CALIB_FIX_ASPECT_RATIO;
            if (fx > 0)
                flags |= LensCalibrator.CALIB_USE_INTRINSIC_GUESS;
            if (chkFocalOpt.isEnabled() && !chkFocalOpt.isSelected())
                flags |= LensCalibrator.CALIB_FIX_FOCAL_LENGTH;
            if (!chkCenterOpt.isSelected())
                flags |= LensCalibrator.CALIB_FIX_PRINCIPAL_POINT;
            video.clearCalibration();
//            calib.saveSnapshot = chkSnapshot.isSelected();
//            calib.base64 = chkBase64.isSelected();
            calib.startCalibration(pat, snapshot, intrinsic, flags);
        }
        if (btnCancel.equals(src)) {
            dispose();
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        boolean sel = e.getStateChange() == ItemEvent.SELECTED;
        if (cboBoardType.equals(src) && sel) {
            BoardPattern pat = (BoardPattern)e.getItem();
            sliCols.setValue(pat.width);
            sliRows.setValue(pat.height);
        }
//        if (chkSnapshot.equals(src)) {
//            chkBase64.setEnabled(sel);
//        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if (sliFocal.equals(src)) {
            chkFocalOpt.setEnabled(sliFocal.getValue() > 0);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        cboBoardType = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sliRows = new com.openitvn.control.UCValueSlider(4, 20, 6);
        jLabel6 = new javax.swing.JLabel();
        sliCols = new com.openitvn.control.UCValueSlider(4, 20, 9);
        jLabel5 = new javax.swing.JLabel();
        sliSnapshot = new com.openitvn.control.UCValueSlider(10, 30, 15);
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        sliFocal = new com.openitvn.control.UCValueSlider(0, Float.MAX_VALUE);
        chkFocalOpt = new javax.swing.JCheckBox();
        sliCenterX = new com.openitvn.control.UCValueSlider(0, Float.MAX_VALUE, video.getWidth() / 2);
        sliCenterY = new com.openitvn.control.UCValueSlider(0, Float.MAX_VALUE, video.getHeight() / 2);
        chkCenterOpt = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Calibration");
        setResizable(false);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Pattern Recognition"));

        cboBoardType.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        cboBoardType.setModel(new javax.swing.DefaultComboBoxModel(BoardPattern.values()));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Pattern Type");
        jLabel7.setToolTipText("Board Pattern Type");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Pattern Size");
        jLabel3.setToolTipText("Board Size (cols x rows)");

        sliRows.setFractionRange(0);
        sliRows.setStep(0.1F);

        jLabel6.setText("x");

        sliCols.setFractionRange(0);
        sliCols.setStep(0.1F);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Snapshots");

        sliSnapshot.setFractionRange(0);
        sliSnapshot.setStep(0.1F);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sliSnapshot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(sliCols, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel6)
                        .addGap(4, 4, 4)
                        .addComponent(sliRows, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboBoardType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {sliCols, sliRows});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboBoardType, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(sliRows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sliCols, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(sliSnapshot, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboBoardType, jLabel3, jLabel5, jLabel6, jLabel7, sliCols, sliRows, sliSnapshot});

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Intrinsic Predefinition"));

        jLabel2.setText("Focal Length");

        sliFocal.setStep(0.1F);

        chkFocalOpt.setSelected(true);
        chkFocalOpt.setToolTipText("Software Optimize");
        chkFocalOpt.setEnabled(false);

        sliCenterX.setFractionRange(0);
        sliCenterX.setStep(0.1F);

        sliCenterY.setFractionRange(0);
        sliCenterY.setStep(0.1F);

        chkCenterOpt.setToolTipText("Software Optimize");

        jLabel4.setText("Principal Point");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(sliCenterX, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8)
                        .addGap(4, 4, 4)
                        .addComponent(sliCenterY, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sliFocal, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFocalOpt, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkCenterOpt, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {sliCenterX, sliCenterY});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkFocalOpt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliFocal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCenterOpt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliCenterY, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliCenterX, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkCenterOpt, chkFocalOpt, jLabel2, jLabel4, jLabel8, sliCenterX, sliCenterY, sliFocal});

        btnStart.setText("Start");

        btnCancel.setText("Cancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStart)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox<BoardPattern> cboBoardType;
    private javax.swing.JCheckBox chkCenterOpt;
    private javax.swing.JCheckBox chkFocalOpt;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel7;
    private com.openitvn.control.UCValueSlider sliCenterX;
    private com.openitvn.control.UCValueSlider sliCenterY;
    private com.openitvn.control.UCValueSlider sliCols;
    private com.openitvn.control.UCValueSlider sliFocal;
    private com.openitvn.control.UCValueSlider sliRows;
    private com.openitvn.control.UCValueSlider sliSnapshot;
    // End of variables declaration//GEN-END:variables
}