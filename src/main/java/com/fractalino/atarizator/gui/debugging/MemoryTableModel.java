/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui.debugging;

import com.fractalino.atarizator.emulate.Memory;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author fractalino
 */
public class MemoryTableModel extends AbstractTableModel{
    
    private Memory mem;
    
    private int cols;
    
    private Mode viewMode = Mode.HEX;
    
    public MemoryTableModel(Memory mem, int cols) {
        this.mem = mem;
        this.cols = cols;
    }

    @Override
    public int getRowCount() {
        return mem.getCapacity() / cols;
    }

    @Override
    public int getColumnCount() {
        return cols + 1;
    }
    
    @Override
    public String getColumnName(int col) {
        return Integer.toHexString(col);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return String.format("%04x", rowIndex * cols);
        }
        
        int value = mem.read(rowIndex * cols + columnIndex - 1);
        
        switch(viewMode) {
            case OCT: return String.format("%04o", value);
            case DEC: return value & 0xFF;
            case HEX: return String.format("%02x", value);
        }
        
        return null;
    }
    
    public void setViewMode(Mode viewMode) {
        if(this.viewMode != viewMode) {
            this.viewMode = viewMode;
            this.fireTableDataChanged();
        }
    }
    
    public void setColumns(int cols) {
        if(this.cols != cols) {
            this.cols = cols;
            this.fireTableStructureChanged();
        }
    }
    
    public enum Mode {
        OCT, DEC, HEX
    }
    
}
