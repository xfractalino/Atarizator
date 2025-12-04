/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fractalino
 */
public abstract class Memory implements Device {
    
    private final List<MemoryWriteListener> mwls = new ArrayList<>(1);
    
    public abstract int getCapacity();
    protected abstract int doRead(int addr);
    protected abstract void doWrite(int addr, int v);
    
    @Override
    public final int read(int addr) {
        return doRead(addr);
    }
    
    @Override
    public final void write(int addr, int v) {
        for(var mwl : mwls) mwl.onMemoryWrite(addr, v);
        
        doWrite(addr, v);
    }
    
    public void registerMemoryWriteListener(MemoryWriteListener mwl) {
        mwls.add(mwl);
    }
    
    public void unregisterMemoryWriteListener(MemoryWriteListener mwl) {
        mwls.remove(mwl);
    }
    
    public record MemoryRecord(
            Memory mem,
            String name) {
    }
}
