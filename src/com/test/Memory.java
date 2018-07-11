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

	private void init() {
		for (int i = 0; i < 2048; i++) {
			memory[i] = null;
		}		
		initContent();
	}

	public BitSet Get(int index) {
		if (index >= 2048) {
			this.subject.updateUserConsole("Out of memory, Error !!!!\n");
			this.subject.updateMFR(3);;
			return null;
		} 
		
		if (memory[index] == null) {
			this.subject.updateUserConsole("Access an unallocated memory address:" + index +  " ###\n");
			this.subject.updateMFR(4);
			return null;
		}
		
		return memory[index];
	}

	public int Set(int index, BitSet content) {
		if (index >= 2048) {
			this.subject.updateUserConsole("Out of memory, Error !!!!\n");
			this.subject.updateMFR(3);
			return -2;
		}
		
		if (index <= 5) {
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return -2;
		}
		
		memory[index] = content;
		updateContent();
		this.subject.updateData(this);
		
		return 0;
	}

	public int Set(int index, long value) {
		if (index >= 2048) {
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, Error !!!!\n");
			return -2;
		}
		
		if (index <= 5) {
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return -2;
		}
		
		if (memory[index] == null) {
			BitSet bt = new BitSet(16);
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
		
		return 0;
	}
	public Integer GetValueWithInt(int index) {
		if (index >= 2048) {
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, Error !!!!\n");
			return null;
		}
		
		if (memory[index] == null) {
			this.subject.updateUserConsole("Access an unallocated memory address:" + index + " !!!!\n");
			this.subject.updateMFR(4);
			return null;
		}
		
		BitSet bitset = memory[index];
		int bitInteger = 0;
	    for(int i = 0 ; i < 16; i++)
	        if(bitset.get(i))
	            bitInteger |= (1 << i);
	    return new Integer(bitInteger);
	}
	
	public boolean SetReservedMemory(int index, int content) {
		if (index < 0 || index > 5) {
			this.subject.updateUserConsole("Try to write content to unreserved memory, error\n");
			this.subject.updateMFR(2);
			return false;
		}
		
		if (memory[index] == null) {
			BitSet bt = new BitSet(16);
			memory[index] = bt; 
		}
		
		memory[index].clear();
		int ix = 0;
		while (content != 0L) {
			if (content % 2L != 0) {
				memory[index].set(ix);
			}
			++ix;
			content = content >>> 1;
		}
		updateContent();
		this.subject.updateData(this);
		
		return true;
		
	}
	public boolean SetReservedMemory(int index, String Content) {
		if (index < 0 || index > 5) {
			this.subject.updateUserConsole("Try to write content to unreserved memory, error\n");
			this.subject.updateMFR(2);
			return false;
		}
		BitSet instruction = codec.Encode(Content);
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
	public boolean LoadContent(int index, String Content) {
		this.subject.updatePhase("Loading");
		if (index >= 2048) {
			this.subject.updateMFR(3);
			this.subject.updateUserConsole("Out of memory, Error !!!!\n");
			return false;
		}
		
		if (index <= 5) {
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			this.subject.updateMFR(0);
			return false;
		}
		
		BitSet instruction = codec.Encode(Content);
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
	
	public boolean LoadData(int index, int data) {
		this.subject.updatePhase("Loading");
		int result = this.Set(index, data);
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

        for( int i = 0; i < 16;  i++ )
        {
            s.append( bs.get( i ) == true ? 1: 0 );
        }
        return s.toString();
	}
	
	public void SetIndirectAddress(boolean indirectAddress) {
		this.codec.SetIndirectAddress(indirectAddress);
	}
}
