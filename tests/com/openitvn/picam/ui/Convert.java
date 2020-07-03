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
package com.openitvn.picam.ui;

import com.openitvn.picam.PicamConfig;
import com.openitvn.picam.server.PicamProfile;
import com.openitvn.picam.proc.BlobDetector;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.proc.LensEntry;
import com.openitvn.picam.PicamVideo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Thinh Pham
 */
public class Convert extends javax.swing.JFrame implements Runnable {
    
    static {
        PicamConfig.initSystem();
    }
    
    private final PicamVideo video = new PicamVideo();
    private final LensCalibrator calib = new LensCalibrator();
    private final BlobDetector detect = new BlobDetector();

    public Convert() {
        initComponents();
    }
    
    public static void main(String[] args) {
        Convert form = new Convert();
        java.awt.EventQueue.invokeLater(form);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new com.openitvn.control.UCFileChooser("usr");
        txtInput = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnConvert = new javax.swing.JButton();
        grcTarget = new com.openitvn.picam.gui.Resolution();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Profile Converter");
        setLocationByPlatform(true);
        setResizable(false);

        txtInput.setEditable(false);
        txtInput.setBackground(java.awt.Color.white);
        txtInput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtInputMouseClicked(evt);
            }
        });

        jLabel1.setText("Input");

        jLabel2.setText("To");

        btnConvert.setText("Convert");
        btnConvert.setEnabled(false);
        btnConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(grcTarget, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtInput, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnConvert, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {grcTarget, txtInput});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grcTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConvert)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnConvert, grcTarget, jLabel1, jLabel2, txtInput});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtInputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtInputMouseClicked
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtInput.setText(file.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(fc.getSelectedFile())) {
                calib.clearEntries();
                detect.clearEntries();
                PicamProfile.load(video, calib, detect, fis);
                grcTarget.setEnabled(true);
                btnConvert.setEnabled(true);
            } catch (IOException ex) {
                grcTarget.setEnabled(false);
                btnConvert.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Unable to load profile.", "Load Profile", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_txtInputMouseClicked

    private void btnConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConvertActionPerformed
        int srcWidth = video.getWidth();
        int srcHeight = video.getHeight();
        int dstWidth = grcTarget.getWidthValue();
        int dstHeight = grcTarget.getHeightValue();
        if (dstWidth == video.getWidth()) {
            JOptionPane.showMessageDialog(this, "Target resolution same as the source.", "Convert Profile", JOptionPane.ERROR_MESSAGE);
        } else {
            float fx = (float)dstWidth / srcWidth;
            float fy = (float)dstHeight / srcHeight;
            video.setWidth(dstWidth);
            video.setHeight(dstHeight);
            for (int i = 0; i < calib.getRowCount(); i++) {
                LensEntry e = calib.getEntry(i);
                e.intrinsic[0] *= fx;
                e.intrinsic[2] *= fx;
                e.intrinsic[4] *= fy;
                e.intrinsic[5] *= fy;
                e.error *= Math.max(fx, fy);
            }
            fc.setSelectedFile(new File(String.format("profile_%1$dx%2$d.xml", dstWidth, dstHeight)));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File out = fc.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(out, false)) {
                    PicamProfile.save(video, calib, detect, fos);
//                        JOptionPane.showMessageDialog(this, "The profile has been converted.", "Convert Profile", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnConvertActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConvert;
    private com.openitvn.control.UCFileChooser fc;
    private com.openitvn.picam.gui.Resolution grcTarget;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtInput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        setVisible(true);
    }
}
