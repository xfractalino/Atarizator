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
    
    @Override
    public void write(int addr, int v) {
        mm[addr % cap] = (byte) (v & 0xFF);
    }
    
    @Override
    public int read(int addr) {
        return mm[addr % cap] & 0xFF;
    }
    
    /**
     * Little endian load word.
     * 
     * @param addr
     * @return 
     */
    public int readWordLE(int addr) {
        int LL = read(addr);
        int HH = read(addr + 1);
        
        return LL + (HH << 8);
    }
    
    public int readWordZpBug(int addr) {
        int LL = read(addr);
        int HH = read((addr & 0xFF) == 0xFF ? 0x00 : addr + 1);
        
        return LL + (HH << 8);
    }
    
    @Override
    public int getCapacity() {
        return cap;
    }

    @Override public void tick() {}
}
