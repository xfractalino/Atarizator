/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui.debugging;

import com.fractalino.atarizator.emulate.Computer;
import com.fractalino.atarizator.emulate.Memory;
import com.fractalino.atarizator.emulate.atari.Atari2600;
import com.fractalino.atarizator.emulate.atari.test.Tests;

import java.awt.Component;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fractalino
 */
public class DebugWindow extends javax.swing.JFrame {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugWindow.class);

    private Computer computer;
    
    private final Timer runStepsTimer;
    
    public DebugWindow(Computer computer) {
        this.computer = computer;
        
        initComponents();
        postInitComponents();
        
        runStepsTimer = new Timer(50, (e) -> {
            step();

            for(Component c : tabbedPane.getComponents()) {
                if(c instanceof MemoryViewPanel mvp) {
                    mvp.getMemoryTableModel().fireTableDataChanged();
                }
            }
        });
    }
    
    private void resetTabs() {
        tabbedPane.removeAll();
    }
    
    private void updateTabs() {
        for(Memory.MemoryRecord memr : computer.getBus().enumMemory())
            tabbedPane.addTab(memr.name(), new MemoryViewPanel(memr.mem()));
    }
    
    private void postInitComponents() {
        fileChooser = new JFileChooser((File) null);
        
        updateTabs();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        opCode = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Step");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jButton2.setText("Run Steps");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        jSpinner1.setValue(3);
        jPanel1.add(jSpinner1);
        jPanel1.add(jSeparator1);

        jLabel1.setText("Current opcode:");
        jPanel1.add(jLabel1);

        opCode.setEditable(false);
        opCode.setText("00");
        jPanel1.add(opCode);

        jMenu1.setText("File");

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setText("Open...");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(openMenuItem);

        jMenuItem1.setText("Load test ROM");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        int opt = fileChooser.showDialog(this, "Open");
        
        if(opt == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                
                computer.loadROM(data);
                
                for(Component c : tabbedPane.getComponents()) {
                    if(c instanceof MemoryViewPanel mvp) {
                        mvp.getMemoryTableModel().fireTableDataChanged();
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this, 
                        ex, 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        step();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        computer.loadROM(Tests.generateTestRom());
        
        for(Component c : tabbedPane.getComponents()) {
            if(c instanceof MemoryViewPanel mvp) {
                mvp.update(0, 0);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(runStepsTimer.isRunning()) runStepsTimer.stop();
        else runStepsTimer.start();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void step() {
        computer.step();
        opCode.setText(String.format(
                "%02x", 
                computer.getCPU().currentOpcode()
        ));
        for(Component c : tabbedPane.getComponents()) {
            if(c instanceof MemoryViewPanel mvp) {
                mvp.update(0, 0);
            }
        }
    }
    
    public static void init() {
        java.awt.EventQueue.invokeLater(() -> {
            new DebugWindow(new Atari2600()).setVisible(true);
        });
    }
    
    private JFileChooser fileChooser;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField opCode;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
