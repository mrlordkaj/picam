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
package com.openitvn.picam.proc;

import com.openitvn.picam.Parameter;
import com.openitvn.picam.PicamVideo;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public final class ParamEntry {
    
    public boolean active;
    public String title;
    public int iso = 500;
    public Parameter imxfx = Parameter.MMAL_PARAM_IMAGEFX_NONE;
    public int sharpness = 0;
    public int brightness = 50;
    public int contrast = 0;
    public final ArrayList<BlobFilter> filters = new ArrayList<>();
    
    public ParamEntry() {
        filters.add(new BlobFilter(BlobFilter.Type.BrightnessMin, true, 128));
        filters.add(new BlobFilter(BlobFilter.Type.BrightnessMax, true, 255));
    }
    
    public BlobFilter getFilter(BlobFilter.Type type) {
        for (BlobFilter i : filters) {
            if (i.type == type) {
                return i;
            }
        }
        BlobFilter f = new BlobFilter(type);
        filters.add(f);
        return f;
    }
    
    public BlobFilter getFilter(String type) {
        return getFilter(BlobFilter.Type.fromCode(type));
    }
    
    @Override
    public String toString() {
        return title == null ? "Untitled" : title;
    }
    
    public void applyTo(PicamVideo vid) {
        vid.setISO(iso);
        vid.setImageFx(imxfx);
        vid.setSharpness(sharpness);
        vid.setContrast(contrast);
        vid.setBrightness(brightness);
    }
}