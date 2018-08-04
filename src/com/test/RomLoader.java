package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public boolean LoadProgram(int startAddress, String programPath, boolean isBootStrap) {
		boolean result = true;
		List<String> rom_program_list = this.readRomProgramToStringArrList(programPath);
		int len = rom_program_list.size();
		if (len == 0) {
			return false;
		}
		
		for (int i = 0; i < len; i++) {
			cpu.SetMAR(startAddress + i);
			BitSet instruction = cpu.Encode(rom_program_list.get(i));
			if (instruction == null) {
				this.subject.updateMFR(7);
				this.subject.updateUserConsole("Encoding instruction error. Invalid instruction!!!\n");
				return false;
			}
			cpu.SetMBR(InstructionCodec.GetValueWithInt(instruction));
			result = memory.Set(startAddress + i, instruction);
			if (result == false) {
				return false;
			}
		}
		
		if (isBootStrap) {
			//Set boot end location
			cpu.SetBootEndLocation(startAddress+len);
			
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
			
			//load 0 to 6
			result = memory.LoadData(6, 0);
			//load 32767 to 7
			result = memory.LoadData(7, 32767);
			if (result == false) {
				return false;
			}
		}
		
		cpu.SetPC(startAddress);
		return true;
	}
	
	public boolean LoadTextFile(int startAddress, String filePath) {
		boolean result = true;
		//Set M[2001] and M[2002] to 1. M[2001] save the line index, M[2002] save the word index
		memory.LoadData(2001, 0);
		memory.LoadData(2002, 0);
		//Set M[2003] to 1, this is the flag to mark if find the word or not. 1 means not find, 0 means find
		memory.LoadData(2003, 1);
		//Set M[15] to 99, using calculating index address
		memory.LoadData(15, 91);
		//Set M[16] to 135, using calculating index address
		memory.LoadData(16, 141);
		//Set M[17] to 168, using calculating index address
		memory.LoadData(17, 183);
		//Set M[20] to 213, using calculating index address
		memory.LoadData(20, 225);
		//Set M[13] to 257, using flag of the string ending
		memory.LoadData(13, 257);
		//Set M[18] to 2000, using address index
		memory.LoadData(18, 2000);
		//Set M[19] to 1023, using address index
		memory.LoadData(19, 1023);
		
		File file = new File(filePath);
		String theString = "";
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			theString = scanner.nextLine();
			while (scanner.hasNextLine()) {
			       theString = theString + "\n" + scanner.nextLine();
			}
			scanner.close();
			//replace 2 or more white spaces with single space 
			theString = theString.trim().replaceAll(" +", " ");
			char[] charArray = theString.toCharArray();
			System.out.println("result is " + theString + " " + charArray.length);			
			for (int i = 0; i < charArray.length; i++) {
				result = memory.LoadData(startAddress + i, (int)charArray[i]);
				if (result == false) {
					return result;
				}
			}
			//set the next memory slot to 257 which means the end of the file
			result = memory.LoadData(startAddress +  charArray.length, 257);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Catch exception when scan the text file\n");
			e.printStackTrace();		
		}

		return result;
	}
	public boolean LoadBootStrap() {
		boolean result = LoadProgram(8, "bootstrap.txt", true);		
		return result;
	}

	public int Execute() {
		return cpu.Execute();
	}
}
