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
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import com.openitvn.picam.VideoProcessor;

/**
 *
 * @author Thinh Pham
 */
public final class BlobDetector extends AbstractTableModel implements VideoProcessor {
    
    private static final int BLOB_RADIUS = 5;
    
    //<editor-fold desc="Native Access" defaultstate="collapsed">
    private native long _create();
    private native void _destroy(long ptr);
    private native boolean _isFilterEnabled(long ptr, int id);
    private native void _setFilterEnabled(long ptr, int id, boolean a);
    private native float _getFilterValue(long ptr, int id);
    private native void _setFilterValue(long ptr, int id, float v);
    private native int _getGrayData(long ptr, byte[] dst);
    private native int _getMonoData(long ptr, byte[] dst);
    private native void _setGenerateMono(long ptr, boolean b);
    
    public boolean isFilterEnabled(BlobFilter.Type t) {
        return _isFilterEnabled(ptr, t.id);
    }
    
    public BlobDetector setFilterEnabled(BlobFilter.Type t, boolean a) throws IllegalArgumentException {
        _setFilterEnabled(ptr, t.id, a);
        return this;
    }
    
    public float getFilterValue(BlobFilter.Type t) {
        return _getFilterValue(ptr, t.id);
    }
    
    public BlobDetector setFilterValue(BlobFilter.Type t, float v) throws IllegalArgumentException {
        _setFilterValue(ptr, t.id, v);
        return this;
    }
    
    public void setGenerateMono(boolean b) {
        _setGenerateMono(ptr, b);
    }
    //</editor-fold>
    
    // data
    private BufferedImage grayImg;
    private byte[] grayData;
    private float[] uvData;
    private float[] xyData;
    private final long ptr;
    
    public BlobDetector() {
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
        grayImg = new BufferedImage(vid.getWidth(), vid.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayData = ((DataBufferByte)grayImg.getRaster().getDataBuffer()).getData();
        uvData = new float[0];
        xyData = new float[0];
    }
    
    @Override
    public void videoStopped(PicamVideo vid) {
        grayImg = null;
        grayData = null;
    }
    
    @Override
    public void frameChanged(PicamVideo vid) {
    
    }
    
    @Override
    public void paint(PicamVideo vid, Graphics2D g) {
        if (PicamConfig.drawMono) {
            _getMonoData(ptr, grayData);
        } else {
            _getGrayData(ptr, grayData);
        }
        g.drawImage(grayImg, 0, 0, null);
        float[] xy = xyData; // avoid asynchonized modification
        float[] uv = uvData;
        int blobCount = xy.length >> 1;
        if (PicamConfig.drawInfo) {
            g.drawString(String.format("Blobs: %1$d", blobCount), 6, 20);
        }
        if (PicamConfig.drawBlob) {
            int size = BLOB_RADIUS << 1;
            int k = 0;
            for (int i = 0; i < blobCount; i++) {
                int u = (int) uv[k];
                int x = (int) xy[k++];
                int v = (int) uv[k];
                int y = (int) xy[k++];
                g.setStroke(new BasicStroke(2));
                // detected blobs
                g.setColor(Color.RED);
                g.drawOval(u - BLOB_RADIUS, v - BLOB_RADIUS, size, size);
                // undistorted blobs
                g.setColor(Color.GREEN);
                g.drawOval(x - BLOB_RADIUS, y - BLOB_RADIUS, size, size);
                // lines from detected to undistorted blobs
                g.setColor(Color.YELLOW);
                g.drawLine(u, v, x, y);
            }
        }
    }
    
    /**
     * Sets detected and undistorted position of markers.
     * This is a native method, do not modify.
     */
    private synchronized void setBlobData(float[] xyData, float[] uvData) {
        this.xyData = xyData;
        this.uvData = uvData;
    }
    
    public float[] getXYData() {
        return xyData;
    }
    
    public float[] getUVData() {
        return uvData;
    }
    
    public static final String[] COLUMNS = { "", "Title", "Color" };
    public static final int COL_ACTIVE = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_COLOR = 2;
    
    private final ArrayList<ParamEntry> entries = new ArrayList<>();
    
    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_ACTIVE:
                return Boolean.class;
                
            case COL_TITLE:
            case COL_COLOR:
                return String.class;
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        ParamEntry e = entries.get(row);
        switch (col) {
            case COL_ACTIVE:
                return e.active;
                
            case COL_TITLE:
                return e.toString();
                
            case COL_COLOR:
                BlobFilter f = e.getFilter(BlobFilter.Type.HueMid);
                return (f != null && f.active) ? f.value : "None";
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
    
    public int addEntry(ParamEntry entry) {
        int i = 0;
        for (ParamEntry e : entries) {
            if (entry.equals(e)) {
                return i;
            }
            i++;
        }
        entries.add(entry);
        fireTableRowsInserted(i, i);
        return i;
    }
    
    public void removeEntry(int index) {
        if (entries.remove(index) != null) {
            fireTableRowsDeleted(index, index);
        }
    }
    
    public void clearEntries() {
        entries.clear();
        fireTableDataChanged();
    }
    
    public ParamEntry getEntry(int index) {
        return entries.get(index);
    }
    
    public boolean activeEntry(int index) {
        if (index < entries.size()) {
            activeEntry(entries.get(index));
            return true;
        }
        return false;
    }
    
    public boolean activeEntry(String name) {
        for (ParamEntry e : entries) {
            if (name.equalsIgnoreCase(e.title)) {
                activeEntry(e);
                return true;
            }
        }
        return false;
    }
    
    private void activeEntry(ParamEntry entry) {
        if (!entry.active) {
            // apply filters
            for (BlobFilter f : entry.filters) {
                _setFilterEnabled(ptr, f.type.id, f.active);
                _setFilterValue(ptr, f.type.id, f.value);
            }
            // update table
            for (ParamEntry e : entries) {
                e.active = false;
            }
            entry.active = true;
            fireTableDataChanged();
        }
    }
    
    public ParamEntry getActiveEntry() {
        for (ParamEntry e : entries) {
            if (e.active) {
                return e;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Blob Detector";
    }
}