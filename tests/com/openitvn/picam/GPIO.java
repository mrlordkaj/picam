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
package com.openitvn.picam;

/**
 *
 * @author Thinh Pham
 */
public  class GPIO {
    // pin modes
    public static final int INPUT               = 0;
    public static final int OUTPUT              = 1;
    public static final int PWM_OUTPUT          = 2;
    public static final int GPIO_CLOCK          = 3;
    public static final int SOFT_PWM_OUTPUT     = 4;
    public static final int SOFT_TONE_OUTPUT    = 5;
    public static final int PWM_TONE_OUTPUT     = 6;
    public static final int LOW                 = 0;
    public static final int HIGH                = 1;
    // interrupt levels
    public static final int INT_EDGE_SETUP	= 0;
    public static final int INT_EDGE_FALLING    = 1;
    public static final int INT_EDGE_RISING	= 2;
    public static final int INT_EDGE_BOTH	= 3;

    public static native int setup();
    public static native void pinMode(int pin, int mode);
    public static native void digitalWrite(int pin, int value);
    
    private static boolean initialized;
    
    public static boolean init() {
        if (!initialized)
            initialized = (setup() == INT_EDGE_SETUP);
        return initialized;
    }
}
