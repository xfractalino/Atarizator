/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fractalino.atarizator.emulate.atari.test;

/**
 *
 * @author fractalino
 */
public final class Tests {
    public static byte[] generateTestRom() {
        byte[] romData = new byte[4096]; 

        int offset = 0x0000;

        // LDX #$FF (A2 FF)
        romData[offset++] = (byte) 0xA2;
        romData[offset++] = (byte) 0xFF;

        // TXS (9A)
        romData[offset++] = (byte) 0x9A;

        // LDA #$12 (A9 12)
        romData[offset++] = (byte) 0xA9;
        romData[offset++] = (byte) 0x12;

        // PHA (48)
        romData[offset++] = (byte) 0x48;

        // LDA #$34 (A9 34)
        romData[offset++] = (byte) 0xA9;
        romData[offset++] = (byte) 0x34;

        // PHA (48)
        romData[offset++] = (byte) 0x48;

        // PLA (68)
        romData[offset++] = (byte) 0x68;

        // PLP (28)
        romData[offset++] = (byte) 0x28; 

        // LDA #$AA (A9 AA)
        romData[offset++] = (byte) 0xA9;
        romData[offset++] = (byte) 0xAA;

        // STA $0080 (85 80)
        romData[offset++] = (byte) 0x85;
        romData[offset++] = (byte) 0x80;

        // LDA $0080 (A5 80)
        romData[offset++] = (byte) 0xA5;
        romData[offset++] = (byte) 0x80;

        // JMP $F000 (4C 00 F0)
        romData[offset++] = (byte) 0x4C;
        romData[offset++] = (byte) 0x00;
        romData[offset++] = (byte) 0xF0;

        // Reset
        romData[0xFFC] = (byte) 0x00; 
        romData[0xFFD] = (byte) 0xF0;

        return romData;
    }
}
