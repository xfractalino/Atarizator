/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate;

/**
 *
 * @author fractalino
 */
public class Memory8 implements Memory {
    
    private final int cap;
    private final int reg;
    private final byte[] mm;
    
    public Memory8(int cap) {
        this(cap, 0);
    }
    
    public Memory8(int cap, int reg) {
        this.cap = cap;
        this.reg = reg;
        
        mm = new byte[cap];
    }
    
    public void store(int addr, int v) {
        mm[addr % cap] = (byte) (v & 0xFF);
    }
    
    @Override
    public int load(int addr) {
        return mm[addr % cap] & 0xFF;
    }
    
    /**
     * Little endian load word.
     * 
     * @param addr
     * @return 
     */
    public int loadWordLE(int addr) {
        int LL = load(addr);
        int HH = load(addr + 1);
        
        return LL + (HH << 8);
    }
    
    public int loadWordZpBug(int addr) {
        int LL = load(addr);
        int HH = load((addr & 0xFF) == 0xFF ? 0x00 : addr + 1);
        
        return LL + (HH << 8);
    }
    
    public int getCapacity() {
        return cap;
    }
}
