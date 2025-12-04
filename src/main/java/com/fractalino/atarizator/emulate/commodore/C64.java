/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fractalino.atarizator.emulate.commodore;

import com.fractalino.atarizator.emulate.Computer;
import com.fractalino.atarizator.emulate.MOS6502;

/**
 *
 * @author fractalino
 */
public class C64 extends Computer<C64Bus, MOS6502<C64Bus>> {

    public C64() {
        this(new C64Bus());
    }
    
    private C64(C64Bus bus) {
        super(bus, new MOS6502<>(bus));
    }
    
    @Override
    public void step() {
        // TODO
    }

    @Override
    public void loadROM(byte[] rom) {
        // TODO
    }
    
}
