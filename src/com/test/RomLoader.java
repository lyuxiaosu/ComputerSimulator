package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class RomLoader {

	private static final String[] rom_program = new String[] { 
			"LDR 2, 1, 1", // Load register 2 with the contents of the memory location 8, with index register 1
			"STR 2, 0, 19", // Store register 2 to memory location 19, NO indexing
			"LDX 3, 19", // Load index register 1 from the content of memory location 8
			"LDA 2, 1, 17", // Load register 2 with memory address 17, with index register 1
			"STX 1, 25", // store index register 1 to address 25, with index register 1
	};

	private CentralProcessor cpu;
	private Memory memory;
	private IUpdate subject;

	public RomLoader(IUpdate subject, CentralProcessor cpu, Memory memory) {
		this.cpu = cpu;
		this.memory = memory;
		this.subject = subject;
	}

	public List<String> readRomProgramToStringArrList(String filePath) {
		List<String> list = new ArrayList<String>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					list.add(lineTxt);
				}
				bufferedReader.close();
				read.close();
			} else {
				this.subject.updateUserConsole("Cannot find bootstrap file\n");
			}
		} catch (Exception e) {
			this.subject.updateUserConsole("Read bootstrap file error\n");
			e.printStackTrace();
		}

		return list;
	}

	public boolean LoadProgram() {
		boolean result = true;
		// int len = rom_program.length;
		List<String> rom_program_list = this.readRomProgramToStringArrList("bootstrap.txt");
		int len = rom_program_list.size();
		if (len == 0) {
			return false;
		}
		
		for (int i = 0; i < len; i++) {
			cpu.SetMAR(8 + i);
			BitSet instruction = cpu.Encode(rom_program_list.get(i));
			if (instruction == null) {
				this.subject.updateMFR(7);
				this.subject.updateUserConsole("Encoding instruction error. Invalid instruction!!!\n");
				return false;
			}
			cpu.SetMBR(InstructionCodec.GetValueWithInt(instruction));
			result = memory.Set(8 + i, instruction);
			if (result == false) {
				return false;
			}
		}
		//Set boot end location
		cpu.SetBootEndLocation(8+len);
		// Initialize the reserved memory with TRAP and MFT instructions
		String trap_instruction = "TRAP 15";
		String fault_instruction = "MFT 6";
		result = memory.SetReservedMemory(0, trap_instruction);
		if (result == false) {
			return false;
		}
		result = memory.SetReservedMemory(1, fault_instruction);
		if (result == false) {
			return false;
		}
		cpu.SetPC(8);
		return result;
	}

	public int Execute() {
		return cpu.Execute();
	}
}
