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
package com.openitvn.picam.gui;

import com.openitvn.picam.PicamConfig;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author Thinh Pham
 */
public class About extends javax.swing.JDialog implements HyperlinkListener {
    
    private final String ver;
    private Image img;
    
    public About(java.awt.Frame parent) {
        super(parent, true);
        ver = String.format("v%1$s - %2$s %3$s%4$s",
                PicamConfig.APP_VERSION,
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
        try {
            img = ImageIO.read(getClass()
                    .getResourceAsStream("/com/openitvn/picam/img/splash.jpg"));
        } catch (IOException | IllegalArgumentException ex) { }
        initComponents();
    }
    
    private void paintViewport(Graphics g) {
        g.drawImage(img, 0, 0, null);
        viewport.paintComponents(g);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewport = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                paintViewport(g);
            }
        };
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDesc = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        viewport.setPreferredSize(new java.awt.Dimension(400, 300));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setText(com.openitvn.picam.PicamConfig.APP_NAME);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setText(ver);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel5.setText("Software is up to date.");

        txtDesc.setEditable(false);
        txtDesc.setContentType("text/html"); // NOI18N
        txtDesc.setText("<html>\n<body>\nCopyright © 2017 Thinh Pham.<br/>\n<br/>\nThis software has been released under the GNU General<br/>\nPublic License version 3 with native components exception.<br/>\nFor more information, please visit <a title=\"Our Website\" href=\"http://unisoft.org\">our website</a>.\n</body>\n</html>");
        txtDesc.setOpaque(false);
        txtDesc.addHyperlinkListener(this);

        javax.swing.GroupLayout viewportLayout = new javax.swing.GroupLayout(viewport);
        viewport.setLayout(viewportLayout);
        viewportLayout.setHorizontalGroup(
            viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(viewportLayout.createSequentialGroup()
                        .addGroup(viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        viewportLayout.setVerticalGroup(
            viewportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(txtDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(viewport, java.awt.BorderLayout.CENTER);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        if (evt.getSource() == txtDesc) {
            About.this.txtDescHyperlinkUpdate(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void txtDescHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_txtDescHyperlinkUpdate
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(evt.getURL().toURI());
            } catch (URISyntaxException | IOException ex) { }
        }
    }//GEN-LAST:event_txtDescHyperlinkUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextPane txtDesc;
    private javax.swing.JPanel viewport;
    // End of variables declaration//GEN-END:variables
}
