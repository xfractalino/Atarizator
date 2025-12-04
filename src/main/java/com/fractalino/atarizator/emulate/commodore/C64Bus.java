package com.fractalino.atarizator.emulate.commodore;

import com.fractalino.atarizator.emulate.Bus;
import com.fractalino.atarizator.emulate.MOS6502;
import com.fractalino.atarizator.emulate.Memory;
import com.fractalino.atarizator.emulate.Memory8;

/**
 *
 * @author fractalino
 */
public class C64Bus implements Bus {
    
    private final Memory8 cram = new Memory8(1024);
    private final Memory8 dram = new Memory8(65536);
    
    private final Memory8 basicRom  = new Memory8(8192);
    private final Memory8 kernalRom = new Memory8(8192);
    private final Memory8 charRom   = new Memory8(4096);
    
    private final MOS6502 cpu;
    private final VIC2 vic  = new VIC2();
    private final SID  sid  = new SID();
    private final CIA  cia1;
    private final CIA  cia2;
    
    private int portDirection = 0xFF;
    private int portData = 0x27;
    
    C64Bus() {
        cpu  = new MOS6502(this);
        cia1 = new CIA(this, CIA.CIAID.CIA1);
        cia2 = new CIA(this, CIA.CIAID.CIA2);
    }
    
    @Override
    public int read(int addr) {
        if (addr >= 0xE000) {
            if ((portData & 0x02) == 0x02) {
                // if KERNAL is hidden by RAM, read RAM.
                return dram.read(addr); 
            }
            
            return kernalRom.read(addr - 0xE000);
        }
        
        if (addr >= 0xD000 && addr <= 0xDFFF) {
            int bits = portData & 0x06;

            return switch (bits) {
                case 0b00: // Bits 2, 1 = 0, 0 (I/O + KERNAL)
                    yield readIO(addr);
                case 0b10: // Bits 2, 1 = 1, 0 (char ROM + KERNAL)
                    yield charRom.read(addr - 0xD000);
                case 0b11: // Bits 2, 1 = 1, 1 (RAM + RAM)
                    yield dram.read(addr);
                default:
                    yield readIO(addr);
            };
        }
        
        if (addr >= 0xA000 && addr <= 0xBFFF) {
            if ((portData & 0x01) == 0x00) { 
                return basicRom.read(addr - 0xA000);
            }
            
            return dram.read(addr);
        }
    
        // low RAM ($0000 - $9FFF)
        return dram.read(addr);
    }

    @Override
    public void write(int addr, int val) {
        if (addr >= 0xE000) {
            if ((portData & 0x02) == 0x02) {
                // if KERNAL is hidden by RAM, read RAM.
                dram.write(addr, val); 
            }
            
            kernalRom.write(addr - 0xE000, val);
        }
        
        if (addr >= 0xD000 && addr <= 0xDFFF) {
            int bits = portData & 0x06;

            switch (bits) {
                case 0b00: // Bits 2, 1 = 0, 0 (I/O + KERNAL)
                    writeIO(addr, val);
                case 0b10: // Bits 2, 1 = 1, 0 (char ROM + KERNAL)
                    charRom.write(addr - 0xD000, val);
                case 0b11: // Bits 2, 1 = 1, 1 (RAM + RAM)
                    dram.write(addr, val);
                default:
                    writeIO(addr, val);
            };
        }
        
        if (addr >= 0xA000 && addr <= 0xBFFF) {
            if ((portData & 0x01) == 0x00) { 
                basicRom.write(addr - 0xA000, val);
            }
            
            dram.write(addr, val);
        }
    
        // low RAM ($0000 - $9FFF)
        dram.write(addr, val);
    }
    
    public void nmi() {
        cpu.nmi();
    }
    
    public void irq() {
        cpu.irq();
    }

    @Override
    public Memory.MemoryRecord[] enumMemory() {
        return new Memory.MemoryRecord[] {
            new Memory.MemoryRecord(charRom,   "User ROM"),
            new Memory.MemoryRecord(kernalRom, "Kernal ROM"),
            new Memory.MemoryRecord(basicRom,  "Char ROM"),
            
            new Memory.MemoryRecord(dram, "Dynamic RAM"),
            new Memory.MemoryRecord(cram, "Color RAM"),
            
            new Memory.MemoryRecord(cia1.getRegisters(), "CIA 1"),
            new Memory.MemoryRecord(cia2.getRegisters(), "CIA 2")
        };
    }
    
    private int readIO(int addr) {
        if(addr <= 0xD3FF) return vic .read(addr - 0xD000);
        if(addr <= 0xD7FF) return sid .read(addr - 0xD400);
        if(addr <= 0xDBFF) return cram.read(addr - 0xD800);
        if(addr <= 0xDCFF) return cia1.read(addr - 0xDC00);
        if(addr <= 0xDDFF) return cia2.read(addr - 0xDD00);
        // I/O expansions. We leave them unsupported and fall back to 0xFF for now.
        if(addr <= 0xDFFF) return 0xFF; 
        
        return 0xFF;
    }
    
    private void writeIO(int addr, int val) {
        if(addr <= 0xD3FF) vic .write(addr - 0xD000, val);
        if(addr <= 0xD7FF) sid .write(addr - 0xD400, val);
        if(addr <= 0xDBFF) cram.write(addr - 0xD800, val);
        if(addr <= 0xDCFF) cia1.write(addr - 0xDC00, val);
        if(addr <= 0xDDFF) cia2.write(addr - 0xDD00, val);
        // I/O expansions. We leave them unsupported and fall back to 0xFF for now.
        if(addr <= 0xDFFF);
    }
}
