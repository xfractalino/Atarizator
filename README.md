# Atarizator
 
A MOS 6502/6507 emulator written in Java, with partial Commodore 64 and Atari 2600 machine implementations built on a shared hardware abstraction layer.
 
## Features
 
### CPU (MOS6502.java)
 
The core is a cycle-accurate MOS 6502 implementation covering the full official instruction set plus a large subset of NMOS illegal opcodes:
 
**Illegal opcodes implemented:** SLO, RLA, SRE, RRA, SAX, LAX, DCP, ISC, ANC, ALR, ARR, AXS — the combinatorial instructions that most toy emulators skip entirely.
 
**Hardware bugs emulated:**
- JMP indirect page-wrap bug (`0x6C`): if the low byte of the pointer address is `0xFF`, the high byte is fetched from the same page rather than the next, matching real NMOS 6502 behaviour.
- JAM/KIL opcodes (`0x02`, `0x12`, `0x22`, ...): the CPU halts, mirroring what happens on real hardware when these are hit.
**Cycle accuracy:**
- Per-instruction cycle counts from the full 256-entry `CYCLES` table.
- Page-cross penalties tracked dynamically for indexed addressing modes (`absx`, `absy`, `indy`).
- BCD/decimal mode in ADC and SBC, including the extra cycle penalty.
**Interrupt handling:** NMI, IRQ (maskable via the I flag), and BRK with correct B-flag and stack behaviour. Reset vector loaded from `$FFFC`.
 
**Debug opcode:** `0xF2` is repurposed as a `LOG` instruction that dumps CPU state via SLF4J. Costs 0 cycles and is transparent to normal execution.
 
### Architecture
 
The CPU is decoupled from any specific machine through a generic `Bus<B>` interface. Address decoding, memory mapping, and peripheral routing are all handled by machine-specific bus implementations — the CPU only calls `read(addr)` and `write(addr, value)`.
 
```
MOS6502<B extends Bus>
    └── Bus (interface)
         ├── Atari2600Bus   →  Atari2600 (MOS6507 + TIA + RIOT)
         └── C64Bus         →  C64 (CIA x2, VIC-II, SID, KeyboardMatrix)
```
 
`Device` and `Peripheral` provide the base abstractions for memory-mapped hardware. `MemoryWriteListener` allows components to observe writes to specific address ranges without polling.
 
### Machine targets
 
**Atari 2600** — bus and address decoding implemented, TIA and RIOT stubbed.
 
**Commodore 64** — bus with full address decoding implemented, CIA (Complex Interface Adapter) with timer and keyboard matrix support, VIC-II and SID stubbed. The keyboard matrix (`KeyboardMatrix.java`) maps physical key events to the C64's hardware scan matrix.
 
### Debugging
 
A GLFW/LWJGL-based debug window (`DebugWindow`) provides a live memory view (`MemoryViewPanel`) backed by a custom `MemoryTableModel`. Useful for inspecting zero-page state, stack, and ROM regions while the emulator is running.
 
## Building
 
Requires Java 17+ and Maven.
 
```bash
mvn compile
mvn package
```
 
## Project structure
 
```
src/main/java/com/fractalino/atarizator/
├── emulate/
│   ├── MOS6502.java          # CPU core
│   ├── Bus.java              # Bus interface
│   ├── Computer.java         # Machine base class
│   ├── Device.java           # Memory-mapped device base
│   ├── Memory.java / Memory8.java
│   ├── MemoryWriteListener.java
│   ├── Peripheral.java
│   ├── atari/
│   │   ├── Atari2600.java
│   │   ├── Atari2600Bus.java
│   │   ├── Atari2600TIA.java
│   │   └── Atari2600RIOT.java
│   └── commodore/
│       ├── C64.java
│       ├── C64Bus.java
│       ├── CIA.java
│       ├── KeyboardMatrix.java
│       ├── VIC2.java
│       └── SID.java
└── gui/
    ├── GLFWSystem.java
    ├── Window.java
    └── debugging/
        ├── DebugWindow.java
        └── MemoryViewPanel.java
```
 
## License
 
GPL-3.0 — see [LICENSE](LICENSE).
