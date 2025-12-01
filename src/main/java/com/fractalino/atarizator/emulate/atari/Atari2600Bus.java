/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.atari;

import com.fractalino.atarizator.emulate.Bus;
import com.fractalino.atarizator.emulate.Memory;
import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class Atari2600Bus implements Bus {
    private final Atari2600TIA tia;
    private final Atari2600RIOT riot;
    private final Memory8 cartridge; // The ROM
    
    private static final int MAX_CARTRIDGE_DIM = 4096;

    public Atari2600Bus() {
        this.tia = new Atari2600TIA();
        this.riot = new Atari2600RIOT();
        
        this.cartridge = new Memory8(MAX_CARTRIDGE_DIM);
    }
    
    public void setCartridge(byte[] romData) {
        for(int i = 0; i < romData.length && i < 4096; i++) {
             this.cartridge.store(i, romData[i]);
        }
    }

    @Override
    public int load(int addr) {
        // The 6507 only has 13 address lines (A0-A12). Handle this:
        addr &= 0x1FFF;

        // A12 = 1? It's the Cartridge ($1000 - $1FFF)
        if ((addr & 0x1000) != 0) {
            return cartridge.load(addr & 0x0FFF);
        }

        // A12 = 0. It's system memory.
        
        // A7 = 0 and A9 = 0? TIA ($0000 - $007F)
        if ((addr & 0x0080) == 0) {
            return tia.read(addr & 0x3F); // TIA is mirrored every 64 bytes
        }

        // A9 = 1? RIOT I/O and Timer ($0200 - $02FF)
        if ((addr & 0x0200) != 0) {
             return riot.readIO(addr);
        }
        
        // Other: RIOT RAM ($0080 - $00FF)
        return riot.readRAM(addr);
    }

    @Override
    public void store(int addr, int val) {
        addr &= 0x1FFF;

        // ROM Write? Illegal.
        if ((addr & 0x1000) != 0) {
            return; 
        }

        // TIA Write ($00 - $7F)
        if ((addr & 0x0080) == 0) {
            tia.write(addr & 0x3F, val);
            return;
        }

        // RIOT I/O ($0200+)
        if ((addr & 0x0200) != 0) {
            riot.writeIO(addr, val);
            return;
        }

        // RIOT RAM ($80 - $FF)
        riot.writeRAM(addr, val);
    }
    
    @Override
    public int loadWord(int addr) {
        int l = load(addr);
        int h = load(addr + 1);
        return l | (h << 8);
    }
    
    public int loadWordZpBug(int addr) {
        int l = load(addr);
        int h = load((addr & 0xFF) == 0xFF ? (addr & 0xFF00) : addr + 1);
        
        return l | (h << 8);
    }
    
    public Atari2600RIOT getRIOT() {
        return riot;
    }
    
    public Atari2600TIA getTIA() {
        return tia;
    }

    @Override
    public Memory.MemoryRecord[] enumMemory() {
        return new Memory.MemoryRecord[] {
            new Memory.MemoryRecord(cartridge, "Cartridge"),
            new Memory.MemoryRecord(riot.getMemory(), "RIOT")
        };
    }
}
