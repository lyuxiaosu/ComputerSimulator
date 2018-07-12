package com.test;

import java.util.*;
public class RomLoader {
	
	private static final String[] rom_program = new String[] {			
			"LDR 2, 1, 1", //Load register 2 with the contents of the memory location 8, with index register 1
			"STR 2, 0, 19", //Store register 2 to memory location 19, NO indexing	
			"LDX 3, 19", // Load index register 1 from the content of memory location 8
			"LDA 2, 1, 17", //Load register 2 with memory address 17, with index register 1
			"STX 1, 25", // store index register 1 to address 25, with index register 1
	};
	
	private CentralProcessor cpu;
	private Memory memory;
	public RomLoader(CentralProcessor cpu, Memory memory) {
		this.cpu = cpu;
		this.memory = memory;
	}
	
	public boolean LoadProgram() {
		//Initialize the reserved memory
		String trap_instruction = "TRAP 15";
		String fault_instruction = "MFT 6";
		boolean result = memory.SetReservedMemory(0, trap_instruction);
		if (result == false) {
			return false;
		}
		result = memory.SetReservedMemory(1,  fault_instruction);
		if (result == false) {
			return false;
		}
		
		int len = rom_program.length;
		
		for (int i = 0; i < len; i++) {
			result = memory.LoadContent(8 + i, rom_program[i]);
			if (result == false) {
				return false;
			}
		}
		cpu.SetPC(8);
		return result;
	}
	
	public int Execute() {
		return cpu.Execute();
	}
}
