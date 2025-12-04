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
    public int read(int addr);
    public void write(int addr, int val);
    
    public Memory.MemoryRecord[] enumMemory();
    
    public default void registerMemoryWriteListener(MemoryWriteListener[] listeners) {
        var recs = enumMemory();
        
        if(listeners.length != recs.length) {
            throw new IllegalArgumentException("Can only register the same "
                    + "number of listeners as the number of memories.");
        }
        
        for(int i = 0; i < recs.length; i++) {
            var rec = recs[i];
            var lst = listeners[i];
            
            rec.mem().registerMemoryWriteListener(lst);
        }
    }
    
    public default int loadWord(int addr) {
        int l = read(addr);
        int h = read(addr + 1);
        return l | (h << 8);
    }
}
