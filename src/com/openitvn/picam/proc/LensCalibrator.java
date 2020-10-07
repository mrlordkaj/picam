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
package com.openitvn.picam.proc;

import com.openitvn.picam.PicamConfig;
import com.openitvn.picam.PicamVideo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import com.openitvn.picam.VideoProcessor;

/**
 *
 * @author Thinh Pham
 */
public final class LensCalibrator extends AbstractTableModel implements VideoProcessor {
    
    private final long ptr;
    private native long _create();
    private native void _destroy(long ptr);
    private native void _startCalibration(long ptr, int pattern, int width, int height, int snapshot, double[] intrinsic, int flags);
    private native int _getFlags(long ptr);
    private native int _getBoardType(long ptr);
    private native int _getBoardCols(long ptr);
    private native int _getBoardRows(long ptr);
    private native int _getSnapshotLimit(long ptr);
    private native int _getSnapshotCount(long ptr);
    private native int _getFrameData(long ptr, byte[] dst);
    
    public static final String[] COLUMNS = { "", "Title", "Errors" };
    public static final int COL_ACTIVE = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_ERROR = 2;
    
    public static final int CALIB_USE_INTRINSIC_GUESS = 1,
                            CALIB_FIX_ASPECT_RATIO    = 2,
                            CALIB_FIX_PRINCIPAL_POINT = 4,
                            CALIB_ZERO_TANGENT_DIST   = 8,
                            CALIB_FIX_FOCAL_LENGTH    = 16;
    
    private static final BasicStroke LINE_EDGE_STROKE = new BasicStroke(2);
    private static final BasicStroke RING_EDGE_STROKE = new BasicStroke(6);
    private static final Color       RING_EDGE_COLOR  = new Color(0, 1, 0, 0.6f);
    private static final Color       RING_FILL_COLOR  = new Color(1, 1, 1, 0.4f);
    private static final int         RING_RADIUS      = 30;
    
    private boolean started = false;
    private final Rectangle ringIn = new Rectangle();
    private final Rectangle ringOut = new Rectangle();
    private int steadyAngle;
    private boolean needUpdate = true;
    private BufferedImage frameImg;
    private float[] cornerData = new float[0];
    private int cx, cy;
    
    private final ArrayList<LensEntry> entries = new ArrayList<>();
    
    public LensCalibrator() {
        ptr = _create();
    }
    
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    public void finalize() throws Throwable {
        _destroy(ptr);
        super.finalize();
    }
    
    @Override
    public long getPointer() {
        return ptr;
    }
    
    @Override
    public void videoStarted(PicamVideo vid) {
        if (vid != null) {
            int w = vid.getWidth();
            int h = vid.getHeight();
            switch (vid.getRawMode()) {
                case I420:
                    frameImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                    break;
                case RGB16:
                    frameImg = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_565_RGB);
                    break;
                case BGR24:
                    frameImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
                    break;
            }
            needUpdate = true;
            cx = w / 2;
            cy = h / 2;
            ringIn.setRect(cx-RING_RADIUS+3, cy-RING_RADIUS+3, RING_RADIUS*2-6, RING_RADIUS*2-6);
            ringOut.setRect(cx-RING_RADIUS, cy-RING_RADIUS, RING_RADIUS*2, RING_RADIUS*2);
        }
    }
    
    @Override
    public void videoStopped(PicamVideo vid) {
        frameImg = null;
    }
    
    @Override
    public void frameChanged(PicamVideo vid) {
        
    }
    
    @Override
    public void paint(PicamVideo vid, Graphics2D g) {
        boolean calib = vid.isCalibrated();
        if (needUpdate) {
            // TODO: short[] in case of RGB16
            byte[] data = ((DataBufferByte)frameImg.getRaster().getDataBuffer()).getData();
            _getFrameData(ptr, data);
            needUpdate = false;
        }
        g.drawImage(frameImg, 0, 0, null);
        int cornerCount = cornerData.length >> 1;
        if (!calib && cornerCount > 0) {
            g.setStroke(LINE_EDGE_STROKE);
            int a = 5;
            int b = 10;
            for (int i = 0; i < cornerCount; i++) {
                int pos = i*2;
                int x = (int)cornerData[pos];
                int y = (int)cornerData[pos+1];
                if (i == 0)
                    g.setColor(Color.GREEN);
                else if (i == cornerCount - 1)
                    g.setColor(Color.RED);
                else
                    g.setColor(Color.YELLOW);
                g.drawOval(x-a, y-a, b, b);
            }
            if (0 < steadyAngle && steadyAngle < 360) {
                g.setStroke(RING_EDGE_STROKE);
                g.setPaint(RING_EDGE_COLOR);
                g.drawOval(ringOut.x, ringOut.y, ringOut.width, ringOut.height);
                g.setPaint(RING_FILL_COLOR);
                g.fillArc(ringIn.x, ringIn.y, ringIn.width, ringIn.height, 90, -steadyAngle);
            }
        }
        int top = 0;
        int left = 8;
        if (calib) {
            if (PicamConfig.drawInfo) {
                LensEntry e = getActiveEntry();
                g.setColor(Color.GREEN);
                g.drawString(String.format("Errors: %1$.8f", e.error), left, top += 20);
            }
        } else if (started) {
            BoardPattern pat = BoardPattern.fromId(_getBoardType(ptr));
            g.setColor(Color.RED);
            g.drawString(String.format("Recognitizing %1$s %2$d x %3$d", pat.title, pat.width, pat.height), left, top += 20);
            g.drawString(String.format("Snapshots: %1$d / %2$d", _getSnapshotCount(ptr), _getSnapshotLimit(ptr)), left, top += 20);
        }
    }
    
    public LensCalibrator startCalibration(BoardPattern pat, int snapshot, double[] intrinsic, int flags) throws IllegalArgumentException {
        _startCalibration(ptr, pat.type, pat.width, pat.height, snapshot, intrinsic, flags);
        started = true;
        return this;
    }
    
    /**
     * Callback from native when the lens is calibrated.
     */
    void finishCalibration(double[] camData, double[] distData, double error) {
        LensEntry e = new LensEntry();
        e.time = System.currentTimeMillis();
        e.pattern = getBoardPattern();
        e.flags = _getFlags(ptr);
        System.arraycopy(camData, 0, e.intrinsic, 0, 9);
        System.arraycopy(distData, 0, e.distCoeffs, 0, 5);
        e.error = error;
        int i = entries.size();
        entries.add(e);
        activeEntry(i);
    }
    
    /**
     * Sets position data of all corners when they is found.
     * Method called from native, do not modify.
     */
    synchronized void setCornerData(float[] cornerData, int steadyAngle) {
        this.cornerData = cornerData;
        this.steadyAngle = steadyAngle;
        this.needUpdate = true;
    }
    
    public BoardPattern getBoardPattern() {
        BoardPattern pat = BoardPattern.fromId(_getBoardType(ptr));
        pat.width = _getBoardCols(ptr);
        pat.height = _getBoardRows(ptr);
        return pat;
    }
    
    public int getSnapshotLimit() {
        return _getSnapshotLimit(ptr);
    }
    
    public int getSnapshotCount() {
        return _getSnapshotCount(ptr);
    }
    
    public int getFlags() {
        return _getFlags(ptr);
    }
    
    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_ACTIVE:
                return Boolean.class;
                
            case COL_TITLE:
                return String.class;
                
            case COL_ERROR:
                return Double.class;
        }
        return Object.class;
    }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        LensEntry e = entries.get(row);
        switch (col) {
            case COL_ACTIVE:
                return e.active;
                
            case COL_TITLE:
                return e.toString();
                
            case COL_ERROR:
                return e.error;
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return col == COL_ACTIVE || col == COL_TITLE;
    }
    
    @Override
    public void setValueAt(Object val, int row, int col) {
        switch (col) {
            case COL_ACTIVE:
                activeEntry(row);
                break;
                
            case COL_TITLE:
                entries.get(row).title = val.toString();
                break;
        }
    }
    
    public int addEntry(LensEntry entry) {
        int i = 0;
        for (LensEntry e : entries) {
            if (entry.equals(e)) {
                return i;
            }
            i++;
        }
        entries.add(entry);
        fireTableRowsInserted(i, i);
        return i;
    }
    
    public void removeEntry(int i) {
        if (entries.remove(i) != null) {
            fireTableRowsDeleted(i, i);
        }
    }
    
    public void clearEntries() {
        entries.clear();
        fireTableDataChanged();
    }
    
    public LensEntry getEntry(int i) {
        return entries.get(i);
    }
    
    public void activeEntry(int i) {
        LensEntry entry = entries.get(i);
        if (!entry.active) {
            for (LensEntry e : entries) {
                e.active = false;
            }
            entry.active = true;
            fireTableRowsUpdated(0, entries.size());
        }
    }
    
    public LensEntry getActiveEntry() {
        for (LensEntry e : entries) {
            if (e.active) {
                return e;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Lens Calibrator";
    }
}