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
import com.fractalino.atarizator.gui.KeyboardAdapter;

import java.util.stream.IntStream;

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
    
    private final KeyboardMatrix kb = new KeyboardMatrix();

    @Override
    public int read(int addr) {
        return switch(addr) {
            case PRA -> { yield IntStream.range(0, 8)
                    .map(i -> kb.readColumn(outLatch[PRA] & (1 << i)))
                    .reduce(0, (a, b) -> a | b); }
            case PRB -> { yield IntStream.range(0, 8)
                    .map(i -> kb.readRow(outLatch[PRA] & (1 << i)))
                    .reduce(0, (a, b) -> a | b); }
            default ->  { yield registers.read(addr); }
        };
    }
    
    @Override
    public void write(int addr, int v) {
        registers.write(addr, v);
        
        if(addr == PRA || addr == PRB) 
            outLatch[addr] = v;
    }
    
    @Override
    public void tick() {
        
    }
}
