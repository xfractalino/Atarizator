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
public interface Bus {
    public int load(int addr);
    public int loadWord(int addr);
    public void store(int addr, int val);
    public Memory.MemoryRecord[] enumMemory();
}
