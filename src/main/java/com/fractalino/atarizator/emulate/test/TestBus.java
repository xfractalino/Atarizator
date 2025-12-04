/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.test;

import com.fractalino.atarizator.emulate.Bus;

import com.fractalino.atarizator.emulate.Memory;
import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class TestBus implements Bus {
    
    private Memory8 mem = new Memory8(65536);

    @Override
    public int read(int addr) {
        return mem.read(addr);
    }

    @Override
    public void write(int addr, int val) {
        mem.write(addr, val);
    }

    @Override
    public Memory.MemoryRecord[] enumMemory() {
        return new Memory.MemoryRecord[]{ 
            new Memory.MemoryRecord(mem, "Test Memory") 
        };
    }
    
}
