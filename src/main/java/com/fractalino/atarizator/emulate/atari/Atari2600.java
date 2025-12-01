/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate.atari;

import com.fractalino.atarizator.emulate.Computer;
import com.fractalino.atarizator.emulate.MOS6502;

/**
 *
 * @author fractalino
 */
public class Atari2600 extends Computer<Atari2600Bus, MOS6502<Atari2600Bus>> {
    private int totalCycles = 0;
    
    public Atari2600() {
        this(new Atari2600Bus());
    }
    
    private Atari2600(Atari2600Bus bus) {
        super(bus, new MOS6502<Atari2600Bus>(bus)
        );
    }
    
    @Override
    public void loadROM(byte[] rom) {
        bus.setCartridge(rom);
        
        cpu.reset();
    }

    @Override
    public void step() {
        int cycles = cpu.step();
        totalCycles += cycles;

        bus.getRIOT().tick(cycles);
        bus.getTIA().tick(3 * cycles);
    }
    
}
