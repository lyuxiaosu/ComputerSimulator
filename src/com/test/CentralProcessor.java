package com.test;

import java.util.BitSet;

public class CentralProcessor {
	private Memory memory;
	private PC pc;
	private CCR ccr;
	private IR ir;
	private MAR mar;
	private MBR mbr;
	private MFR mfr;
	private MSR msr;
	private IndexRegister x1, x2, x3;
	private InstructionDecoder instruction_decoder;
	private InstructionEncoder instruction_encoder;
	private SingalController signal_controller;
	private GPR r0, r1, r2, r3;
	private String[][] GPRContent; // pairs of general purpose register and its value
	private String[][] IXContent; // pairs of index register and its value
	private IUpdate subject;
	private IStop simulator;
	private IGetInput input;

	public CentralProcessor(IUpdate subject, IStop simulator, IGetInput input, Memory memory) {
		this.subject = subject;
		this.memory = memory;
		this.simulator = simulator;
		this.input = input;
		init();
	}

	private void init() {
		pc = new PC(subject);
		ccr = new CCR(subject);
		ir = new IR(subject);
		mar = new MAR(subject);
		mbr = new MBR(subject, memory);
		mfr = new MFR(subject);
		msr = new MSR(subject);
		x1 = new IndexRegister(subject);
		x2 = new IndexRegister(subject);
		x3 = new IndexRegister(subject);
		instruction_decoder = new InstructionDecoder(subject);
		instruction_encoder = new InstructionEncoder(subject);
		signal_controller = new SingalController(this, memory, subject, simulator, input);
		r0 = new GPR(subject);
		r1 = new GPR(subject);
		r2 = new GPR(subject);
		r3 = new GPR(subject);

		GPRContent = new String[4][2];
		IXContent = new String[3][2];

		initGPRContent();
		initIXContent();

		pc.AddNext(mar).AddNext(mbr).AddNext(ir).AddNext(instruction_decoder).AddNext(signal_controller);
	}
	
	/**
	 * Execute one instruction. The return values can be the followings:
	 * -1 means sucessfully finish executing boostrap program
	 * 0 means successfully execute one instruction
	 * -2 means failed to executing one instruction
	 * -3 means no instruction to execute
	 */
	
	public int Execute() {
		int result = pc.Process(new Integer(0));
		return result;
	}

	public void ShutDown() {

	}

	public void updateStatus(boolean status) {
		msr.SetStatus(status);
	}

	public void SetPC(int address) {
		pc.SetValue(address);
	}
	
	public String[][] GetGPRContent() {
		return GPRContent;
	}
	
	/** 
	 * Set value to the specified GPR
	 */
	public int SetGPR(int index, int value) {
		if (index == 0) {
			int old_r0 = r0.GetValueWithInt();
			String old_binary = r0.GetBinaryString();
			r0.SetValue(value);
			String new_binary = r0.GetBinaryString();
			this.subject.updateUserConsole("GPR-0 from " + old_r0 + "(" + old_binary + ") to " + value + "(" + new_binary + ")\n");
		} else if (index == 1) {
			int old_r1 = r1.GetValueWithInt();
			String old_binary = r1.GetBinaryString();
			r1.SetValue(value);
			String new_binary = r1.GetBinaryString();
			this.subject.updateUserConsole("GPR-1 from " + old_r1 + "(" + old_binary + ") to " + value + "(" + new_binary + ")\n");
		} else if (index == 2) {
			int old_r2 = r2.GetValueWithInt();
			String old_binary = r2.GetBinaryString();
			r2.SetValue(value);
			String new_binary = r2.GetBinaryString();
			this.subject.updateUserConsole("GPR-2 from " + old_r2 + "(" + old_binary + ") to " + value + "(" + new_binary + ")\n");
		} else if (index == 3) {
			int old_r3 = r3.GetValueWithInt();
			String old_binary = r3.GetBinaryString();
			r3.SetValue(value);
			String new_binary = r3.GetBinaryString();
			this.subject.updateUserConsole("GPR-3 from " + old_r3 + "(" + old_binary + ") to " + value + "(" + new_binary + ")\n");
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			this.SetMFR(6);
			return -2;
		}
		
		updateGPRContent();
		return 0;
	}
	/**
	 * Get value of the specified GPR
	 * Return null means invalid GPR index
	 */
	public Integer GetGPR(int index) {
		if (index == 0) {
			return new Integer(r0.GetValueWithInt());
		} else if (index == 1) {
			return new Integer(r1.GetValueWithInt());
		} else if (index == 2) {
			return new Integer(r2.GetValueWithInt());
		} else if (index == 3){
			return new Integer(r3.GetValueWithInt());
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			this.SetMFR(6);
			return null;
		}
	}
	
	public BitSet GetGPRWithBitSet(int index) {
		if (index == 0) {
			return r0.Get();
		} else if (index == 1) {
			return r1.Get();
		} else if (index == 2) {
			return r2.Get();
		} else if (index == 3){
			return r3.Get();
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			this.SetMFR(6);
			return null;
		}
	}
	
	public void SetGPRWithBitSet(int index, BitSet bitset) {
		if (index == 0) {
			int old_r0 = r0.GetValueWithInt();
			String old_binary = r0.GetBinaryString();
			r0.Set(bitset);
			int new_r0 = r0.GetValueWithInt();
			String new_binary = r0.GetBinaryString();
			this.subject.updateUserConsole("GPR-0 from " + old_r0 + "(" + old_binary + ") to " + new_r0 + "(" + new_binary + ")\n");
		} else if (index == 1) {
			int old_r1 = r1.GetValueWithInt();
			String old_binary = r1.GetBinaryString();
			r1.Set(bitset);
			int new_r1 = r1.GetValueWithInt();
			String new_binary = r1.GetBinaryString();
			this.subject.updateUserConsole("GPR-1 from " + old_r1 + "(" + old_binary + ") to " + new_r1 + "(" + new_binary + ")\n");		
		} else if (index == 2) {
			int old_r2 = r2.GetValueWithInt();
			String old_binary = r2.GetBinaryString();
			r2.Set(bitset);
			int new_r2 = r2.GetValueWithInt();
			String new_binary = r2.GetBinaryString();
			this.subject.updateUserConsole("GPR-2 from " + old_r2 + "(" + old_binary + ") to " + new_r2 + "(" + new_binary + ")\n");		
		} else if (index == 3){
			int old_r3 = r3.GetValueWithInt();
			String old_binary = r3.GetBinaryString();
			r3.Set(bitset);
			int new_r3 = r3.GetValueWithInt();
			String new_binary = r3.GetBinaryString();
			this.subject.updateUserConsole("GPR-3 from " + old_r3 + "(" + old_binary + ") to " + new_r3 + "(" + new_binary + ")\n");		
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			this.SetMFR(6);
		}
		
		updateGPRContent();
	}
	
	private void initGPRContent() {
		updateGPRContent();
	}
	/**
	 * update GPRContent's content. GPRContent holds the pairs of GPR's index and its value
	 */
	private void updateGPRContent() {
		// update GPRs content
		System.out.println("gpr[2]:" + r2.GetValueWithInt());
		GPRContent[0][0] = "0";
		GPRContent[0][1] = Integer.toString(r0.GetValueWithInt());
		GPRContent[1][0] = "1";
		GPRContent[1][1] = Integer.toString(r1.GetValueWithInt());
		GPRContent[2][0] = "2";
		GPRContent[2][1] = Integer.toString(r2.GetValueWithInt());
		GPRContent[3][0] = "3";
		GPRContent[3][1] = Integer.toString(r3.GetValueWithInt());
		this.subject.updateData(r0);
	}

	private void initIXContent() {
		updateIXContent();
	}
	/**
	 * update IXContent's content. IXContent holds the pairs of IX's index and its value
	 */
	private void updateIXContent() {
		IXContent[0][0] = "1";
		IXContent[0][1] = Integer.toString(x1.GetValueWithInt());
		IXContent[1][0] = "2";
		IXContent[1][1] = Integer.toString(x2.GetValueWithInt());
		IXContent[2][0] = "3";
		IXContent[2][1] = Integer.toString(x3.GetValueWithInt());
		this.subject.updateData(x1);
	}

	public String[][] GetIXContent() {
		return IXContent;
	}
	/**
	 * Get IX's value by specifying its index
	 */
	public Integer GetIX(int index) {
		if (index == 0) {
			return new Integer(0); // no indexing
		} else if (index == 1) {
			return new Integer(x1.GetValueWithInt());
		} else if (index == 2) {
			return new Integer(x2.GetValueWithInt());
		} else if (index == 3) {
			return new Integer(x3.GetValueWithInt());
		} else {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + index + ". The range of IX is 0-3\n");
			this.SetMFR(5);
			return null;
		}
	}
	/**
	 * Set IX's value by specifying its index
	 */
	public int SetIX(int index, int value) {
		if (index == 0) {
			return 0;
		} else if (index == 1) {
			x1.SetValue(value);
		} else if (index == 2) {
			x2.SetValue(value);
		} else if (index == 3) {
			x3.SetValue(value);
		} else {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + index + ". The range of IX is 0-3\n");
			this.SetMFR(5);
			return -2;
		}
		
		updateIXContent(); 
		return 0;
	}
	/** 
	 * Get CCR's value with String format
	 */
	public String GetCCR() {
		return ccr.GetBinaryString();
	}
	/**
	 * Get CCR's bit value
	 */
	public boolean GetCCRBit(int index) {
		return ccr.Get(index);
	}
	/**
	 * Set CCR's bit value
	 */
	public void SetCCRBit(int index) {
		ccr.Set(index);
	}
	/**
	 * Reset CCR's bit value to 0
	 */
	public void ResetCCR() {
		ccr.SetValue(0);
	}
	
	/**
	 * Get PC's value with String format
	 */
	public String GetPC() {
		return Integer.toString(pc.GetValueWithInt());
	}
	/**
	 * Get IR's value with String format
	 */
	public String GetIR() {
		return ir.GetBinaryString();
	}
	/**
	 * Get MAR's value with String format
	 */
	public String GetMAR() {
		return Integer.toString(mar.GetValueWithInt());
	}
	/**
	 * Get MBR's value with String format
	 */
	public String GetMBR() {
		return mbr.GetBinaryString();
	}
	public int GetMBRWithInt() {
		return mbr.GetValueWithInt();
	}
	
	/**
	 * Get MFR's value with String format
	 */
	public String GetMFR() {
		return Integer.toString(mfr.GetValueWithInt());
	}
	
	/**
	 * Set MFR with fault code ID. The possible fault code IDs are:
	 * codeId :0	Illegal Memory Address to Reserved Locations
	 * codeId :1	Illegal TRAP code
	 * codeId :2	Illegal Operation Code
	 * codeId :3	Illegal Memory Address beyond 2048 (memory installed)
	 */	
	public void SetMFR(int codeId) {
		mfr.SetValue(codeId);
		//When machine fault happened, save pc's content to memory address 4,
		//then set pc to memory address 1 to let processor execute the machine fault instruction
		int pc_content = pc.GetValueWithInt();
		if (pc_content == 1) { //The PC content has already been set with memory address 1, which means this is a duplicate setting
			return;
		}
		
		System.out.println("pc instruction is " + pc_content);
		memory.SetReservedMemory(4, pc_content);
		pc.SetValue(1);
	}
	/**
	 * Get MSR's value with String format
	 */
	public String GetMSR() {
		return msr.GetBinaryString();
	}
	/**
	 * Set if enable indirect addressing or not. true means enable indirect addressing mode
	 */
	public void SetIndirectAddress(boolean indirectAddress) {
		this.instruction_decoder.SetIndirectAddress(indirectAddress);
		this.instruction_encoder.SetIndirectAddress(indirectAddress);
	}
	/**
	 * Set machine status to MSR
	 */
	public void SetMSR(int status) {
		msr.SetValue(status);		
	}
	/**
	 * Set value to MAR
	 */
	public void SetMAR(int value) {
		mar.SetValue(value);
	}
	/**
	 * Set value to MBR
	 */
	public void SetMBR(int value) {
		mbr.SetValue(value);
	}
	/**
	 * Set the instruction to IR
	 */
	public void SetIR(int value) {
		ir.SetValue(value);
	}
	/**
	 * Encode a string instruction into binary code 
	 */
	public BitSet Encode(String instruction) {
		return instruction_encoder.Encode(instruction);
	}
	/**
	 * 
	 */
	public boolean SetBootEndLocation(int location) {
		return pc.SetBootEndLocation(location);
	}
	
	public void InputNotify(int devid, String number) {
		this.signal_controller.InputNotify(devid, number);
	}
}
