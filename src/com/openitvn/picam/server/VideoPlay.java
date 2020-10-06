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

import com.openitvn.picam.PicamConfig;
import com.openitvn.control.UCFileChooser;
import com.openitvn.control.UCStyleConfig;
import com.openitvn.helper.FormHelper;
import com.openitvn.picam.proc.LensCalibrator;
import com.openitvn.picam.proc.BlobDetector;
import com.openitvn.picam.Encoding;
import com.openitvn.picam.PicamVideo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.openitvn.picam.VideoProcessor;
import com.openitvn.picam.gui.About;

/**
 *
 * @author Thinh Pham
 */
public final class VideoPlay extends JFrame implements
        ActionListener, ItemListener,
        com.openitvn.picam.VideoListener {
    
    static {
        PicamConfig.initSystem();
    }
    
    final PicamVideo video = new PicamVideo();
    final LensCalibrator calib = new LensCalibrator();
    final BlobDetector blob = new BlobDetector();
    
    private int paintPeriod = 1000 / 15;
    private long lastPaint, lastInfo;
    
    // fps counter
    private int fps, frameCount;
    private long lastCount;
    
    private final String imgDir = "/com/openitvn/picam/img/";
    private File curDir; // remember current directory of FileChooser
    
    private final PicamConfig config = PicamConfig.getInstance();
    
    private VideoPlay(int width, int height, Encoding raw) {
        initComponents();
        // complete init components
        video.addPicamListener(VideoPlay.this);
        // update drawing menus
        mnuInfo.setSelected(config.drawInfo);
        mnuMono.setSelected(config.drawMono);
        mnuBlob.setSelected(config.drawBlob);
        // preload profile and start video
        String fn = String.format("profile_%1$dx%2$d.xml", width, height);
        try (FileInputStream fis = new FileInputStream(PicamConfig.WORK_PATH + "/" + fn)) {
            PicamProfile.load(video, calib, blob, fis);
        } catch (IOException ex) {
            video.setResolution(width, height);
        }
        video.setRawMode(raw);
        setAdvanced(false);
        grcRes.setWidthValue(video.getWidth());
        grcRes.setHeightValue(video.getHeight());
    }
    
    @Override
    public void dispose() {
        video.stop();
        config.save();
        super.dispose();
        System.exit(0); // TODO: do not force exit
    }
    
    @Override
    public void videoStarted(PicamVideo vid) {
        viewport.setPreferredSize(new Dimension(vid.getWidth(), vid.getHeight()));
        remove(viewport);
        add(viewport);
        pack();
    }
    
    @Override
    public void videoStopped(PicamVideo vid) {
    
    }
    
    @Override
    public synchronized void frameChanged(PicamVideo vid) {
        // remote desktop encounters problem when
        // trying stream at high framerate,
        // so we should limit repaint to a lower frequency.
        long curTime = System.currentTimeMillis();
        if (curTime - lastPaint > paintPeriod) {
            viewport.repaint();
            lastPaint = curTime;
        }
        // fps measurement
        if (curTime - lastCount >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastCount = curTime;
        } else {
            frameCount++;
        }
    }
    
    private void paintViewport(Graphics2D g) {
        g.setFont(UCStyleConfig.FONT_MONO_14);
        g.setColor(Color.GREEN);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        VideoProcessor proc = video.getProcessor();
        if (proc == null) {
            g.drawImage(video.getFrameImage(), 0, 0, null);
        } else {
            proc.paint(video, g);
        }
    }
    
    void newSession() {
        mnuProcVideo.setSelected(true);
        video.setProcessor(null);
        video.stop();
        calib.clearEntries();
        blob.clearEntries();
        startVideo();
    }
    
    void loadSession(File in) {
        mnuProcVideo.setSelected(true);
        video.setProcessor(null);
        video.stop();
        calib.clearEntries();
        blob.clearEntries();
        try (FileInputStream fis = new FileInputStream(in)) {
            PicamProfile.load(video, calib, blob, fis);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to load profile.", "Load Profile", JOptionPane.ERROR_MESSAGE);
        }
        startVideo();
    }
    
    private void startVideo() {
        try {
            video.startVideo();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Picam Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setAdvanced(boolean v) {
        lblSensor.setVisible(v);
        cboSensor.setVisible(v);
        lblShutter.setVisible(v);
        vsShutter.setVisible(v);
        dlgSess.pack();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        procGroup = new javax.swing.ButtonGroup();
        dlgSess = new javax.swing.JDialog(this, true);
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cboRaw = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        grcRes = new com.openitvn.picam.gui.Resolution();
        jLabel1 = new javax.swing.JLabel();
        vsFramerate = new com.openitvn.control.UCValueSlider(0, 90, 90) {
            public void setValue(float v) {
                setUnit(v > 0 ? "fps" : "auto");
                super.setValue(v);
            }
        };
        vsShutter = new com.openitvn.control.UCValueSlider(0, 100) {
            public void setValue(float v) {
                setUnit(v > 0 ? "ms" : "auto");
                super.setValue(v);
            }
        };
        lblShutter = new javax.swing.JLabel();
        cboSensor = new javax.swing.JComboBox<>();
        lblSensor = new javax.swing.JLabel();
        chkAdvanced = new javax.swing.JCheckBox();
        btnStart = new javax.swing.JButton();
        viewport = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                paintViewport((Graphics2D)g);
            }
        };
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuTask = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        mnuSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuProcVideo = new javax.swing.JRadioButtonMenuItem();
        mnuProcCalib = new javax.swing.JRadioButtonMenuItem();
        mnuProcBlob = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuConfig = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        mnuInfo = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuMono = new javax.swing.JCheckBoxMenuItem();
        mnuBlob = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuContent = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnuAbout = new javax.swing.JMenuItem();

        dlgSess.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dlgSess.setTitle("New Session");
        dlgSess.setLocationByPlatform(true);
        dlgSess.setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Camera Settings"));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Resolution");
        jLabel12.setToolTipText("Video Solution");

        cboRaw.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        cboRaw.setModel(new javax.swing.DefaultComboBoxModel(
            new Encoding[] {
                com.openitvn.picam.Encoding.I420,
                com.openitvn.picam.Encoding.RGB16,
                com.openitvn.picam.Encoding.BGR24
            }));
            cboRaw.setSelectedItem(video.getRawMode());

            jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel15.setText("Raw mode");
            jLabel15.setToolTipText("Video Raw Type");

            jLabel1.setText("Framerate");

            vsFramerate.setFractionRange(0);
            vsFramerate.setStep(0.5F);
            vsFramerate.setUnit("auto");
            vsFramerate.setValue(video.getFrameRate());

            vsShutter.setStep(0.2F);
            vsShutter.setUnit("auto");
            vsShutter.setValue(video.getShutterSpeed());

            lblShutter.setText("Shutter spd.");

            cboSensor.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
            cboSensor.setModel(new javax.swing.DefaultComboBoxModel(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7 }));
            cboSensor.setSelectedItem(video.getSensorMode());

            lblSensor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            lblSensor.setText("Sensor mode");
            lblSensor.setToolTipText("Video Solution");

            javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblSensor, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblShutter, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboRaw, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(grcRes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(vsFramerate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(vsShutter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSensor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );

            jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboRaw, cboSensor, grcRes, vsFramerate, vsShutter});

            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel12)
                        .addComponent(grcRes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel15)
                        .addComponent(cboRaw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblSensor)
                        .addComponent(cboSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblShutter)
                        .addComponent(vsShutter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(vsFramerate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );

            jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboRaw, cboSensor, grcRes, jLabel1, jLabel12, jLabel15, lblSensor, lblShutter, vsFramerate, vsShutter});

            chkAdvanced.setText("Advanced mode");
            chkAdvanced.addItemListener(this);

            btnStart.setText("Start Session");
            btnStart.addActionListener(this);

            javax.swing.GroupLayout dlgSessLayout = new javax.swing.GroupLayout(dlgSess.getContentPane());
            dlgSess.getContentPane().setLayout(dlgSessLayout);
            dlgSessLayout.setHorizontalGroup(
                dlgSessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dlgSessLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(dlgSessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(dlgSessLayout.createSequentialGroup()
                            .addComponent(chkAdvanced)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnStart)))
                    .addContainerGap())
            );
            dlgSessLayout.setVerticalGroup(
                dlgSessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dlgSessLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(dlgSessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStart)
                        .addComponent(chkAdvanced))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle(com.openitvn.picam.PicamConfig.APP_NAME);
            setIconImages(java.util.Arrays.asList(new java.awt.Image[] {
                new javax.swing.ImageIcon(getClass().getResource(imgDir+"icon_16.png")).getImage(),
                new javax.swing.ImageIcon(getClass().getResource(imgDir+"icon_24.png")).getImage(),
                new javax.swing.ImageIcon(getClass().getResource(imgDir+"icon_32.png")).getImage(),
                new javax.swing.ImageIcon(getClass().getResource(imgDir+"icon_48.png")).getImage(),
                new javax.swing.ImageIcon(getClass().getResource(imgDir+"icon_64.png")).getImage(),
            }));
            setLocationByPlatform(true);
            setResizable(false);

            javax.swing.GroupLayout viewportLayout = new javax.swing.GroupLayout(viewport);
            viewport.setLayout(viewportLayout);
            viewportLayout.setHorizontalGroup(
                viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 400, Short.MAX_VALUE)
            );
            viewportLayout.setVerticalGroup(
                viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 285, Short.MAX_VALUE)
            );

            getContentPane().add(viewport, java.awt.BorderLayout.CENTER);

            mnuTask.setText("File");

            mnuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
            mnuNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_new.png"))); // NOI18N
            mnuNew.setText("New Profile...");
            mnuNew.addActionListener(this);
            mnuTask.add(mnuNew);

            mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
            mnuLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_open.png"))); // NOI18N
            mnuLoad.setText("Load Profile...");
            mnuLoad.addActionListener(this);
            mnuTask.add(mnuLoad);

            mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            mnuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_save.png"))); // NOI18N
            mnuSave.setText("Save Profile...");
            mnuSave.addActionListener(this);
            mnuTask.add(mnuSave);
            mnuTask.add(jSeparator1);

            mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
            mnuExit.setText("Exit");
            mnuExit.addActionListener(this);
            mnuTask.add(mnuExit);

            jMenuBar1.add(mnuTask);

            jMenu4.setText("Tasks");

            mnuProcVideo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
            procGroup.add(mnuProcVideo);
            mnuProcVideo.setSelected(true);
            mnuProcVideo.setText("No Processor");
            mnuProcVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_camera.png"))); // NOI18N
            mnuProcVideo.addItemListener(this);
            jMenu4.add(mnuProcVideo);

            mnuProcCalib.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
            procGroup.add(mnuProcCalib);
            mnuProcCalib.setText("Lens Calibrator");
            mnuProcCalib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_lens.png"))); // NOI18N
            mnuProcCalib.addItemListener(this);
            jMenu4.add(mnuProcCalib);

            mnuProcBlob.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
            procGroup.add(mnuProcBlob);
            mnuProcBlob.setText("Blob Detector");
            mnuProcBlob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_blob.png"))); // NOI18N
            mnuProcBlob.addItemListener(this);
            jMenu4.add(mnuProcBlob);
            jMenu4.add(jSeparator3);

            mnuConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
            mnuConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_setting.png"))); // NOI18N
            mnuConfig.setText("Preferences...");
            mnuConfig.addActionListener(this);
            jMenu4.add(mnuConfig);

            jMenuBar1.add(jMenu4);

            jMenu1.setText("Draw");

            mnuInfo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, 0));
            mnuInfo.setText("Draw Info");
            mnuInfo.addItemListener(this);
            jMenu1.add(mnuInfo);
            jMenu1.add(jSeparator2);

            mnuMono.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, 0));
            mnuMono.setText("Draw Mono");
            mnuMono.setEnabled(false);
            mnuMono.addItemListener(this);
            jMenu1.add(mnuMono);

            mnuBlob.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
            mnuBlob.setText("Draw Blobs");
            mnuBlob.setEnabled(false);
            mnuBlob.addItemListener(this);
            jMenu1.add(mnuBlob);

            jMenuBar1.add(jMenu1);

            jMenu3.setText("Help");

            mnuContent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
            mnuContent.setText("Contents...");
            jMenu3.add(mnuContent);
            jMenu3.add(jSeparator6);

            mnuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
            mnuAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitvn/picam/img/icon_about.png"))); // NOI18N
            mnuAbout.setText("About");
            mnuAbout.addActionListener(this);
            jMenu3.add(mnuAbout);

            jMenuBar1.add(jMenu3);

            setJMenuBar(jMenuBar1);

            pack();
        }

        // Code for dispatching events from components to event handlers.

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == btnStart) {
                VideoPlay.this.btnStartActionPerformed(evt);
            }
            else if (evt.getSource() == mnuNew) {
                VideoPlay.this.mnuNewActionPerformed(evt);
            }
            else if (evt.getSource() == mnuLoad) {
                VideoPlay.this.mnuLoadActionPerformed(evt);
            }
            else if (evt.getSource() == mnuSave) {
                VideoPlay.this.mnuSaveActionPerformed(evt);
            }
            else if (evt.getSource() == mnuExit) {
                VideoPlay.this.mnuExitActionPerformed(evt);
            }
            else if (evt.getSource() == mnuConfig) {
                VideoPlay.this.mnuConfigActionPerformed(evt);
            }
            else if (evt.getSource() == mnuAbout) {
                VideoPlay.this.mnuAboutActionPerformed(evt);
            }
        }

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getSource() == chkAdvanced) {
                VideoPlay.this.chkAdvancedItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuProcVideo) {
                VideoPlay.this.mnuProcVideoItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuProcCalib) {
                VideoPlay.this.mnuProcCalibItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuProcBlob) {
                VideoPlay.this.mnuProcBlobItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuInfo) {
                VideoPlay.this.mnuInfoItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuMono) {
                VideoPlay.this.mnuMonoItemStateChanged(evt);
            }
            else if (evt.getSource() == mnuBlob) {
                VideoPlay.this.mnuBlobItemStateChanged(evt);
            }
        }// </editor-fold>//GEN-END:initComponents

    private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
        dlgSess.pack();
        FormHelper.centerToParent(dlgSess);
        dlgSess.setVisible(true);
    }//GEN-LAST:event_mnuNewActionPerformed

    private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
        UCFileChooser fc = new UCFileChooser(curDir);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            curDir = fc.getCurrentDirectory();
            loadSession(fc.getSelectedFile());
        }
    }//GEN-LAST:event_mnuLoadActionPerformed

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        UCFileChooser fc = new UCFileChooser(curDir);
        fc.setSelectedFile(new File(String.format("profile_%1$dx%2$d.xml", video.getWidth(), video.getHeight())));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            curDir = fc.getCurrentDirectory();
            File out = fc.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(out, false)) {
                if (PicamProfile.save(video, calib, blob, fos))
                    JOptionPane.showMessageDialog(this, "Saved to: "+out.getName(), "Profile Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuSaveActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConfigActionPerformed
        Setting.getInstance(this).setVisible(true);
    }//GEN-LAST:event_mnuConfigActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        About dlg = new About(this);
        FormHelper.setToCenter(dlg, this);
        dlg.setVisible(true);
    }//GEN-LAST:event_mnuAboutActionPerformed

    private void mnuProcVideoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuProcVideoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED)
            video.setProcessor(null);
    }//GEN-LAST:event_mnuProcVideoItemStateChanged

    private void mnuProcCalibItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuProcCalibItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (video.getRawMode() != Encoding.I420) {
                video.stop();
                video.setRawMode(Encoding.I420);
                startVideo();
            }
            video.setProcessor(calib);
        }
    }//GEN-LAST:event_mnuProcCalibItemStateChanged

    private void mnuProcBlobItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuProcBlobItemStateChanged
        boolean sel = evt.getStateChange() == ItemEvent.SELECTED;
        if (sel) {
            if (video.getRawMode() != Encoding.I420) {
                video.stop();
                video.setRawMode(Encoding.I420);
                startVideo();
            }
            video.setProcessor(blob);
        }
        mnuMono.setEnabled(sel);
        mnuBlob.setEnabled(sel);
    }//GEN-LAST:event_mnuProcBlobItemStateChanged

    private void mnuInfoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuInfoItemStateChanged
        config.drawInfo = evt.getStateChange() == ItemEvent.SELECTED;
    }//GEN-LAST:event_mnuInfoItemStateChanged

    private void mnuMonoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuMonoItemStateChanged
        config.drawMono = evt.getStateChange() == ItemEvent.SELECTED;
        blob.setGenerateMono(config.drawMono);
    }//GEN-LAST:event_mnuMonoItemStateChanged

    private void mnuBlobItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuBlobItemStateChanged
        config.drawBlob = evt.getStateChange() == ItemEvent.SELECTED;
    }//GEN-LAST:event_mnuBlobItemStateChanged

    private void chkAdvancedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkAdvancedItemStateChanged
        setAdvanced(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_chkAdvancedItemStateChanged

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        dlgSess.dispose();
        video.stop();
        video.setRawMode((Encoding)cboRaw.getSelectedItem())
                .setWidth(grcRes.getWidthValue())
                .setHeight(grcRes.getHeightValue())
                .setSensorMode((int)cboSensor.getSelectedItem())
                .setShutterSpeed(Math.round(vsShutter.getValue() * 1000)) // mini -> micro
                .setFrameRate(vsFramerate.getRound())
                .clearCalibration();
        newSession();
    }//GEN-LAST:event_btnStartActionPerformed

    public static void main(String[] args) {
        int verbose = 3;
        int width = 640;
        int height = 480;
        Encoding raw = Encoding.I420;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--width":
                case "-w":
                    width = Integer.parseInt(args[++i]);
                    break;
                    
                case "--height":
                case "-h":
                    height = Integer.parseInt(args[++i]);
                    break;
                    
                case "-bgr":
                    raw = Encoding.BGR24;
                    break;
                    
                case "-rgb":
                    raw = Encoding.RGB16;
                    break;
                    
                case "--verbose":
                case "-vb":
                    verbose = Integer.parseInt(args[i++]);
                    break;
            }
        }
        VideoPlay gui = new VideoPlay(width, height, raw);
        gui.video.setVerbose(verbose);
        gui.setVisible(true);
        gui.startVideo();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox<com.openitvn.picam.Encoding> cboRaw;
    private javax.swing.JComboBox<Integer> cboSensor;
    private javax.swing.JCheckBox chkAdvanced;
    private javax.swing.JDialog dlgSess;
    private com.openitvn.picam.gui.Resolution grcRes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JLabel lblSensor;
    private javax.swing.JLabel lblShutter;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JCheckBoxMenuItem mnuBlob;
    private javax.swing.JMenuItem mnuConfig;
    private javax.swing.JMenuItem mnuContent;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JCheckBoxMenuItem mnuInfo;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JCheckBoxMenuItem mnuMono;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JRadioButtonMenuItem mnuProcBlob;
    private javax.swing.JRadioButtonMenuItem mnuProcCalib;
    private javax.swing.JRadioButtonMenuItem mnuProcVideo;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenu mnuTask;
    private javax.swing.ButtonGroup procGroup;
    private javax.swing.JPanel viewport;
    private com.openitvn.control.UCValueSlider vsFramerate;
    private com.openitvn.control.UCValueSlider vsShutter;
    // End of variables declaration//GEN-END:variables
}
