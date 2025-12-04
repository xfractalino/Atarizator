/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.commodore;

import com.fractalino.atarizator.emulate.Device;
import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class CIA implements Device {
    
    public static final int PRA  = 0x0;
    public static final int PRB  = 0x1;
    public static final int DDRA = 0x2;
    public static final int DDRB = 0x3;
    public static final int TALO = 0x4;
    public static final int TAHI = 0x5;
    public static final int TBLO = 0x6;
    public static final int TBHI = 0x7;
     
    public static final int TOD10THS = 0x8;
    public static final int TODSEC   = 0x9;
    public static final int TODMIN   = 0xA;
    public static final int TODHR    = 0xB;
     
    public static final int SDR = 0xC;
    public static final int ICR = 0xD;
    public static final int CRA = 0xE;
    public static final int CRB = 0xF;
     
    public static final int INPUT = 0x0;
    public static final int OUTPUT = 0x1;
    
    private final Memory8 registers = new Memory8(16);
    private final int[] outLatch = new int[2];
    
    private final C64Bus bus;
    private final KeyboardMatrix kb;
    
    private final CIAID id;
    
    CIA(C64Bus bus, CIAID id) {
        this.bus = bus;
        this.id  = id;
        this.kb  = new KeyboardMatrix(bus);
    }

    @Override
    public int read(int addr) {
        switch (addr) {
            case PRA -> {
                if(id == CIAID.CIA1) {
                    int act = outLatch[PRB] | ~registers.read(DDRB);
                    int ddr = registers.read(DDRA);
                    int ext = 0xFF;

                    for (int i = 0; i < 8; i++) {
                        if ((act & (1 << i)) == 0) {
                           ext &= kb.readColumn(i);
                        }
                    }

                    return (outLatch[PRA] & ddr) | (ext & ~ddr); 
                }
                
                
            }
            
            case PRB -> {
                if(id == CIAID.CIA1) {
                    int act = outLatch[PRA] | ~registers.read(DDRA);
                    int ddr = registers.read(DDRB);
                    int ext = 0xFF;

                    for (int i = 0; i < 8; i++) {
                        if ((act & (1 << i)) == 0) {
                           ext &= kb.readRow(i);
                        }
                    }

                    return (outLatch[PRB] & ddr) | (ext & ~ddr);
                }
            }
            
            default -> {
                return registers.read(addr);
            }
        }
        
        return 0xFF;
    }
    
    @Override
    public void write(int addr, int v) {
        v &= 0xFF; // Sanity check

        switch(addr) {
            case PRA: 
            case PRB:
                outLatch[addr] = v;

                
                // VIC bank switching? if (thisIsCIA2) bus.updateVICBank(v);
                break;

            case DDRA:
            case DDRB:
                registers.write(addr, v);
                break;

            case ICR: // $0D
                boolean set = (v & 0x80) != 0;
                int currentMask = registers.read(ICR);
                if (set) currentMask |= (v & 0x7F);
                else     currentMask &= ~(v & 0x7F);
                
                registers.write(ICR, currentMask);
                
                // checkInterrupts(); // TODO
                break;

            default:
                registers.write(addr, v);
                break;
        }
    }
    
    @Override
    public void tick() {
        
    }
    
    Memory8 getRegisters() {
        return registers;
    }
    
    enum CIAID { CIA1, CIA2 }
}
