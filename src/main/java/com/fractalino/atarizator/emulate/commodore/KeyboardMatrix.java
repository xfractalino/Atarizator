/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.commodore;

import com.fractalino.atarizator.emulate.Peripheral;
import com.fractalino.atarizator.gui.KeyboardAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.lwjgl.glfw.GLFW.*; // We use GLFW specific keys

/**
 *
 * @author fractalino
 */
class KeyboardMatrix implements Peripheral, KeyboardAdapter {
    
    private final AtomicIntegerArray kbMatrix = new AtomicIntegerArray(8 * 8);
    
    private final C64Bus bus;
    
    private static final int[] keyMapDef = new int[] {
        // Letters
        GLFW_KEY_A,1,2,
        GLFW_KEY_B,3,4,
        GLFW_KEY_C,2,4,
        GLFW_KEY_D,2,2,
        GLFW_KEY_E,1,6,
        GLFW_KEY_F,2,5,
        GLFW_KEY_G,3,2,
        GLFW_KEY_H,3,5,
        GLFW_KEY_I,4,1,
        GLFW_KEY_J,4,2,
        GLFW_KEY_K,4,5,
        GLFW_KEY_L,5,2,
        GLFW_KEY_M,4,4,
        GLFW_KEY_N,4,7,
        GLFW_KEY_O,4,6,
        GLFW_KEY_P,5,1,
        GLFW_KEY_Q,7,6,
        GLFW_KEY_R,2,1,
        GLFW_KEY_S,1,5,
        GLFW_KEY_T,2,6,
        GLFW_KEY_U,3,6,
        GLFW_KEY_V,3,7,
        GLFW_KEY_W,1,1,
        GLFW_KEY_X,2,7,
        GLFW_KEY_Y,3,1,
        GLFW_KEY_Z,1,4,
        
        // Numbers
        GLFW_KEY_0,4,3,
        GLFW_KEY_1,7,0,
        GLFW_KEY_2,7,2,
        GLFW_KEY_3,1,0,
        GLFW_KEY_4,1,3,
        GLFW_KEY_5,2,0,
        GLFW_KEY_6,2,3,
        GLFW_KEY_7,3,0,
        GLFW_KEY_8,3,3,
        GLFW_KEY_9,4,0,
        
        // Mapped to STOP
        GLFW_KEY_GRAVE_ACCENT,7,7, 
        
        // C=
        GLFW_KEY_LEFT_CONTROL,7,5, 
        
        // Ctrl
        GLFW_KEY_TAB,7,2,
        
        // <-
        GLFW_KEY_ESCAPE,7,1,
        
        // /
        GLFW_KEY_SLASH,6,7,
        
        // Arrow up
        GLFW_KEY_BACKSLASH,6,6,
        
        // Shift
        GLFW_KEY_RIGHT_SHIFT,6,5,
        GLFW_KEY_LEFT_SHIFT,1,7,
        GLFW_KEY_CAPS_LOCK,1,7,
        
        // Home
        GLFW_KEY_F5,6,3,
        
        // ;
        GLFW_KEY_APOSTROPHE,6,2,
        
        // *
        GLFW_KEY_RIGHT_BRACKET,6,1,
        
        // Pound
        GLFW_KEY_F6,6,0,
        
        // ,
        GLFW_KEY_COMMA,5,7,
        GLFW_KEY_PERIOD,5,4,
        
        // @
        GLFW_KEY_LEFT_BRACKET,5,6,
        
        // :
        GLFW_KEY_SEMICOLON,5,5,
        
        // -+
        GLFW_KEY_EQUAL,5,3,
        GLFW_KEY_MINUS,5,0,
        
        GLFW_KEY_DOWN,0,7,
        GLFW_KEY_LEFT,0,2,
        GLFW_KEY_ENTER,0,1,
        GLFW_KEY_DELETE,0,0,
        
        // F
        GLFW_KEY_F1,0,4,
        GLFW_KEY_F2,0,5,
        GLFW_KEY_F3,0,6,
        GLFW_KEY_F4,0,3,
    };
    
    private static final Map<Integer, KeyCoord> keyMap = new ConcurrentHashMap<>();
    
    static {
        for(int i = 0; i < keyMapDef.length;) {
            int keyCode = keyMapDef[i++];
            int row     = keyMapDef[i++];
            int col     = keyMapDef[i++];
            
            keyMap.put(keyCode, new KeyCoord(row, col));
        }
    }
    
    KeyboardMatrix(C64Bus bus) {
        this.bus = bus;
        
        for(int i = 0; i < 8*8; i++)
            kbMatrix.set(i, 0x1);
    }

    @Override
    public void keyPressed(int key) {
        setKey(key, true);
    }

    @Override
    public void keyReleased(int key) {
        setKey(key, false);
    }
    
    private void setKey(int key, boolean down) {
        if(down && key == GLFW_KEY_BACKSPACE) {
            bus.nmi();
            return;
        }
        
        var coord = keyMap.get(key);
        
        if(coord == null) return;
        
        int c = coord.c;
        int r = coord.r;
        
        int lin = c*8 + r;
        
        if(down && key == GLFW_KEY_CAPS_LOCK) {
            int lock = kbMatrix.get(lin);
            
            kbMatrix.set(lin, (lock == 0)? 0x0 : 0x1);
        } else {
            kbMatrix.set(lin, down? 0x0 : 0x1);
        }
    }
    
    int readColumn(int c) {
        int ret = 0xFF;
        for(int i = 0; i < 8; i++) {
            if(kbMatrix.get(c * 8 + i) == 0) {
                ret &= ~(1 << i);
            }
        }
        return ret;
    }
    
    int readRow(int r) {
        int ret = 0xFF;
        for(int i = 0; i < 8; i++) {
            if(kbMatrix.get(i * 8 + r) == 0) {
                ret &= ~(1 << i);
            }
        }
        return ret;
    }
    
    private record KeyCoord(int r, int c) {}
    
}
