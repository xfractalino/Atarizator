/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui.debugging;

import com.fractalino.atarizator.emulate.Memory;

/**
 *
 * @author fractalino
 */
public class MemoryViewPanel extends javax.swing.JPanel {

    private final Memory mem;
    
    private MemoryTableModel model = null;
    
    public MemoryViewPanel(Memory mem) {
        this.mem = mem;
        
        initComponents();
    }
    
    public MemoryTableModel getMemoryTableModel() {
        if(model == null) {
            model = new MemoryTableModel(mem, 8);
        }
        
        return model;
    }
    
    public void update(int x, int y) {
        model.fireTableDataChanged(); // to change with a more robust single cell update.
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        memView = new javax.swing.JTable();

        memView.setModel(getMemoryTableModel());
        jScrollPane1.setViewportView(memView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable memView;
    // End of variables declaration//GEN-END:variables
}
