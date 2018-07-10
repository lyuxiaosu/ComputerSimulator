package com.test;

import java.awt.EventQueue;
import java.lang.Thread;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JList;
import javax.swing.JTable;
import java.awt.TextField;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.ScrollPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;

public class ComputerSimulator implements Runnable, ActionListener, IUpdate {

	private JFrame frmComputerSimulator;
	private JButton btnIPL;
	private JButton btnRun;
	private JTextArea txtrConsole;
	private JLabel lblConsoleLable;
	private JTable jtMemory;
	private JScrollPane spMemory;

	private RomLoader loader;
	private CentralProcessor cpu;
	private Memory memory;
	private boolean status;
	private Thread process;
	private boolean isRunning;
	private JLabel lblMemory;
	private JScrollPane spRegister;
	private JLabel lblGPR;
	private JScrollPane spIndexRegister;
	private JLabel lblIndexRegister;
	private JButton btnSingleStep;
	private JButton btnHalt;
	private JTextField tfCCR;
	private JLabel lblPC;
	private JTextField tfPC;
	private JLabel lblIR;
	private JTextField tfIR;
	private JLabel lblMAR;
	private JTextField tfMAR;
	private JLabel lblMBR;
	private JTextField tfMBR;
	private JLabel lblMFR;
	private JTextField tfMFR;
	private JLabel lblMSR;
	private JTextField tfMSR;
	private JScrollPane spConsole;
	private JTable jtRegister;
	private JTable jtIndexRegister;
	private JLabel lblPhase;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ComputerSimulator window = new ComputerSimulator();
					window.frmComputerSimulator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ComputerSimulator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		status = false;
		isRunning = false;
		process = null;
		memory = new Memory(this);
		cpu = new CentralProcessor(this, memory);
		loader = new RomLoader(cpu, memory);

		frmComputerSimulator = new JFrame();
		frmComputerSimulator.setTitle("Computer Simulator");
		frmComputerSimulator.setBounds(100, 100, 1200, 1000);
		frmComputerSimulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmComputerSimulator.getContentPane().setLayout(null);

		// add button "IPL"
		btnIPL = new JButton("IPL");
		btnIPL.setForeground(Color.RED);
		btnIPL.setFont(new Font("宋体", Font.BOLD, 20));
		btnIPL.setBounds(14, 550, 113, 27);
		frmComputerSimulator.getContentPane().add(btnIPL);
		btnIPL.addActionListener(this);

		lblConsoleLable = new JLabel("Console");
		lblConsoleLable.setFont(new Font("宋体", Font.BOLD, 20));
		lblConsoleLable.setForeground(Color.BLACK);
		lblConsoleLable.setBounds(14, 590, 113, 41);
		frmComputerSimulator.getContentPane().add(lblConsoleLable);

		// add button "Run"
		btnRun = new JButton("Run");
		btnRun.setForeground(Color.RED);
		btnRun.setFont(new Font("宋体", Font.BOLD, 20));
		btnRun.setBounds(149, 550, 113, 27);
		frmComputerSimulator.getContentPane().add(btnRun);
		btnRun.addActionListener(this);

		// Memory
		jtMemory = new JTable();
		jtMemory.setFont(new Font("宋体", Font.PLAIN, 18));
		jtMemory.setBounds(900, 45, 72, 18);
		spMemory = new JScrollPane(jtMemory);
		spMemory.setSize(381, 874);
		spMemory.setLocation(787, 66);
		frmComputerSimulator.getContentPane().add(spMemory);

		lblMemory = new JLabel("Memory");
		lblMemory.setFont(new Font("宋体", Font.BOLD, 20));
		lblMemory.setBounds(935, 45, 72, 18);
		frmComputerSimulator.getContentPane().add(lblMemory);
		// GPR
		jtRegister = new JTable();
		jtRegister.setFont(new Font("宋体", Font.PLAIN, 20));
		spRegister = new JScrollPane(jtRegister);
		spRegister.setBounds(14, 307, 242, 89);
		frmComputerSimulator.getContentPane().add(spRegister);

		lblGPR = new JLabel("GPR");
		lblGPR.setFont(new Font("宋体", Font.BOLD, 20));
		lblGPR.setBounds(14, 286, 72, 18);
		frmComputerSimulator.getContentPane().add(lblGPR);

		// IX
		jtIndexRegister = new JTable();
		jtIndexRegister.setFont(new Font("宋体", Font.PLAIN, 20));
		spIndexRegister = new JScrollPane(jtIndexRegister);
		spIndexRegister.setBounds(14, 441, 242, 73);
		frmComputerSimulator.getContentPane().add(spIndexRegister);

		lblIndexRegister = new JLabel("IX");
		lblIndexRegister.setFont(new Font("宋体", Font.BOLD, 20));
		lblIndexRegister.setBounds(14, 420, 72, 18);
		frmComputerSimulator.getContentPane().add(lblIndexRegister);

		// add button "Single Step"
		btnSingleStep = new JButton("Single Step");
		btnSingleStep.setFont(new Font("宋体", Font.BOLD, 20));
		btnSingleStep.setForeground(Color.RED);
		btnSingleStep.addActionListener(this);
		btnSingleStep.setBounds(284, 550, 190, 27);
		frmComputerSimulator.getContentPane().add(btnSingleStep);

		// add button "Halt"
		btnHalt = new JButton("Halt");
		btnHalt.setForeground(Color.RED);
		btnHalt.setFont(new Font("宋体", Font.BOLD, 20));
		btnHalt.addActionListener(this);
		btnHalt.setBounds(498, 550, 113, 27);
		frmComputerSimulator.getContentPane().add(btnHalt);

		// CCR
		JLabel lblCCR = new JLabel("CCR");
		lblCCR.setFont(new Font("宋体", Font.BOLD, 20));
		lblCCR.setBounds(14, 45, 39, 18);
		frmComputerSimulator.getContentPane().add(lblCCR);

		tfCCR = new JTextField();
		tfCCR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfCCR.setEditable(false);
		tfCCR.setBounds(67, 42, 185, 24);
		frmComputerSimulator.getContentPane().add(tfCCR);
		tfCCR.setColumns(10);

		// Program counter
		lblPC = new JLabel("PC");
		lblPC.setFont(new Font("宋体", Font.BOLD, 20));
		lblPC.setBounds(14, 76, 39, 18);
		frmComputerSimulator.getContentPane().add(lblPC);

		tfPC = new JTextField();
		tfPC.setFont(new Font("宋体", Font.PLAIN, 20));
		tfPC.setBounds(67, 73, 185, 24);
		frmComputerSimulator.getContentPane().add(tfPC);
		tfPC.setColumns(10);
		

		// Instruction register
		lblIR = new JLabel("IR");
		lblIR.setFont(new Font("宋体", Font.BOLD, 20));
		lblIR.setBounds(14, 113, 23, 18);
		frmComputerSimulator.getContentPane().add(lblIR);

		tfIR = new JTextField();
		tfIR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfIR.setEditable(false);
		tfIR.setBounds(67, 110, 185, 24);
		frmComputerSimulator.getContentPane().add(tfIR);
		tfIR.setColumns(10);

		// memory address register
		lblMAR = new JLabel("MAR");
		lblMAR.setFont(new Font("宋体", Font.BOLD, 20));
		lblMAR.setBounds(14, 150, 39, 18);
		frmComputerSimulator.getContentPane().add(lblMAR);

		tfMAR = new JTextField();
		tfMAR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfMAR.setEditable(false);
		tfMAR.setBounds(67, 147, 185, 24);
		frmComputerSimulator.getContentPane().add(tfMAR);
		tfMAR.setColumns(10);

		// memory buffer register
		lblMBR = new JLabel("MBR");
		lblMBR.setFont(new Font("宋体", Font.BOLD, 20));
		lblMBR.setBounds(14, 181, 72, 18);
		frmComputerSimulator.getContentPane().add(lblMBR);

		tfMBR = new JTextField();
		tfMBR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfMBR.setEditable(false);
		tfMBR.setBounds(67, 178, 185, 24);
		frmComputerSimulator.getContentPane().add(tfMBR);
		tfMBR.setColumns(10);

		// machine fault register
		lblMFR = new JLabel("MFR");
		lblMFR.setFont(new Font("宋体", Font.BOLD, 20));
		lblMFR.setBounds(14, 212, 39, 18);
		frmComputerSimulator.getContentPane().add(lblMFR);

		tfMFR = new JTextField();
		tfMFR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfMFR.setEditable(false);
		tfMFR.setBounds(67, 212, 185, 24);
		frmComputerSimulator.getContentPane().add(tfMFR);
		tfMFR.setColumns(10);
		// machine status register
		lblMSR = new JLabel("MSR");
		lblMSR.setFont(new Font("宋体", Font.BOLD, 20));
		lblMSR.setBounds(14, 252, 39, 18);
		frmComputerSimulator.getContentPane().add(lblMSR);

		tfMSR = new JTextField();
		tfMSR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfMSR.setEditable(false);
		tfMSR.setBounds(67, 249, 185, 24);
		frmComputerSimulator.getContentPane().add(tfMSR);
		tfMSR.setColumns(10);

		spConsole = new JScrollPane();
		spConsole.setBounds(14, 630, 600, 310);
		frmComputerSimulator.getContentPane().add(spConsole);

		// add txt area showing a simple console
		txtrConsole = new JTextArea();
		txtrConsole.setLineWrap(true);
		txtrConsole.setFont(new Font("Monospaced", Font.PLAIN, 18));
		spConsole.setViewportView(txtrConsole);
		
		lblPhase = new JLabel("Powered On");
		lblPhase.setFont(new Font("宋体", Font.PLAIN, 20));
		lblPhase.setForeground(Color.BLUE);
		lblPhase.setBounds(145, 601, 218, 18);
		frmComputerSimulator.getContentPane().add(lblPhase);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRun) {
			if (status == false) {
				txtrConsole.append("The computer hasn't started yet\n");
				return;
			}

			if (process == null || !process.isAlive()) {
				isRunning = true;
				process = new Thread(this);
				process.start();
				txtrConsole.append("Running\n");
			} else {
				resumeExecution();
			}
		} else if (e.getSource() == btnIPL) {
			boolean result = loader.LoadProgram();
			if (result == true) {
				txtrConsole.append("Success to load the boostrap program to memory !!!\n");
				updatePhase("bootstrap Loaded");
				status = true;
				cpu.updateStatus(status);
				Display();
			} else {
				txtrConsole.append("Failed to load the boostrap program to memory.Shutdown!!!\n");
				updatePhase("Failed to load bootstrap");  
				status = false;
				cpu.updateStatus(status);
				cpu.ShutDown();
			}
		} else if (e.getSource() == btnSingleStep) {
			if (status == true && !isRunning) {
				txtrConsole.append("Single step\n");
				int result = cpu.Execute();
				if (result == -1) { // complete boostrap program
					cpu.SetPC(8); // when finish executing boostrap program, Returning to the boot program means
									// that it prompts the user to either run the currently loaded program again or
									// to load a new program and run it.
					txtrConsole.append("Finish executing boot program, CPU is idle\n");
				} else if (result == -2) {
					txtrConsole.append("Failed to execute instructions, something is wrong!!!\n");
				} else if (result == -3) {
					txtrConsole.append("No instructions to execuate, CPU is idel!!!\n");
				}
			} else if (isRunning) {
				txtrConsole.append("The computer is in running, cannot execute instruction with single step\n");
			} else {
				txtrConsole.append("The computer hasn't started yet\n");
			}
		} else if (e.getSource() == btnHalt) {
			if (isRunning || status) {
				StopMachine();
			}
		}
	}

	public void run() {
		while (isRunning) {
			// return -1 means sucessfully finish executing boostrap program
			// return 0 means successfully execute one instruction
			// return -2 means failed to executing one instruction
			// return -3 means no instruction to execute

			int result = cpu.Execute();
			if (result == -1) { // complete boostrap program
				cpu.SetPC(8); // when finish executing boostrap program, Returning to the boot program means
								// that it prompts the user to either run the currently loaded program again or
								// to load a new program and run it.
				txtrConsole.append("Finish executing boot program, CPU is idle\n");
				isRunning = false;
			} else if (result == -2) {
				txtrConsole.append("Failed to execute instructions, something is wrong!!!\n");
				isRunning = false;
			} else if (result == -3) {
				txtrConsole.append("No instructions to execuate, CPU is idel!!!\n");
				try {
					process.sleep(1000); // to slow down since there is no instructions
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (result == 0) {
				isRunning = true;
				txtrConsole.append("Sucessfully execuate instructions\n");
			} else {
				isRunning = true;
				txtrConsole.append("No resturn code to define, abnormal\n");
			}
		}
	}

	void resumeExecution() {
		txtrConsole.append("Resume. Running\n");
		isRunning = true;
		if (!process.isAlive()) {
			process.start();
		}
	}

	void StopMachine() {
		txtrConsole.append("Computer is shutdowning\n");
		isRunning = false;
		status = false;
		ResetMemory();
		ResetRegisters();
		txtrConsole.append("Computer shutdowned\n");
	}

	void ResetMemory() {

	}

	void ResetRegisters() {

	}

	@Override
	public void updateData(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Memory) {
			String memoryColumn[] = { "Address", "Value" };
			if (jtMemory != null) {
				DefaultTableModel memoryTableModel = (DefaultTableModel) jtMemory.getModel();
				if (memoryTableModel != null) {
					memoryTableModel.setDataVector(memory.GetContent(), memoryColumn);
				}
			}		
		} else if (obj instanceof PC) {
			if (tfPC != null) {
				tfPC.setText(cpu.GetPC());
			}
		} else if (obj instanceof MAR) {
			if (tfMAR != null) {
				tfMAR.setText(cpu.GetMAR());
			}
		} else if (obj instanceof MBR) {
			if (tfMBR != null) {
				tfMBR.setText(cpu.GetMBR());
			}
		} else if (obj instanceof IR) {
			if (tfIR != null) {
				tfIR.setText(cpu.GetIR());
			}
		} else if (obj instanceof GPR) {
			System.out.println("update GPR");
			String GPRColumn[] = { "Register", "Value" };
			if (jtRegister != null) {
				DefaultTableModel registerTableModel = (DefaultTableModel) jtRegister.getModel();
				if (registerTableModel != null) {
					registerTableModel.setDataVector(cpu.GetGPRContent(), GPRColumn);
				}
			}
		} else if (obj instanceof IndexRegister) {
			if (jtIndexRegister != null) {
				String Column[] = { "Register", "Value" };
				DefaultTableModel IXTableModel = (DefaultTableModel) jtIndexRegister.getModel();
				if (IXTableModel != null) {
					IXTableModel.setDataVector(cpu.GetIXContent(), Column);
				}
			}
		} else if (obj instanceof MFR) {
			if (tfMFR != null) {
				tfMFR.setText(cpu.GetMFR());
			}
		}
	}

	@Override
	public void updateUserConsole(String message) {
		txtrConsole.append(message);
	}

	@Override
	public void updateMFR(int value) {
		cpu.SetMFR(value);
	}
	
	@Override
	public void updatePhase(String message) {
		lblPhase.setText(message);
	}
	void Display() {
		// Display memory content
		String memoryColumn[] = { "Address", "Value" };
		DefaultTableModel memoryTableModel = (DefaultTableModel) jtMemory.getModel();
		memoryTableModel.setDataVector(memory.GetContent(), memoryColumn);

		// Display GPRs content
		String GPRColumn[] = { "Register", "Value" };
		DefaultTableModel registerTableModel = (DefaultTableModel) jtRegister.getModel();
		registerTableModel.setDataVector(cpu.GetGPRContent(), GPRColumn);

		// Display Index registers' content
		DefaultTableModel IXTableModel = (DefaultTableModel) jtIndexRegister.getModel();
		IXTableModel.setDataVector(cpu.GetIXContent(), GPRColumn);

		// Display condition code register
		tfCCR.setText(cpu.GetCCR());

		// Display PC
		tfPC.setText(cpu.GetPC());

		// Display IR
		tfIR.setText(cpu.GetIR());

		// Display MAR
		tfMAR.setText(cpu.GetMAR());
		// Display MBR
		tfMBR.setText(cpu.GetMBR());

		// Display MFR
		tfMFR.setText(cpu.GetMFR());

		// Display MSR
		tfMSR.setText(cpu.GetMSR());
	}
}
