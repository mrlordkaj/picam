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
package com.openitvn.picam;

import java.util.ArrayList;

/**
 *
 * @author Thinh
 */
public enum Parameter {
    
    // MMAL_VIDEO_PROFILE_T
    MMAL_VIDEO_PROFILE_H264_BASELINE    (131074, 25, "H264 Baseline"),
    MMAL_VIDEO_PROFILE_H264_MAIN        (131074, 26, "H264 Main"),
    MMAL_VIDEO_PROFILE_H264_HIGH        (131074, 27, "H264 High"),
    
    // MMAL_VIDEO_LEVEL_T
    // TODO: level is not a standalone parameter, belong to profile
    MMAL_VIDEO_LEVEL_H264_4     (131125, 28, "H264 4"),
    MMAL_VIDEO_LEVEL_H264_41    (131125, 29, "H264 4.1"),
    MMAL_VIDEO_LEVEL_H264_42    (131125, 30, "H264 4.2"),
    
    // MMAL_VIDEO_INTRA_REFRESH_T
    MMAL_VIDEO_INTRA_REFRESH_CYCLIC         (131084, 0, "Cyclic"),
    MMAL_VIDEO_INTRA_REFRESH_ADAPTIVE       (131084, 1, "Adaptive"),
    MMAL_VIDEO_INTRA_REFRESH_BOTH           (131084, 2, "Both"),
    MMAL_VIDEO_INTRA_REFRESH_CYCLIC_MROWS   (131084, 0x7F000001, "Cyclicrows"),
//    MMAL_VIDEO_INTRA_REFRESH_DUMMY          (131084, -1, "Dummy"),
    
    // MMAL_PARAM_EXPOSUREMODE_T
    MMAL_PARAM_EXPOSUREMODE_OFF             (65554, 0, "Off"),
    MMAL_PARAM_EXPOSUREMODE_AUTO            (65554, 1, "Auto"),
    MMAL_PARAM_EXPOSUREMODE_NIGHT           (65554, 2, "Night"),
    MMAL_PARAM_EXPOSUREMODE_NIGHTPREVIEW    (65554, 3, "Night preview"),
    MMAL_PARAM_EXPOSUREMODE_BACKLIGHT       (65554, 4, "Backlight"),
    MMAL_PARAM_EXPOSUREMODE_SPOTLIGHT       (65554, 5, "Spotlight"),
    MMAL_PARAM_EXPOSUREMODE_SPORTS          (65554, 6, "Sports"),
    MMAL_PARAM_EXPOSUREMODE_SNOW            (65554, 7, "Snow"),
    MMAL_PARAM_EXPOSUREMODE_BEACH           (65554, 8, "Beach"),
    MMAL_PARAM_EXPOSUREMODE_VERYLONG        (65554, 9, "Very long"),
    MMAL_PARAM_EXPOSUREMODE_FIXEDFPS        (65554, 10, "Fixed FPS"),
    MMAL_PARAM_EXPOSUREMODE_ANTISHAKE       (65554, 11, "Antishake"),
    MMAL_PARAM_EXPOSUREMODE_FIREWORKS       (65554, 12, "Fireworks"),
    
    // MMAL_PARAM_EXPOSUREMETERINGMODE_T
    MMAL_PARAM_EXPOSUREMETERINGMODE_AVERAGE (65555, 0, "Average"),
    MMAL_PARAM_EXPOSUREMETERINGMODE_SPOT    (65555, 1, "Spot"),
    MMAL_PARAM_EXPOSUREMETERINGMODE_BACKLIT (65555, 2, "Backlight"),
    MMAL_PARAM_EXPOSUREMETERINGMODE_MATRIX  (65555, 3, "Matrix"),
    
    // MMAL_PARAMETER_DRC_STRENGTH_T
    MMAL_PARAMETER_DRC_STRENGTH_OFF     (65578, 0, "Off"),
    MMAL_PARAMETER_DRC_STRENGTH_LOW     (65578, 1, "Low"),
    MMAL_PARAMETER_DRC_STRENGTH_MEDIUM  (65578, 2, "Medium"),
    MMAL_PARAMETER_DRC_STRENGTH_HIGH    (65578, 3, "High"),
    
    // MMAL_PARAM_FLICKERAVOID_T
    MMAL_PARAM_FLICKERAVOID_OFF     (65544, 0, "Off"),
    MMAL_PARAM_FLICKERAVOID_AUTO    (65544, 1, "Auto"),
    MMAL_PARAM_FLICKERAVOID_50HZ    (65544, 2, "50Hz"),
    MMAL_PARAM_FLICKERAVOID_60HZ    (65544, 3, "60Hz"),
    
    // MMAL_PARAM_AWBMODE_T
    MMAL_PARAM_AWBMODE_OFF          (65541, 0, "Off"),
    MMAL_PARAM_AWBMODE_AUTO         (65541, 1, "Auto"),
    MMAL_PARAM_AWBMODE_SUNLIGHT     (65541, 2, "Sunlight"),
    MMAL_PARAM_AWBMODE_CLOUDY       (65541, 3, "Cloudy"),
    MMAL_PARAM_AWBMODE_SHADE        (65541, 4, "Shade"),
    MMAL_PARAM_AWBMODE_TUNGSTEN     (65541, 5, "Tungsten"),
    MMAL_PARAM_AWBMODE_FLUORESCENT  (65541, 6, "Fluorescent"),
    MMAL_PARAM_AWBMODE_INCANDESCENT (65541, 7, "Incandescent"),
    MMAL_PARAM_AWBMODE_FLASH        (65541, 8, "Flash"),
    MMAL_PARAM_AWBMODE_HORIZON      (65541, 9, "Horizon"),
    
    // MMAL_PARAM_IMAGEFX_T
    MMAL_PARAM_IMAGEFX_NONE                 (65542, 0, "None"),
    MMAL_PARAM_IMAGEFX_NEGATIVE             (65542, 1, "Negative"),
    MMAL_PARAM_IMAGEFX_SOLARIZE             (65542, 2, "Solarize"),
//    MMAL_PARAM_IMAGEFX_POSTERIZE            (65542, 3, "Posterize"),
//    MMAL_PARAM_IMAGEFX_WHITEBOARD           (65542, 4, "Whiteboard"),
//    MMAL_PARAM_IMAGEFX_BLACKBOARD           (65542, 5, "Blackboard"),
    MMAL_PARAM_IMAGEFX_SKETCH               (65542, 6, "Sketch"),
    MMAL_PARAM_IMAGEFX_DENOISE              (65542, 7, "Denoise"),
    MMAL_PARAM_IMAGEFX_EMBOSS               (65542, 8, "Emboss"),
    MMAL_PARAM_IMAGEFX_OILPAINT             (65542, 9, "Oil Paint"),
    MMAL_PARAM_IMAGEFX_HATCH                (65542, 10, "Hatch"),
    MMAL_PARAM_IMAGEFX_GPEN                 (65542, 11, "Graphite Pen"),
    MMAL_PARAM_IMAGEFX_PASTEL               (65542, 12, "Pastel"),
    MMAL_PARAM_IMAGEFX_WATERCOLOUR          (65542, 13, "Water Colour"),
    MMAL_PARAM_IMAGEFX_FILM                 (65542, 14, "Film"),
    MMAL_PARAM_IMAGEFX_BLUR                 (65542, 15, "Blur"),
    MMAL_PARAM_IMAGEFX_SATURATION           (65542, 16, "Saturation"),
    MMAL_PARAM_IMAGEFX_COLOURSWAP           (65542, 17, "Colour Swap"),
    MMAL_PARAM_IMAGEFX_WASHEDOUT            (65542, 18, "Washed Out"),
    MMAL_PARAM_IMAGEFX_POSTERISE            (65542, 19, "Posterise"),
    MMAL_PARAM_IMAGEFX_COLOURPOINT          (65542, 20, "Colour Point"),
    MMAL_PARAM_IMAGEFX_COLOURBALANCE        (65542, 21, "Colour Balance"),
    MMAL_PARAM_IMAGEFX_CARTOON              (65542, 22, "Cartoon"),
    MMAL_PARAM_IMAGEFX_DEINTERLACE_DOUBLE   (65542, 23, "Deinterlace Double"),
    MMAL_PARAM_IMAGEFX_DEINTERLACE_ADV      (65542, 24, "Deinterlace Advanced"),
    MMAL_PARAM_IMAGEFX_DEINTERLACE_FAST     (65542, 25, "Deinterlace Fast"),
    
    ;
        
    public static final int MMAL_PARAMETER_AWB_MODE                     = 65541,
                            MMAL_PARAMETER_IMAGE_EFFECT                 = 65542,
                            MMAL_PARAMETER_COLOUR_EFFECT                = 65543,
                            MMAL_PARAMETER_FLICKER_AVOID                = 65544,
                            MMAL_PARAMETER_EXPOSURE_COMP                = 65549,
                            MMAL_PARAMETER_EXPOSURE_MODE                = 65554,
                            MMAL_PARAMETER_EXP_METERING_MODE            = 65555,
                            MMAL_PARAMETER_VIDEO_STABILISATION          = 65565,
                            MMAL_PARAMETER_DYNAMIC_RANGE_COMPRESSION    = 65578,
                            MMAL_PARAMETER_SHARPNESS                    = 65580,
                            MMAL_PARAMETER_CONTRAST                     = 65581,
                            MMAL_PARAMETER_BRIGHTNESS                   = 65582,
                            MMAL_PARAMETER_SATURATION                   = 65583,
                            MMAL_PARAMETER_ISO                          = 65584,
                            MMAL_PARAMETER_CUSTOM_AWB_GAINS             = 65604;
    
    public static final int MMAL_PARAMETER_PROFILE = 131074,
                            MMAL_PARAMETER_VIDEO_INTRA_REFRESH = 131084;
    
    public static Parameter getAWB(int id) {
        return fromId(MMAL_PARAMETER_AWB_MODE, id);
    }
    
    public static Parameter getImageFx(int id) {
        return fromId(MMAL_PARAMETER_IMAGE_EFFECT, id);
    }
    
    public static Parameter getFlicker(int id) {
        return fromId(MMAL_PARAMETER_FLICKER_AVOID, id);
    }
    
    public static Parameter getExposure(int id) {
        return fromId(MMAL_PARAMETER_EXPOSURE_MODE, id);
    }
    
    public static Parameter getMetering(int id) {
        return fromId(MMAL_PARAMETER_EXP_METERING_MODE, id);
    }
    
    public static Parameter getDRC(int id) {
        return fromId(MMAL_PARAMETER_DYNAMIC_RANGE_COMPRESSION, id);
    }
    
    public static Parameter fromId(int type, int id) {
        for (Parameter p : values()) {
            if (p.type == type && p.id == id)
                return p;
        }
        return null;
    }
    
    public static Parameter[] getAWBList() {
        return getParameterList(MMAL_PARAMETER_AWB_MODE);
    }
    
    public static Parameter[] getImageFxList() {
        return getParameterList(MMAL_PARAMETER_IMAGE_EFFECT);
    }
    
    public static Parameter[] getFlickerList() {
        return getParameterList(MMAL_PARAMETER_FLICKER_AVOID);
    }
    
    public static Parameter[] getExposureList() {
        return getParameterList(MMAL_PARAMETER_EXPOSURE_MODE);
    }
    
    public static Parameter[] getMeteringList() {
        return getParameterList(MMAL_PARAMETER_EXP_METERING_MODE);
    }
    
    public static Parameter[] getDRCList() {
        return getParameterList(MMAL_PARAMETER_DYNAMIC_RANGE_COMPRESSION);
    }
    
    public static Parameter[] getIntraRefreshList() {
        return getParameterList(MMAL_PARAMETER_VIDEO_INTRA_REFRESH);
    }
    
    public static Parameter[] getParameterList(int type) {
        ArrayList<Parameter> rs = new ArrayList<>();
        for (Parameter p : values()) {
            if (p.type == type)
                rs.add(p);
        }
        return rs.toArray(new Parameter[rs.size()]);
    }
    
    public final int type;
    public final int id;
    public final String text;
    
    private Parameter(int type, int id, String text) {
        this.type = type;
        this.id = id;
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
