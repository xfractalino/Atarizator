package com.fractalino.atarizator.emulate.commodore;

import com.fractalino.atarizator.emulate.Device;
import com.fractalino.atarizator.emulate.Memory8;
import com.fractalino.atarizator.gui.KeyboardAdapter;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 *
 * @author fractalino
 */
public class CIA implements Device, KeyboardAdapter {
    
    private AtomicIntegerArray kbMatrix = new AtomicIntegerArray(8 * 8);
    
    public final int PRA  = 0x0;
    public final int PRB  = 0x1;
    public final int DDRA = 0x2;
    public final int DDRB = 0x3;
    public final int TALO = 0x4;
    public final int TAHI = 0x5;
    public final int TBLO = 0x6;
    public final int TBHI = 0x7;
    
    public final int TOD10THS = 0x8;
    public final int TODSEC   = 0x9;
    public final int TODMIN   = 0xA;
    public final int TODHR    = 0xB;
    
    public final int SDR = 0xC;
    public final int ICR = 0xD;
    public final int CRA = 0xE;
    public final int CRB = 0xF;
    
    public final int INPUT = 0x0;
    public final int OUTPUT = 0x0;
    
    private final Memory8 registers = new Memory8(16);
    private final int[] outLatch = new int[2];

    @Override
    public int read(int addr) {
        return switch(addr) {
            case PRA -> { yield IntStream.range(0, 8)
                    .map(i -> readKbColumn(outLatch[PRA] & (1 << i)))
                    .reduce(0, (a, b) -> a | b); }
            case PRB -> { yield IntStream.range(0, 8)
                    .map(i -> readKbRow(outLatch[PRA] & (1 << i)))
                    .reduce(0, (a, b) -> a | b); }
            default ->  { yield registers.read(addr); }
        };
    }

    private int readKbColumn(int c) {
        int ret = 0x0;
        for(int i = 0; i < 8; i++) {
            ret |= (kbMatrix.get(c * 8 + i) == 1) ? (1 << i) : 0x0;
        }
        return ret;
    }
    
    private int readKbRow(int r) {
        int ret = 0x0;
        for(int i = 0; i < 8; i++) {
            ret |= (kbMatrix.get(i * 8 + r) == 1) ? (1 << i) : 0x0;
        }
        return ret;
    }
    
    @Override
    public void write(int addr, int v) {
        registers.write(addr, v);
        
        if(addr == PRA || addr == PRB) 
            outLatch[addr] = v;
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
        switch(key) {
            // TODO
        }
    }
    
    @Override
    public void tick() {
        
    }
    
}
