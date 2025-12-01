/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.emulate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fractalino
 * @param <B>
 */
public class MOS6502<B extends Bus> implements CPU{

    private static final int CC = 0x100; // Carry check

    // == FLAGS ==
    private static final int N = 0x80; // Negative flag
    private static final int V = 0x40; // Overflow flag
    private static final int B = 0x10; // Break command
    private static final int D = 0x08; // Decimal mode flag
    private static final int I = 0x04; // IRQ disable
    private static final int Z = 0x02; // Zero flag
    private static final int C = 0x01; // Carry flag
    // ===========

    // == REGISTERS ==
    // 8 bits
    private int A = 0x0; // Accumulator register
    private int X = 0x0; // X register
    private int Y = 0x0; // Y register
    private int S = 0x0; // Status register
    private int P = 0xFF; // Stack pointer
    // 16 bits
    private int PC = 0x0;

    private enum R {
        A, X, Y, S, P
    }
    // ===============

    private boolean jammed = false;
    private int penalty = 0;
    private int currentOpcode = 0x0;

    private final B bus;

    public MOS6502(B bus) {
        this.bus = bus;
    }

    public int step() {
        if(jammed) return 0;
        
        penalty = 0;
        
        currentOpcode = next();
        exec(currentOpcode);
        
        System.out.println(this);

        int cost = CYCLES[currentOpcode];

        return cost + penalty;
    }

    public void exec(int op) {
        switch (op) {
            case 0x0 -> BRK();
            case 0x1 -> ORA(xind(next()));
            case 0x5 -> ORA(next());
            case 0x6 -> ASL(next());
            case 0x8 -> PHP();
            case 0x9 -> ORA(imm());
            case 0xA -> ASL_A();
            case 0xD -> ORA(nextWord());
            case 0xE -> ASL(nextWord());

            case 0x10 -> BPL(next());
            case 0x11 -> ORA(indy(next()));
            case 0x15 -> ORA(zpgx(next()));
            case 0x16 -> ASL(zpgx(next()));
            case 0x18 -> CLC();
            case 0x19 -> ORA(absy(nextWord()));
            case 0x1D -> ORA(absx(nextWord()));
            case 0x1E -> ASL(nextWord() + X);

            case 0x20 -> JSR(nextWord());
            case 0x21 -> AND(xind(next()));
            case 0x24 -> BIT(next());
            case 0x25 -> AND(next());
            case 0x26 -> ROL(next());
            case 0x28 -> PLP();
            case 0x29 -> AND(imm());
            case 0x2A -> ROL_A();
            case 0x2C -> BIT(nextWord());
            case 0x2D -> AND(nextWord());
            case 0x2E -> ROL(nextWord());

            case 0x30 -> BMI(next());
            case 0x31 -> AND(indy(next()));
            case 0x35 -> AND(zpgx(next()));
            case 0x36 -> ROL(zpgx(next()));
            case 0x38 -> SEC();
            case 0x39 -> AND(absy(nextWord()));
            case 0x3D -> AND(absx(nextWord()));
            case 0x3E -> ROL(absx(nextWord()));

            case 0x40 -> RTI();
            case 0x41 -> EOR(xind(next()));
            case 0x45 -> EOR(next());
            case 0x46 -> LSR(next());
            case 0x48 -> PHA();
            case 0x49 -> EOR(imm());
            case 0x4A -> LSR_A();
            case 0x4C -> JMP(nextWord());
            case 0x4D -> EOR(nextWord());
            case 0x4E -> LSR(nextWord());

            case 0x50 -> BVC(next());
            case 0x51 -> EOR(indy(next()));
            case 0x55 -> EOR(zpgx(next()));
            case 0x56 -> LSR(zpgx(next()));
            case 0x58 -> CLI();
            case 0x59 -> EOR(absy(nextWord()));
            case 0x5D -> EOR(absx(nextWord()));
            case 0x5E -> LSR(absx(nextWord()));

            case 0x60 -> RTS();
            case 0x61 -> ADC(xind(next()));
            case 0x65 -> ADC(next());
            case 0x66 -> ROR(next());
            case 0x68 -> PLA();
            case 0x69 -> ADC(imm());
            case 0x6A -> ROR_A();
            case 0x6C -> JMP(jmpPageWrapBug(nextWord()));
            case 0x6D -> ADC(nextWord());
            case 0x6E -> ROR(nextWord());

            case 0x70 -> BVS(next());
            case 0x71 -> ADC(indy(next()));
            case 0x75 -> ADC(zpgx(next()));
            case 0x76 -> ROR(zpgx(next()));
            case 0x78 -> SEI();
            case 0x79 -> ADC(absy(nextWord()));
            case 0x7D -> ADC(absx(nextWord()));
            case 0x7E -> ROR((nextWord() + X) & 0xFFFF);

            case 0x81 -> STA(xind(next()));
            case 0x84 -> STY(next());
            case 0x85 -> STA(next());
            case 0x86 -> STX(next());
            case 0x88 -> DEY();
            case 0x8A -> TXA();
            case 0x8C -> STY(nextWord());
            case 0x8D -> STX(nextWord());
            case 0x8E -> STA(nextWord());

            case 0x90 -> BCC(next());
            case 0x91 -> STA((bus.loadWord(next()) + Y) & 0xFFFF);
            case 0x94 -> STY(zpgx(next()));
            case 0x95 -> STA(zpgx(next()));
            case 0x96 -> STX(zpgy(next()));
            case 0x98 -> TYA();
            case 0x99 -> STA((nextWord() + Y) & 0xFFFF);
            case 0x9A -> TXS();
            case 0x9D -> STA((nextWord() + X) & 0xFFFF);

            case 0xA0 -> LDY(imm());
            case 0xA1 -> LDA(xind(next()));
            case 0xA2 -> LDX(imm());
            case 0xA4 -> LDY(next());
            case 0xA5 -> LDA(next());
            case 0xA6 -> LDX(next());
            case 0xA8 -> TAY();
            case 0xA9 -> LDA(imm());
            case 0xAA -> TAX();
            case 0xAC -> LDY(nextWord());
            case 0xAD -> LDA(nextWord());
            case 0xAE -> LDX(nextWord());

            case 0xB0 -> BCS(next());
            case 0xB1 -> LDA(indy(next()));
            case 0xB4 -> LDY(zpgx(next()));
            case 0xB5 -> LDA(zpgx(next()));
            case 0xB6 -> LDX(zpgy(next()));
            case 0xB8 -> CLV();
            case 0xB9 -> LDA(absy(nextWord()));
            case 0xBA -> TSX();
            case 0xBC -> LDY(absx(nextWord()));
            case 0xBD -> LDA(absx(nextWord()));
            case 0xBE -> LDX(absy(nextWord()));

            case 0xC0 -> CPY(imm());
            case 0xC1 -> CMP(xind(next()));
            case 0xC4 -> CPY(next());
            case 0xC5 -> CMP(next());
            case 0xC6 -> DEC(next());
            case 0xC8 -> INY();
            case 0xC9 -> CMP(imm());
            case 0xCA -> DEX();
            case 0xCC -> CPY(nextWord());
            case 0xCD -> CMP(nextWord());
            case 0xCE -> DEC(nextWord());

            case 0xD0 -> BNE(next());
            case 0xD1 -> CMP(indy(next()));
            case 0xD5 -> CMP(zpgx(next()));
            case 0xD6 -> DEC(zpgx(next()));
            case 0xD8 -> CLD();
            case 0xD9 -> CMP(absy(nextWord()));
            case 0xDA -> TSX();
            case 0xDD -> CMP(absx(nextWord()));
            case 0xDE -> DEC((nextWord() + X) & 0xFFFF);

            case 0xE0 -> CPX(imm());
            case 0xE1 -> SBC(xind(next()));
            case 0xE4 -> CPX(next());
            case 0xE5 -> SBC(next());
            case 0xE6 -> INC(next());
            case 0xE8 -> INX();
            case 0xE9 -> SBC(imm());
            case 0xEA -> NOP();
            case 0xEC -> CPX(nextWord());
            case 0xED -> SBC(nextWord());
            case 0xEE -> INC(nextWord());

            case 0xF0 -> BEQ(next());
            case 0xF1 -> SBC(indy(next()));
            case 0xF5 -> SBC(zpgx(next()));
            case 0xF6 -> INC(zpgx(next()));
            case 0xF8 -> SED();
            case 0xF9 -> SBC(absy(nextWord()));
            case 0xFD -> SBC(absx(nextWord()));
            case 0xFE -> INC(absx(nextWord()));

            // ILLEGAL OPCODES
            
            // SLO
            case 0x03 -> SLO(xind(next()));
            case 0x07 -> SLO(next());
            case 0x0F -> SLO(nextWord());
            case 0x13 -> SLO(indy(next()));
            case 0x17 -> SLO(zpgx(next()));
            case 0x1B -> SLO(absy(nextWord()));
            case 0x1F -> SLO(absx(nextWord()));

            // RLA
            case 0x23 -> RLA(xind(next()));
            case 0x27 -> RLA(next());
            case 0x2F -> RLA(nextWord());
            case 0x33 -> RLA(indy(next()));
            case 0x37 -> RLA(zpgx(next()));
            case 0x3B -> RLA(absy(nextWord()));
            case 0x3F -> RLA(absx(nextWord()));

            // SRE
            case 0x43 -> SRE(xind(next()));
            case 0x47 -> SRE(next());
            case 0x4F -> SRE(nextWord());
            case 0x53 -> SRE(indy(next()));
            case 0x57 -> SRE(zpgx(next()));
            case 0x5B -> SRE(absy(nextWord()));
            case 0x5F -> SRE(absx(nextWord()));

            // RRA
            case 0x63 -> RRA(xind(next()));
            case 0x67 -> RRA(next());
            case 0x6F -> RRA(nextWord());
            case 0x73 -> RRA(indy(next()));
            case 0x77 -> RRA(zpgx(next()));
            case 0x7B -> RRA(absy(nextWord()));
            case 0x7F -> RRA(absx(nextWord()));

            // SAX
            case 0x83 -> SAX(xind(next()));
            case 0x87 -> SAX(next());
            case 0x8F -> SAX(nextWord());
            case 0x97 -> SAX(zpgy(next()));

            // LAX
            case 0xA3 -> LAX(xind(next()));
            case 0xA7 -> LAX(next());
            case 0xAF -> LAX(nextWord());
            case 0xB3 -> LAX(indy(next()));
            case 0xB7 -> LAX(zpgy(next()));
            case 0xBF -> LAX(absy(nextWord()));

            // DCP
            case 0xC3 -> DCP(xind(next()));
            case 0xC7 -> DCP(next());
            case 0xCF -> DCP(nextWord());
            case 0xD3 -> DCP(indy(next()));
            case 0xD7 -> DCP(zpgx(next()));
            case 0xDB -> DCP(absy(nextWord()));
            case 0xDF -> DCP(absx(nextWord()));

            // ISC
            case 0xE3 -> ISC(xind(next()));
            case 0xE7 -> ISC(next());
            case 0xEF -> ISC(nextWord());
            case 0xF3 -> ISC(indy(next()));
            case 0xF7 -> ISC(zpgx(next()));
            case 0xFB -> ISC(absy(nextWord()));
            case 0xFF -> ISC(absx(nextWord()));

            // ANC
            case 0x0B, 0x2B -> ANC();

            // ALR
            case 0x4B -> ALR();

            // ARR
            case 0x6B -> ARR();

            // AXS
            case 0xCB -> AXS();

            // SBC
            case 0xEB -> SBC(imm());
            
            case 0xF2 -> LOG();

            // <editor-fold defaultstate="collapsed" desc="JAM">
            case int t when t % 0x10 == 0x2 -> JAM();
            // </editor-fold>

            default -> NOP();
        }
    }

    private void LSR(int addr) {
        int M = bus.load(addr);
        int lsb = M & 0x1;
        M >>>= 1;
        M &= 0xFF;
        uaf(M);
        sf(C, lsb == 0x1);
        bus.store(addr, M);
    }

    private void LSR_A() {
        int lsb = A & 0x1;
        A >>>= 1;
        A &= 0xFF;
        uaf(A);
        sf(C, lsb == 0x1);
    }

    private void ROL(int addr) {
        int M = bus.load(addr);
        M <<= 1;
        M |= S & C;
        uaf(M);
        sf(C, (M & CC) == CC);
        bus.store(addr, M);
    }

    private void ROL_A() {
        A <<= 1;
        A |= S & C;
        uaf(A);
        sf(C, (A & CC) == CC);
    }

    private void ROR(int addr) {
        int M = bus.load(addr);
        int lsb = M & 0x1;
        M >>>= 1;
        M |= (S & C) << 7;
        uaf(M);
        sf(C, lsb == 1);
        bus.store(addr, M);
    }

    private void ROR_A() {
        int lsb = A & 0x1;
        A >>>= 1;
        A |= (S & C) << 7;
        uaf(A);
        sf(C, lsb == 1);
    }

    private void BIT(int addr) {
        final int M = bus.load(addr);
        sf(N, (M & 0x80) == 0x80);
        sf(V, (M & 0x40) == 0x40);
        sf(Z, (A & M) == 0x0);
    }

    private void STA(int addr) {
        bus.store(addr, A);
    }

    private void STX(int addr) {
        bus.store(addr, X);
    }

    private void STY(int addr) {
        bus.store(addr, Y);
    }

    private void TXS() {
        P = X;
    }

    private void TXA() {
        A = X;
        uaf(A);
    }

    private void TYA() {
        A = Y;
        uaf(A);
    }

    private void TAX() {
        X = A;
        uaf(X);
    }

    private void TAY() {
        Y = A;
        uaf(Y);
    }

    private void TSX() {
        X = P;
        uaf(X);
    }

    private void DEY() {
        uaf(--Y);
    }

    private void DEX() {
        uaf(--X);
    }

    private void DEC(int addr) {
        int v = bus.load(addr) - 1;
        uaf(v);
        bus.store(addr, v);
    }

    private void INY() {
        uaf(++Y);
    }

    private void INX() {
        uaf(++X);
    }

    private void INC(int addr) {
        int v = bus.load(addr) + 1;
        uaf(v);
        v &= 0xFF;
        bus.store(addr, v);
    }

    private void LDY(int addr) {
        Y = bus.load(addr);
        uaf(Y);
    }

    private void LDX(int addr) {
        X = bus.load(addr);
        uaf(X);
    }

    private void LDA(int addr) {
        A = bus.load(addr);
        uaf(A);
    }

    private void JSR(int addr) {
        spush(PC + 2);
        JMP(addr);
    }

    private void JMP(int addr) {
        PC = addr;
    }

    private void RTS() {
        int SPC = spull();
        PC = SPC + 1;
    }

    private void BRK() {
        PC += 2;

        interrupt(true);
    }

    private void RTI() {
        S = spull();

        S |= 0x20;
        S &= ~B;

        int low = spull();
        int high = spull();

        PC = (high << 8) | low;
    }

    private void BVC(int r) {
        if ((S & V) == V)
            return;

        JMP(rel(r));
    }

    private void BVS(int r) {
        if ((S & V) == 0x0)
            return;

        JMP(rel(r));
    }

    private void BMI(int r) {
        if ((S & N) == 0x0)
            return;

        JMP(rel(r));
    }

    private void BNE(int r) {
        if ((S & Z) == Z)
            return;

        JMP(rel(r));
    }

    private void BPL(int r) {
        if ((S & N) == N)
            return;

        JMP(rel(r));
    }

    private void BEQ(int r) {
        if ((S & Z) == 0x0)
            return;

        JMP(rel(r));
    }

    private void BCC(int r) {
        if ((S & C) == C)
            return;

        JMP(rel(r));
    }

    private void BCS(int r) {
        if ((S & C) == 0x0)
            return;

        JMP(rel(r));
    }

    private void CLC() {
        sf(C, false);
    }

    private void CLI() {
        sf(I, false);
    }

    private void CLV() {
        sf(V, false);
    }

    private void CLD() {
        sf(D, false);
    }

    private void SEI() {
        sf(I, true);
    }

    private void SEC() {
        sf(C, true);
    }

    private void SED() {
        sf(D, true);
    }

    private void CPY(int addr) {
        int cmp = Y - bus.load(addr);
        uaf(cmp);
        sf(C, (cmp & CC) == CC);
    }

    private void CPX(int addr) {
        int cmp = X - bus.load(addr);
        uaf(cmp);
        sf(C, (cmp & CC) == CC);
    }

    private void CMP(int addr) {
        int cmp = A - bus.load(addr);
        uaf(cmp);
        sf(C, (cmp & CC) == CC);
    }

    private void PHP() {
        spush(S);
    }

    private void PHA() {
        spush(A);
    }

    private void PLP() {
        S = spull();
    }

    private void PLA() {
        A = spull();
    }

    private void ORA(int addr) {
        final int M = bus.load(addr);
        A |= M;
        uaf(A);
    }

    private void AND(int addr) {
        final int M = bus.load(addr);
        A &= M;
        uaf(A);
    }

    private void EOR(int addr) {
        final int M = bus.load(addr);
        A ^= M;
        uaf(A);
    }

    private void ASL(int addr) {
        int M = bus.load(addr);
        M <<= 1;
        uaf(M);
        sf(C, M > 0xFF);
        bus.store(addr, M);
    }

    private void ASL_A() {
        A <<= 1;
        uaf(A);
        sf(C, A > 0xFF);
    }

    private void ADC(int addr) {
        int M = bus.load(addr);

        int c_in = (S & C);
        int binSum = A + M + c_in;

        sf(V, ((A ^ binSum) & (M ^ binSum) & 0x80) != 0);
        sf(N, (binSum & 0x80) != 0);
        sf(Z, (binSum & 0xFF) == 0);

        // decimal handle
        if ((S & D) != 0) {
            if ((A & 0x0F) + (M & 0x0F) + c_in > 9) {
                binSum += 0x06;
            }
            if (binSum > 0x9F) {
                binSum += 0x60;
            }
            
            penalty++;
        }

        sf(C, binSum > 0xFF);

        A = binSum & 0xFF;
    }

    private void SBC(int addr) {
        int M = bus.load(addr);
        int c_in = (S & C);

        int binDiff = A - M - (1 - c_in);

        sf(V, ((A ^ binDiff) & (A ^ M) & 0x80) != 0);
        sf(N, (binDiff & 0x80) != 0);
        sf(Z, (binDiff & 0xFF) == 0);

        // decimal handle
        if ((S & D) != 0) {
            int lowDiff = (A & 0x0F) - (M & 0x0F) - (1 - c_in);
            if (lowDiff < 0) {
                binDiff -= 0x06;
            }

            if (binDiff < 0) {
                binDiff -= 0x60;
            }
            sf(C, binDiff >= 0);
            
            penalty++;
        }

        sf(C, binDiff >= 0);

        A = binDiff & 0xFF;
    }

    private void SLO(int addr) {
        ASL(addr);
        ORA(addr);
    }

    private void RLA(int addr) {
        ROL(addr);
        AND(addr);
    }

    private void SRE(int addr) {
        LSR(addr);
        EOR(addr);
    }

    private void RRA(int addr) {
        ROR(addr);
        ADC(addr);
    }

    private void SAX(int addr) {
        bus.store(addr, A & X);
    }

    private void LAX(int addr) {
        int value = bus.load(addr);
        A = value;
        X = value;
        uaf(A);
    }

    private void DCP(int addr) {
        DEC(addr);
        CMP(addr);
    }

    private void ISC(int addr) {
        INC(addr);
        SBC(addr);
    }

    private void ANC() {
        AND(imm());
        sf(C, (S & N) != 0);
    }

    private void ALR() {
        AND(imm());
        LSR_A();
    }

    private void ARR() {
        int M = bus.load(imm());
        A &= M;
        // ROR A
        int oldC = (S & C);
        A = (A >>> 1) | (oldC << 7);

        uaf(A); // N and Z

        sf(C, (A & 0x40) != 0);
        sf(V, (((A >>> 6) ^ (A >>> 5)) & 1) == 1);
    }

    private void AXS() {
        int M = bus.load(imm());
        int diff = (A & X) - M;
        sf(C, diff >= 0);
        X = diff & 0xFF;
        uaf(X);
    }

    private void JAM() {
        jammed = true;
    }

    private void NOP() {
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MOS6502.class);
    
    /** 
     * This is an helper special instruction used to log CPU data in this 
     * emulator.
     * 
     * It uses 0 cycles.
     */
    private void LOG() {
        LOGGER.info("MOS6507[PC={}, S={}, jam={}]",
                Integer.toHexString(PC),
                Integer.toBinaryString(S),
                jammed
        );
    }

    private int xind(int ptr) {
        int zp = (ptr + X) & 0xFF;
        
        int low = bus.load(zp);
        int high = bus.load((zp + 1) & 0xFF);
        
        return low | (high << 8);
    }

    private int indy(int addr) {
        int low = bus.load(addr);
        int high = bus.load((addr + 1) & 0xFF);
        int base = low | (high << 8);
        int effective = (base + Y) & 0xFFFF;
        
        checkPageCross(base, effective);
        
        return effective;
    }

    private int rel(int bb) {
        return (PC + (byte)bb) & 0xFFFF;
    }

    private int zpgx(int addr) {
        return (addr + X) & 0xFF;
    }

    private int zpgy(int addr) {
        return (addr + Y) & 0xFF;
    }

    private int imm() {
        return PC++;
    }

    private int absx(int addr) {
        int effective = (addr + X) & 0xFFFF;
        checkPageCross(addr, effective);
        return effective;
    }

    private int absy(int addr) {
        int effective = (addr + Y) & 0xFFFF;
        checkPageCross(addr, effective);
        return effective;
    }

    // page cross adds a cycle
    private void checkPageCross(int base, int eff) {
        if ((base & 0xFF00) != (eff & 0xFF00))
            penalty++;
    }

    public void setFlags(byte flags) {
        this.S = flags;
    }

    private void uaf(int t) {
        sf(Z, t == 0);
        sf(N, (t & 0x80) == 0x80);
    }

    private void sf(int flag, boolean set) {
        if (set)
            S |= flag;
        else
            S &= ~flag;
    }

    private int nextWord() {
        final int word = bus.loadWord(PC);
        PC+=2;
        return word;
    }

    private int next() {
        return bus.load(PC++);
    }

    private void spush(int b) {
        bus.store(0x100 + P, b);

        P = (P - 1) & 0xFF;
    }

    private int spull() {
        P = (P + 1) & 0xFF;

        return bus.load(0x100 + P);
    }

    private int jmpPageWrapBug(int addr) {
        final int target_lsb = addr;
        // trigger the MOS6502 JMP bug if conditions are met
        final int target_msb = ((addr & 0xFF) == 0xFF) ? (addr & 0xFF00) | ((addr) & 0xFF) : (addr + 1);

        return (bus.load(target_msb) << 8) |
                bus.load(target_lsb);
    }

    private void interrupt(boolean isSoftwareBreak) {
        spush((PC >> 8) & 0xFF);
        spush(PC & 0xFF);

        int statusToPush = S | 0x20;
        if (isSoftwareBreak) {
            statusToPush |= B;
        }
        spush(statusToPush);

        // Disable further interrupts
        sf(I, true);

        PC = bus.loadWord(0xFFFE);
    }
    
    public void reset() {
        P = 0xFF;
        sf(I, true);
        sf(D, false);
        // Load the Reset Vector (FFFC/FFFD)
        PC = bus.loadWord(0xFFFC);
        
        System.out.println(Integer.toHexString(PC));
    }
    
    @Override
    public int currentOpcode() {
        return currentOpcode;
    }
    
    @Override
    public String toString() {
        return String.format("MOS6507[PC = %04x; S = %s; OP = %02x]", 
                PC, Integer.toString(S, 2), currentOpcode);
    }

    private static final int[] CYCLES = {
            7, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 4, 4, 6, 6,
            2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
            6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 4, 4, 6, 6,
            2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
            6, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 3, 4, 6, 6,
            2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
            6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 5, 4, 6, 6,
            2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
            2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4,
            2, 6, 2, 6, 4, 4, 4, 4, 2, 5, 2, 5, 5, 5, 5, 5,
            2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4,
            2, 5, 2, 5, 4, 4, 4, 4, 2, 4, 2, 4, 4, 4, 4, 4,
            2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,
            2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
            2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6,
            2, 5, 0, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7
    };
}
