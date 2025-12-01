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
public abstract class Computer<B extends Bus, C extends CPU> {
    protected final B bus;
    protected final C cpu;
    
    public Computer(B bus, C cpu) {
        this.bus = bus;
        this.cpu = cpu;
    }
    
    public B getBus() {
        return bus;
    }
    
    public C getCPU() {
        return cpu;
    }
    
    public abstract void step();
    public abstract void loadROM(byte[] rom);
}
