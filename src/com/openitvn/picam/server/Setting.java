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
package com.openitvn.picam.server;

import com.openitvn.picam.gui.Calibrate;
import com.openitvn.helper.FormHelper;
import com.openitvn.picam.proc.BlobDetector;
import com.openitvn.picam.proc.BlobFilter;
import com.openitvn.picam.proc.BoardPattern;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.proc.LensEntry;
import com.openitvn.picam.proc.ParamEntry;
import com.openitvn.picam.Parameter;
import com.openitvn.picam.PicamVideo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author Thinh Pham
 */
public class Setting extends javax.swing.JDialog implements
        ActionListener, ItemListener, ChangeListener {
    
    private final class CalibrateTable extends JTable implements MouseListener, ActionListener {
        final JPopupMenu mnuPopup = new JPopupMenu();
        final JMenuItem mnuDelete = new JMenuItem("Delete");
        
        CalibrateTable() {
            addMouseListener(CalibrateTable.this);
            mnuDelete.addActionListener(CalibrateTable.this);
            mnuPopup.add(mnuDelete);
        }
        
        @Override
        public void tableChanged(TableModelEvent evt) {
            super.tableChanged(evt);
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() == LensCalibrator.COL_ACTIVE) {
                LensEntry e = calib.getActiveEntry();
                if (e != null)
                    video.setCalibration(e.intrinsic, e.distCoeffs);
            }
        }
        
        @Override
        public void valueChanged(ListSelectionEvent evt) {
            super.valueChanged(evt);
            int row = getSelectedRow();
            if (row >= 0) {
                LensEntry e = calib.getEntry(row);
                BoardPattern p = e.pattern;
                Date t = Date.from(Instant.ofEpochMilli(e.time));
                double[] c = e.intrinsic;
                lblCalibDesc.setText("<html>" +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t) + "<br/>" +
                        String.format("%1$s %2$d x %3$d<br/>", p.title, p.width, p.height) +
                        String.format("%1$.3f %2$.3f<br/>", c[0], c[4]) +
                        String.format("%1$.3f %2$.3f<br/>", c[2], c[5]) +
                        String.format("%1$.8f", e.error) +
                        "</html>");
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                int i = rowAtPoint(e.getPoint());
                if (i >= 0 && i < getRowCount())
                    setRowSelectionInterval(i, i);
                else
                    clearSelection();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                int i = getSelectedRow();
                if (i >= 0)
                    mnuPopup.show(this, e.getX(), e.getY());
            }
        }

        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            int i = calibTable.getSelectedRow();
            if (mnuDelete.equals(src)) {
                LensEntry entry = calib.getEntry(i);
                if (entry.active) {
                    JOptionPane.showMessageDialog(Setting.this, "Unable to delete an active entry.", "Delete Entry", JOptionPane.ERROR_MESSAGE);
                } else {
                    int rs = JOptionPane.showConfirmDialog(Setting.this, String.format("Do you want to delete %1$s?", entry.toString()), "Delete Entry", JOptionPane.YES_NO_OPTION);
                    if (rs == JOptionPane.YES_OPTION)
                        calib.removeEntry(i);
                }
            }
        }
    }
    
    private final class DetectTable extends JTable implements MouseListener, ActionListener {
        final JPopupMenu mnuPopup = new JPopupMenu();
        final JMenuItem mnuDelete = new JMenuItem("Delete");
        
        DetectTable() {
            addMouseListener(DetectTable.this);
            mnuDelete.addActionListener(DetectTable.this);
            mnuPopup.add(mnuDelete);
        }
        
        @Override
        public void tableChanged(TableModelEvent evt) {
            super.tableChanged(evt);
            if (evt.getType() == TableModelEvent.UPDATE) {
                ParamEntry e = detect.getActiveEntry();
                if (e != null) {
                    e.applyTo(video);
                    refreshParams();
                }
            }
        }
        
        @Override
        public void valueChanged(ListSelectionEvent evt) {
            super.valueChanged(evt);
            int row = getSelectedRow();
            if (row >= 0) {
                ParamEntry e = detect.getEntry(row);
                lblDetectDesc.setText("<html>" +
                        e.imxfx + "<br/>" +
                        e.iso + "<br/>" +
                        e.brightness + "<br/>" +
                        e.contrast + "<br/>" +
                        e.sharpness + "<br/>" +
                        "</html>");
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                int i = rowAtPoint(e.getPoint());
                if (i >= 0 && i < getRowCount())
                    setRowSelectionInterval(i, i);
                else
                    clearSelection();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                int i = getSelectedRow();
                if (i >= 0)
                    mnuPopup.show(this, e.getX(), e.getY());
            }
        }

        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            int i = detectTable.getSelectedRow();
            if (mnuDelete.equals(src)) {
                ParamEntry e = detect.getEntry(i);
                if (e.active) {
                    JOptionPane.showMessageDialog(Setting.this, "Unable to delete an active entry.", "Delete Entry", JOptionPane.ERROR_MESSAGE);
                } else {
                    int rs = JOptionPane.showConfirmDialog(Setting.this, String.format("Do you want to delete %1$s?", e.toString()), "Delete Entry", JOptionPane.YES_NO_OPTION);
                    if (rs == JOptionPane.YES_OPTION)
                        detect.removeEntry(i);
                }
            }
        }
    }
    
    private static Setting instance;
    
    static Setting getInstance(VideoPlay gui) {
        if (instance == null) {
            instance = new Setting(gui);
            FormHelper.setToCenter(instance, gui);
        }
        return instance;
    }
    
    private final VideoPlay gui;
    private final PicamVideo video;
    private final LensCalibrator calib;
    private final BlobDetector detect;

    public Setting(VideoPlay gui) {
        super(gui, false);
        this.gui = gui;
        this.video = gui.video;
        this.calib = gui.calib;
        this.detect = gui.blob;
        initComponents();
        // calib table
        javax.swing.table.TableColumnModel tcm = calibTable.getColumnModel();
        tcm.getColumn(LensCalibrator.COL_ACTIVE).setMinWidth(30);
        tcm.getColumn(LensCalibrator.COL_ACTIVE).setMaxWidth(30);
        tcm.getColumn(LensCalibrator.COL_ERROR).setMinWidth(80);
        tcm.getColumn(LensCalibrator.COL_ERROR).setMaxWidth(80);
        // detect table
        tcm = detectTable.getColumnModel();
        tcm.getColumn(BlobDetector.COL_ACTIVE).setMinWidth(30);
        tcm.getColumn(BlobDetector.COL_ACTIVE).setMaxWidth(30);
        tcm.getColumn(BlobDetector.COL_COLOR).setMinWidth(80);
        tcm.getColumn(BlobDetector.COL_COLOR).setMaxWidth(80);
        
        refreshParams();
    }
    
    @Override
    public void dispose() {
        instance = null;
        super.dispose();
    }
    
    private void refreshParams() {
        // camera
        ivExposure.setValue(video.getISO());
        cboImageEffect.setSelectedItem(video.getImageFx());
        ivSharpness.setValue(video.getSharpness());
        ivContrast.setValue(video.getContrast());
        ivBrightness.setValue(video.getBrightness());
        // color filter
        boolean a = detect.isFilterEnabled(BlobFilter.Type.HueMid);
        chkColor.setSelected(a);
        fvHue.setEnabled(a);        
        fvHue.setValue(detect.getFilterValue(BlobFilter.Type.HueMid));
        fvSaturation.setEnabled(a);
        fvSaturation.setLowValue(detect.getFilterValue(BlobFilter.Type.SaturationMin));
        fvSaturation.setHighValue(detect.getFilterValue(BlobFilter.Type.SaturationMax));
        // brightness filter
        fvBrightness.setLowValue(detect.getFilterValue(BlobFilter.Type.BrightnessMin));
        fvBrightness.setHighValue(detect.getFilterValue(BlobFilter.Type.BrightnessMax));
        // bounding filter
        a = detect.isFilterEnabled(BlobFilter.Type.BoundingMin);
        chkBound.setSelected(a);
        fvBounding.setEnabled(a);
        fvBounding.setLowValue(detect.getFilterValue(BlobFilter.Type.BoundingMin));
        fvBounding.setHighValue(detect.getFilterValue(BlobFilter.Type.BoundingMax));
        // contrast filter
        a = detect.isFilterEnabled(BlobFilter.Type.ContrastMin);
        chkContrast.setSelected(a);
        fvContrast.setEnabled(a);
        fvContrast.setLowValue(detect.getFilterValue(BlobFilter.Type.ContrastMin));
        fvContrast.setHighValue(detect.getFilterValue(BlobFilter.Type.ContrastMax));
    }
    
    private void setFilterEnabled(BlobFilter.Type type, boolean a) {
        detect.setFilterEnabled(type, a);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.getFilter(type).active = a;
    }
    
    private void setFilterValue(BlobFilter.Type type, float val) {
        detect.setFilterValue(type, val);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.getFilter(type).value = val;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popCalib = new javax.swing.JPopupMenu();
        mnuCalibNew = new javax.swing.JMenuItem();
        popDetect = new javax.swing.JPopupMenu();
        mnuDetectNew = new javax.swing.JMenuItem();
        mainTabbed = new javax.swing.JTabbedPane();
        paramTab = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        chkBound = new javax.swing.JCheckBox();
        chkContrast = new javax.swing.JCheckBox();
        fvBrightness = new com.openitvn.control.UCRangeSlider(0, 255, 64, 255);
        fvBounding = new com.openitvn.control.UCRangeSlider(1, 40);
        fvContrast = new com.openitvn.control.UCRangeSlider(0, 1);
        chkColor = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        fvSaturation = new com.openitvn.control.UCRangeSlider(0, 255, 64, 255);
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fvHue = new com.openitvn.control.UCColorSlider();
        jPanel6 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        cboImageEffect = new javax.swing.JComboBox<>();
        ivSharpness = new com.openitvn.control.UCValueSlider(-100, 100);
        ivContrast = new com.openitvn.control.UCValueSlider(-100, 100);
        ivBrightness = new com.openitvn.control.UCValueSlider(0, 100);
        jLabel22 = new javax.swing.JLabel();
        ivExposure = new com.openitvn.control.UCValueSlider(100, 800);
        calibTab = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        calibTable = new CalibrateTable();
        jLabel1 = new javax.swing.JLabel();
        lblCalibDesc = new javax.swing.JLabel();
        trackTab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detectTable = new DetectTable();
        jLabel2 = new javax.swing.JLabel();
        lblDetectDesc = new javax.swing.JLabel();

        mnuCalibNew.setText("New Entry");
        mnuCalibNew.addActionListener(this);
        popCalib.add(mnuCalibNew);

        mnuDetectNew.setText("New Entry");
        mnuDetectNew.addActionListener(this);
        popDetect.add(mnuDetectNew);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainTabbed.setFocusable(false);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Detector Filters"));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Bounding");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Contrast");

        chkBound.addItemListener(this);

        chkContrast.addItemListener(this);

        fvBrightness.setFractionRange(0);
        fvBrightness.setStep(0.5F);
        fvBrightness.addChangeListener(this);

        fvBounding.setFractionRange(0);
        fvBounding.setStep(0.1F);
        fvBounding.addChangeListener(this);

        fvContrast.setFractionRange(2);
        fvContrast.setStep(0.002F);
        fvContrast.addChangeListener(this);

        chkColor.addItemListener(this);

        jLabel3.setText("Colour");

        fvSaturation.setFractionRange(0);
        fvSaturation.setStep(0.5F);
        fvSaturation.addChangeListener(this);

        jLabel4.setText("Saturation");

        jLabel5.setText("Brightness");

        fvHue.addChangeListener(this);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fvContrast, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fvBounding, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fvBrightness, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fvSaturation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fvHue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkColor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBound, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkContrast, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel9Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkBound, chkColor, chkContrast});

        jPanel9Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fvBounding, fvBrightness, fvContrast, fvHue, fvSaturation});

        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkColor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fvHue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fvSaturation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fvBrightness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkBound)
                            .addComponent(fvBounding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkContrast)
                            .addComponent(fvContrast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkBound, chkColor, chkContrast, fvBounding, fvBrightness, fvContrast, fvHue, fvSaturation, jLabel10, jLabel3, jLabel4, jLabel5, jLabel9});

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Image Processing"));

        jLabel23.setText("Brightness");

        jLabel24.setText("Sharpness");

        jLabel25.setText("Contrast");

        jLabel28.setText("Exposure");

        cboImageEffect.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        cboImageEffect.setModel(new javax.swing.DefaultComboBoxModel(
            new com.openitvn.picam.Parameter[] {
                com.openitvn.picam.Parameter.MMAL_PARAM_IMAGEFX_NONE,
                com.openitvn.picam.Parameter.MMAL_PARAM_IMAGEFX_DENOISE,
                com.openitvn.picam.Parameter.MMAL_PARAM_IMAGEFX_BLUR }
        ));
        cboImageEffect.addItemListener(this);

        ivSharpness.addChangeListener(this);

        ivContrast.addChangeListener(this);

        ivBrightness.addChangeListener(this);

        jLabel22.setText("Noise Filter");

        ivExposure.setStep(5.0F);
        ivExposure.addChangeListener(this);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboImageEffect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ivExposure, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ivSharpness, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(ivBrightness, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ivContrast, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(cboImageEffect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(ivExposure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(ivBrightness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(ivContrast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(ivSharpness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboImageEffect, ivBrightness, ivContrast, ivExposure, ivSharpness, jLabel22, jLabel23, jLabel24, jLabel25, jLabel28});

        javax.swing.GroupLayout paramTabLayout = new javax.swing.GroupLayout(paramTab);
        paramTab.setLayout(paramTabLayout);
        paramTabLayout.setHorizontalGroup(
            paramTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paramTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paramTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        paramTabLayout.setVerticalGroup(
            paramTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paramTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainTabbed.addTab("Parameters", paramTab);

        calibTable.setModel(calib);
        calibTable.setRowHeight(20);
        calibTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        calibTable.getTableHeader().setResizingAllowed(false);
        calibTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(calibTable);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("<html><p align=\"right\">\nTime<br/>\nPattern<br/>\nFocal<br/>\nPrincipal<br/>\nError\n</p></html>");

        lblCalibDesc.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblCalibDesc.setForeground(java.awt.Color.blue);
        lblCalibDesc.setText("<html>\nN/A<br/>\nN/A<br/>\nN/A<br/>\nN/A<br/>\nN/A\n</html>");

        javax.swing.GroupLayout calibTabLayout = new javax.swing.GroupLayout(calibTab);
        calibTab.setLayout(calibTabLayout);
        calibTabLayout.setHorizontalGroup(
            calibTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calibTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(calibTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(calibTabLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCalibDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 259, Short.MAX_VALUE)))
                .addContainerGap())
        );
        calibTabLayout.setVerticalGroup(
            calibTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calibTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(calibTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCalibDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainTabbed.addTab("Lenses", calibTab);

        detectTable.setModel(detect);
        detectTable.setRowHeight(20);
        detectTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        detectTable.getTableHeader().setResizingAllowed(false);
        detectTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(detectTable);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("<html><p align=\"right\">\nFilter<br/>\nExposure<br/>\nBrightness<br/>\nContrast<br/>\nSharpness\n</p></html>");

        lblDetectDesc.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblDetectDesc.setForeground(java.awt.Color.blue);
        lblDetectDesc.setText("<html>\nN/A<br/>\nN/A<br/>\nN/A<br/>\nN/A<br/>\nN/A\n</html>");

        javax.swing.GroupLayout trackTabLayout = new javax.swing.GroupLayout(trackTab);
        trackTab.setLayout(trackTabLayout);
        trackTabLayout.setHorizontalGroup(
            trackTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(trackTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(trackTabLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDetectDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 245, Short.MAX_VALUE)))
                .addContainerGap())
        );
        trackTabLayout.setVerticalGroup(
            trackTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(trackTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDetectDesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainTabbed.addTab("Detectors", trackTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbed)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == mnuCalibNew) {
            Setting.this.mnuCalibNewActionPerformed(evt);
        }
        else if (evt.getSource() == mnuDetectNew) {
            Setting.this.mnuDetectNewActionPerformed(evt);
        }
    }

    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getSource() == chkBound) {
            Setting.this.chkBoundItemStateChanged(evt);
        }
        else if (evt.getSource() == chkContrast) {
            Setting.this.chkContrastItemStateChanged(evt);
        }
        else if (evt.getSource() == chkColor) {
            Setting.this.chkColorItemStateChanged(evt);
        }
        else if (evt.getSource() == cboImageEffect) {
            Setting.this.cboImageEffectItemStateChanged(evt);
        }
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == fvBrightness) {
            Setting.this.fvBrightnessStateChanged(evt);
        }
        else if (evt.getSource() == fvBounding) {
            Setting.this.fvBoundingStateChanged(evt);
        }
        else if (evt.getSource() == fvContrast) {
            Setting.this.fvContrastStateChanged(evt);
        }
        else if (evt.getSource() == fvSaturation) {
            Setting.this.fvSaturationStateChanged(evt);
        }
        else if (evt.getSource() == fvHue) {
            Setting.this.fvHueStateChanged(evt);
        }
        else if (evt.getSource() == ivSharpness) {
            Setting.this.ivSharpnessStateChanged(evt);
        }
        else if (evt.getSource() == ivContrast) {
            Setting.this.ivContrastStateChanged(evt);
        }
        else if (evt.getSource() == ivBrightness) {
            Setting.this.ivBrightnessStateChanged(evt);
        }
        else if (evt.getSource() == ivExposure) {
            Setting.this.ivExposureStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void mnuCalibNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCalibNewActionPerformed
        Calibrate c = new Calibrate(gui, video, calib);
        FormHelper.setToCenter(c, gui);
        c.setVisible(true);
        dispose();
    }//GEN-LAST:event_mnuCalibNewActionPerformed

    private void mnuDetectNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDetectNewActionPerformed
        ParamEntry e = new ParamEntry();
        int i = detect.addEntry(e);
        detect.activeEntry(i);
        detectTable.editCellAt(i, BlobDetector.COL_TITLE);
    }//GEN-LAST:event_mnuDetectNewActionPerformed

    private void cboImageEffectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboImageEffectItemStateChanged
        ParamEntry param = detect.getActiveEntry();
        Parameter imxfx = (Parameter) evt.getItem();
        video.setImageFx(imxfx);
        if (param != null)
            param.imxfx = imxfx;
    }//GEN-LAST:event_cboImageEffectItemStateChanged

    private void chkColorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkColorItemStateChanged
        boolean sel = (evt.getStateChange() == ItemEvent.SELECTED);
        fvHue.setEnabled(sel);
        fvSaturation.setEnabled(sel);
        setFilterEnabled(BlobFilter.Type.HueMid, sel);
        setFilterEnabled(BlobFilter.Type.SaturationMin, sel);
        setFilterEnabled(BlobFilter.Type.SaturationMax, sel);
    }//GEN-LAST:event_chkColorItemStateChanged

    private void chkBoundItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkBoundItemStateChanged
        boolean sel = (evt.getStateChange() == ItemEvent.SELECTED);
        fvBounding.setEnabled(sel);
        setFilterEnabled(BlobFilter.Type.BoundingMin, sel);
        setFilterEnabled(BlobFilter.Type.BoundingMax, sel);
    }//GEN-LAST:event_chkBoundItemStateChanged

    private void chkContrastItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkContrastItemStateChanged
        boolean sel = (evt.getStateChange() == ItemEvent.SELECTED);
        fvContrast.setEnabled(sel);
        setFilterEnabled(BlobFilter.Type.ContrastMin, sel);
        setFilterEnabled(BlobFilter.Type.ContrastMax, sel);
    }//GEN-LAST:event_chkContrastItemStateChanged

    private void ivExposureStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ivExposureStateChanged
        int iso = Math.round(ivExposure.getValue());
        video.setISO(iso);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.iso = iso;
    }//GEN-LAST:event_ivExposureStateChanged

    private void ivBrightnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ivBrightnessStateChanged
        int bright = Math.round(ivBrightness.getValue());
        video.setBrightness(bright);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.brightness = bright;
    }//GEN-LAST:event_ivBrightnessStateChanged

    private void ivContrastStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ivContrastStateChanged
        int contrast = Math.round(ivContrast.getValue());
        video.setContrast(contrast);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.contrast = contrast;
    }//GEN-LAST:event_ivContrastStateChanged

    private void ivSharpnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ivSharpnessStateChanged
        int sharpness = Math.round(ivSharpness.getValue());
        video.setSharpness(sharpness);
        ParamEntry param = detect.getActiveEntry();
        if (param != null)
            param.sharpness = sharpness;
    }//GEN-LAST:event_ivSharpnessStateChanged

    private void fvHueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fvHueStateChanged
        setFilterValue(BlobFilter.Type.HueMid, fvHue.getValue());
    }//GEN-LAST:event_fvHueStateChanged

    private void fvSaturationStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fvSaturationStateChanged
        setFilterValue(BlobFilter.Type.SaturationMin, fvSaturation.getLowValue());
        setFilterValue(BlobFilter.Type.SaturationMax, fvSaturation.getHighValue());
    }//GEN-LAST:event_fvSaturationStateChanged

    private void fvBrightnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fvBrightnessStateChanged
        setFilterValue(BlobFilter.Type.BrightnessMin, fvBrightness.getLowValue());
        setFilterValue(BlobFilter.Type.BrightnessMax, fvBrightness.getHighValue());
    }//GEN-LAST:event_fvBrightnessStateChanged

    private void fvBoundingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fvBoundingStateChanged
        setFilterValue(BlobFilter.Type.BoundingMin, fvBounding.getLowValue());
        setFilterValue(BlobFilter.Type.BoundingMax, fvBounding.getHighValue());
    }//GEN-LAST:event_fvBoundingStateChanged

    private void fvContrastStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fvContrastStateChanged
        setFilterValue(BlobFilter.Type.ContrastMin, fvContrast.getLowValue());
        setFilterValue(BlobFilter.Type.ContrastMax, fvContrast.getHighValue());
    }//GEN-LAST:event_fvContrastStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel calibTab;
    private javax.swing.JTable calibTable;
    private javax.swing.JComboBox<com.openitvn.picam.Parameter> cboImageEffect;
    private javax.swing.JCheckBox chkBound;
    private javax.swing.JCheckBox chkColor;
    private javax.swing.JCheckBox chkContrast;
    private javax.swing.JTable detectTable;
    private com.openitvn.control.UCRangeSlider fvBounding;
    private com.openitvn.control.UCRangeSlider fvBrightness;
    private com.openitvn.control.UCRangeSlider fvContrast;
    private com.openitvn.control.UCColorSlider fvHue;
    private com.openitvn.control.UCRangeSlider fvSaturation;
    private com.openitvn.control.UCValueSlider ivBrightness;
    private com.openitvn.control.UCValueSlider ivContrast;
    private com.openitvn.control.UCValueSlider ivExposure;
    private com.openitvn.control.UCValueSlider ivSharpness;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCalibDesc;
    private javax.swing.JLabel lblDetectDesc;
    private javax.swing.JTabbedPane mainTabbed;
    private javax.swing.JMenuItem mnuCalibNew;
    private javax.swing.JMenuItem mnuDetectNew;
    private javax.swing.JPanel paramTab;
    private javax.swing.JPopupMenu popCalib;
    private javax.swing.JPopupMenu popDetect;
    private javax.swing.JPanel trackTab;
    // End of variables declaration//GEN-END:variables
}
