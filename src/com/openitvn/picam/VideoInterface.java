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
package com.openitvn.picam;

/**
 *
 * @author Thinh Pham
 */
public interface VideoInterface {
    
    int getWidth();
    VideoInterface setWidth(int v);
    
    int getHeight();
    VideoInterface setHeight(int v);
    
    Encoding getRawMode();
    VideoInterface setRawMode(Encoding v);
    
    int getFrameRate();
    VideoInterface setFrameRate(int v);
    
    int getShutterSpeed();
    VideoInterface setShutterSpeed(int v);
    
    int getSensorMode();
    VideoInterface setSensorMode(int v);
    
    int getISO();
    VideoInterface setISO(int v);
    
    int getEVComp();
    VideoInterface setEVComp(int v);
    
    boolean isVideoStab();
    VideoInterface setVideoStab(boolean v);
    
    int getSharpness();
    VideoInterface setSharpness(int v);
    
    int getContrast();
    VideoInterface setContrast(int v);
    
    int getBrightness();
    VideoInterface setBrightness(int v);
    
    int getSaturation();
    VideoInterface setSaturation(int v);
    
    Parameter getFlicker();
    VideoInterface setFlicker(Parameter v);
    
    Parameter getExposure();
    VideoInterface setExposure(Parameter v);
    
    Parameter getMeterMode();
    VideoInterface setMeterMode(Parameter v);
    
    Parameter getDRCLevel();
    VideoInterface setDRCLevel(Parameter v);
    
    Parameter getAWBMode();
    VideoInterface setAWBMode(Parameter v);
    
    ColorGain getAWBGains();
    VideoInterface setAWBGains(int r, int b);
    
    Parameter getImageFx();
    VideoInterface setImageFx(Parameter v);
    
    ColorGain getColorFx();
    VideoInterface setColorFx(boolean a, int u, int v);
}
