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
public interface Device {
    
    public int read(int v);
    public void write(int addr, int v);
    public void tick();
    
    public default void tick(int cycles) { 
        for(int i = 0; i < cycles; i++) tick();
    }
}
