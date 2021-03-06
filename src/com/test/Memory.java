package com.test;

import java.util.*;
import java.util.stream.IntStream;

public class Memory {
	private BitSet[] memory;
	private int nbits;
	private InstructionCodec codec;
	private String[][] content; // pair of address and value of memory
	private IUpdate subject;

	public Memory(IUpdate subject) {
		this.subject = subject;
		memory = new BitSet[2048];
		content = new String[2048][2];
		codec = new InstructionCodec(subject);
		nbits = 16;
		init();
	}

	/**
	 * initialize each memory location to null
	 */
	private void init() {
		for (int i = 0; i < 2048; i++) {
			memory[i] = null;
		}
		initContent();
	}

	/**
	 * Fetch memory content by specifying the index
	 * 
	 * @param index
	 * @return
	 */
	public BitSet Get(int index) {
		if (index < 0 || index >= 2048) { // beyond the memory addressing range. It should be [0-2047]
			this.subject.updateUserConsole("Out of memory, index " + index + ". Error !!!!\n");
			this.subject.updateMFR(3);
			;
			return null;
		}

		if (memory[index] == null) { // Try to access an no pre-allocated memory, return null
			this.subject.updateUserConsole("Access an unallocated memory address:" + index + " ###\n");
			this.subject.updateMFR(4);
			return null;
		}
		this.subject.updateMAR(index);
		int int_content = InstructionCodec.GetValueWithInt(memory[index]);
		this.subject.updateMBR(int_content);
		return memory[index];
	}

	public boolean Set(int index, BitSet content) {
		if (index < 0 || index >= 2048) { // beyond the memory addressing range. It should be [0-2047]
			this.subject.updateUserConsole("Out of memory, index " + index + ". Error !!!!\n");
			this.subject.updateMFR(3);
			return false;
		}

		if (index <= 5) { // Memory location [0-5] is reserved, which cannot be written
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return false;
		}

		memory[index] = content;
		this.subject.updateMAR(index);
		this.subject.updateMBR(InstructionCodec.GetValueWithInt(memory[index]));
		updateContent();
		this.subject.updateData(this);

		return true;
	}

	public int Set(int index, int value, boolean writeWithCommand) {
		if (index < 0 || index >= 2048) { // beyond the memory addressing range. It should be [0-2047]
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, index " + index + ". Error !!!!\n");
			return -2;
		}

		if (index <= 5) { // Memory location [0-5] is reserved, which cannot be written
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return -2;
		}

		if (memory[index] == null) {
			BitSet bt = new BitSet(nbits);
			memory[index] = bt;
		}

		this.subject.updateMAR(index);
		this.subject.updateMBR(value);

		memory[index].clear(); // Before reseting the BitSet object, clean the previous value
		if (writeWithCommand == false) {
			if (value < -32767 || value > 32767) {
				this.subject.updateUserConsole("Invalid number. The range of number is [-32767, 32767]\n");
				return -2;
			}
			int abs_value = java.lang.Math.abs((int) value);
			int ix = 0;
			while (abs_value != 0L) {
				if (abs_value % 2L != 0) {
					memory[index].set(ix);
				}
				++ix;
				abs_value = abs_value >>> 1;
			}

			if (value < 0) {
				memory[index].set(nbits - 1);
			}
		} else {
			int ix = 0;
			while (value != 0L) {
				if (value % 2L != 0) {
					memory[index].set(ix);
				}
				++ix;
				value = value >>> 1;
			}
		}

		updateContent();
		this.subject.updateData(this);

		return 0;
	}

	/**
	 * return the specified memory content as the format of Integer
	 * 
	 * @param index
	 *            the location to be fetched the data
	 * @return
	 */
	public Integer GetValueWithInt(int index, boolean readWithCommand) {
		if (index < 0 || index >= 2048) {// beyond the memory addressing range. It should be [0-2047]
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, index " + index + ". Error !!!!\n");
			return null;
		}

		if (memory[index] == null) {
			this.subject.updateUserConsole("Access an unallocated memory address:" + index + " !!!!\n");
			this.subject.updateMFR(4);
			return null;
		}

		BitSet bitset = memory[index];
		this.subject.updateMAR(index);// once get the content, update MAR to this value
		int bitInteger = 0;
		if (readWithCommand) {
			for (int i = 0; i < nbits; i++)
				if (bitset.get(i))
					bitInteger |= (1 << i);
		} else {
			for (int i = 0; i < nbits - 1; i++)
				if (bitset.get(i))
					bitInteger |= (1 << i);

			if (bitset.get(nbits - 1) == true) {
				bitInteger = -bitInteger;
			}
		}

		this.subject.updateMBR(bitInteger);
		return new Integer(bitInteger);
	}

	/**
	 * Set integer content to the reserved memory location by specifying index
	 * 
	 * @param index
	 * @param content
	 * @return true: success to set; false: failed to set
	 */
	public boolean SetReservedMemory(int index, int value) {
		if (index < 0 || index > 5) {
			this.subject.updateUserConsole("Try to write content to unreserved memory, error\n");
			this.subject.updateMFR(2);
			return false;
		}

		if (memory[index] == null) {
			BitSet bt = new BitSet(nbits);
			memory[index] = bt;
		}

		memory[index].clear();
		int ix = 0;

		while (value != 0L) {
			if (value % 2L != 0) {
				memory[index].set(ix);
			}
			++ix;
			value = value >>> 1;
		}

		updateContent();
		this.subject.updateData(this);

		return true;

	}

	/**
	 * Set String content to the reserved memory location by specifying index
	 * 
	 * @param index
	 * @param Content
	 * @return true: success to set; false: failed to set
	 */
	public boolean SetReservedMemory(int index, String Content) {
		if (index < 0 || index > 5) {
			this.subject.updateUserConsole("Try to write content to unreserved memory, error\n");
			this.subject.updateMFR(2);
			return false;
		}
		BitSet instruction = codec.Encode(Content); // Encode the instruction to binary code first
		if (instruction == null) {
			this.subject.updateMFR(7);
			this.subject.updateUserConsole("Encoding instruction error!!!\n");
			return false;
		}

		memory[index] = instruction;
		updateContent();
		this.subject.updateData(this);
		return true;

	}

	/**
	 * Set String content to the reserved memory location by specifying index
	 * 
	 * @param index
	 * @param Content
	 * @return true: success to set; false: failed to set
	 */
	public boolean LoadContent(int index, String Content) {
		this.subject.updatePhase("Loading");
		if (index < 0 || index >= 2048) {
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, index " + index + ". Error !!!!\n");
			return false;
		}

		if (index <= 5) {
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return false;
		}

		BitSet instruction = codec.Encode(Content); // Encode the instruction to binary code first
		if (instruction == null) {
			this.subject.updateMFR(7);
			this.subject.updateUserConsole("Encoding instruction error. Invalid instruction!!!\n");
			return false;
		}

		memory[index] = instruction;
		updateContent();
		this.subject.updateData(this);
		return true;
	}

	/**
	 * Set integer content to the memory location by specifying index
	 * 
	 * @param index
	 * @param data
	 * @return true: success to set; false: failed to set
	 */
	public boolean LoadData(int index, int data) {
		this.subject.updatePhase("Loading");
		int result = this.Set(index, data, false);
		this.subject.updateMAR(index);
		this.subject.updateMBR(data);
		if (result == 0) {
			return true;
		} else {
			return false;
		}
	}

	public String[][] GetContent() {
		return content;
	}

	private void initContent() {
		updateContent();
	}

	/**
	 * update all memory content
	 */
	private void updateContent() {
		for (int i = 0; i < 2048; i++) {
			String address = Integer.toString(i);
			content[i][0] = address;
			if (memory[i] != null) {
				content[i][1] = getBinaryString(memory[i]);
			} else {
				content[i][1] = "";
			}
		}
	}

	private String getBinaryString(BitSet bs) {

		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nbits; i++) {
			s.append(bs.get(i) == true ? 1 : 0);
		}
		return s.toString();
	}

	public void SetIndirectAddress(boolean indirectAddress) {
		this.codec.SetIndirectAddress(indirectAddress);
	}

	public void Reset() {
		init();
		this.subject.updateData(this);
	}
}
