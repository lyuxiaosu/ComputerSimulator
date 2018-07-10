package com.test;

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
	private SingalController signal_controller;
	private GPR r0, r1, r2, r3;
	private String[][] GPRContent; // pairs of general purpose register and its value
	private String[][] IXContent; // pairs of index register and its value
	private IUpdate subject;

	public CentralProcessor(IUpdate subject, Memory memory) {
		this.subject = subject;
		this.memory = memory;
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
		signal_controller = new SingalController(this, memory, subject);
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
	
	// return -1 means sucessfully finish executing boostrap program
	// return 0 means successfully execute one instruction
	// return -2 means failed to executing one instruction
	// return -3 means no instruction to execute

	public int Execute() {
		int result = pc.Process(new Integer(0));
		//this.subject.updatePhase("CPU is idle");
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
	
	public int SetGPR(int index, int value) {
		if (index == 0) {
			r0.SetValue(value);
		} else if (index == 1) {
			r1.SetValue(value);
		} else if (index == 2) {
			r2.SetValue(value);
		} else if (index == 3) {
			r3.SetValue(value);
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			return -2;
		}
		
		updateGPRContent();
		return 0;
	}

	public int GetGPR(int index) {
		if (index == 0) {
			return r0.GetValueWithInt();
		} else if (index == 1) {
			return r1.GetValueWithInt();
		} else if (index == 2) {
			return r2.GetValueWithInt();
		} else if (index == 3){
			return r3.GetValueWithInt();
		} else {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + index + ". The range of GPR is 0-3\n");
			return -2;
		}
	}
	
	private void initGPRContent() {
		updateGPRContent();
	}

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
	
	public int GetIX(int index) {
		if (index == 0) {
			return 0; // no indexing
		} else if (index == 1) {
			return x1.GetValueWithInt();
		} else if (index == 2) {
			return x2.GetValueWithInt();
		} else if (index == 3) {
			return x3.GetValueWithInt();
		} else {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + index + ". The range of IX is 0-3\n");
			return -2;
		}
	}
	
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
			return -2;
		}
		
		updateIXContent(); 
		return 0;
	}
	
	public String GetCCR() {
		return ccr.GetBinaryString();
	}
	
	public String GetPC() {
		return Integer.toString(pc.GetValueWithInt());
	}
	
	public String GetIR() {
		return ir.GetBinaryString();
	}
	
	public String GetMAR() {
		return Integer.toString(mar.GetValueWithInt());
	}
	
	public String GetMBR() {
		return mbr.GetBinaryString();
	}
	
	public String GetMFR() {
		return Integer.toString(mfr.GetValueWithInt());
	}
	
	//codeId :0	Illegal Memory Address to Reserved Locations
	//codeId :1	Illegal TRAP code
	//codeId :2	Illegal Operation Code
	//codeId :3	Illegal Memory Address beyond 2048 (memory installed)

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
	public String GetMSR() {
		return msr.GetBinaryString();
	}
}
