/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.test;

import com.fractalino.atarizator.emulate.Computer;
import com.fractalino.atarizator.emulate.MOS6502;
import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class TestComputer extends Computer<TestBus, MOS6502<TestBus>> {
    
    public TestComputer() {
        this(new TestBus());
    }
    
    TestComputer(TestBus bus) {
        super(bus, new MOS6502<>(bus));
    }

    @Override
    public void step() {
        cpu.step();
    }

    @Override
    public void loadROM(byte[] rom) {
        System.arraycopy(
                rom, 0, 
                ((Memory8) bus.enumMemory()[0].mem()).getMemory(), 0, 
                65505
        );
    }
    
}
