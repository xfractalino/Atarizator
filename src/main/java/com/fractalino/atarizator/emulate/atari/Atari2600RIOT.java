/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.atari;

import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class Atari2600RIOT {
    private final Memory8 ram = new Memory8(128);
    
    public int readRAM(int addr) {
        // RAM is at $0080-$00FF.
        return ram.load(addr & 0x7F);
    }
    
    public void writeRAM(int addr, int val) {
        ram.store(addr & 0x7F, val);
    }
    
    public int readIO(int addr) {
        // TODO: Timers and Joystick switches
        return 0;
    }
    
    public void writeIO(int addr, int val) {
        // TODO: Timer settings
    }
    
    public void tick(int cycles) {
        
    }
    
    public Memory8 getMemory() {
        return ram;
    }
}
