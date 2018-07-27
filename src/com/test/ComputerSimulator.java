package com.test;

import java.awt.EventQueue;
import java.lang.Thread;
import java.util.BitSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
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
import javax.swing.JRadioButton;
import java.awt.Component;

public class ComputerSimulator implements Runnable, ActionListener, IUpdate, IStop, IGetInput {

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
	private JRadioButton rdbtnIndirect;
	private JTextField tfLoadPC;
	private JButton btnLoadToPC;
	private JTextField tfR0;
	private JTextField tfR1;
	private JTextField tfR2;
	private JTextField tfR3;
	private JLabel lblloadPC;
	private JButton btnR0Load;
	private JButton btnR1Load;
	private JButton btnR2Load;
	private JButton btnR3Load;
	private JLabel lblX1;
	private JTextField tfX1;
	private JButton btnX1Load;
	private JLabel lblX2;
	private JTextField tfX2;
	private JButton btnX2Load;
	private JLabel lblX3;
	private JTextField tfX3;
	private JButton btnX3Load;
	private JLabel lblInstruction;
	private JTextField tfInstruction;
	private JTextField tfInstructionAddress;
	private JLabel lblData;
	private JTextField tfData;
	private JLabel lblDataAddress;
	private JButton btnDataLoad;
	private JTextField tfDataAddress;
	private JButton btnInstructionLoad;
	private JButton btnCleanConsole;
	private JTextField tfLoadIR;
	private JTextField tfLoadToMAR;
	private JTextField tfLoadToMBR;
	private JButton btnLoadToIR;
	private JButton btnLoadToMAR;
	private JButton btnLoadToMBR;
	private JButton btnLoadData;
	private JTextField tfKeyboard;
	private JButton btnKeyboardInput;
	private JButton btnLoadTest1;
	private JButton btnExecuteTest1;

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
	 * Create the application. test submit
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
		memory = new Memory(this);
		cpu = new CentralProcessor(this, this, this, memory);
		loader = new RomLoader(this, cpu, memory);

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
		spMemory.setSize(352, 759);
		spMemory.setLocation(816, 181);
		frmComputerSimulator.getContentPane().add(spMemory);

		lblMemory = new JLabel("Memory");
		lblMemory.setFont(new Font("宋体", Font.BOLD, 20));
		lblMemory.setBounds(937, 150, 72, 18);
		frmComputerSimulator.getContentPane().add(lblMemory);
		// GPR
		jtRegister = new JTable();
		jtRegister.setFont(new Font("宋体", Font.PLAIN, 20));
		spRegister = new JScrollPane(jtRegister);
		spRegister.setBounds(14, 307, 238, 89);
		frmComputerSimulator.getContentPane().add(spRegister);

		lblGPR = new JLabel("GPR");
		lblGPR.setFont(new Font("宋体", Font.BOLD, 20));
		lblGPR.setBounds(14, 286, 72, 18);
		frmComputerSimulator.getContentPane().add(lblGPR);

		// IX
		jtIndexRegister = new JTable();
		jtIndexRegister.setFont(new Font("宋体", Font.PLAIN, 20));
		spIndexRegister = new JScrollPane(jtIndexRegister);
		spIndexRegister.setBounds(14, 464, 238, 73);
		frmComputerSimulator.getContentPane().add(spIndexRegister);

		lblIndexRegister = new JLabel("IX");
		lblIndexRegister.setFont(new Font("宋体", Font.BOLD, 20));
		lblIndexRegister.setBounds(14, 428, 72, 18);
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
		tfPC.setEditable(false);
		tfPC.setFont(new Font("宋体", Font.PLAIN, 20));
		tfPC.setBounds(67, 75, 185, 24);
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
		spConsole.setBounds(14, 630, 770, 310);
		frmComputerSimulator.getContentPane().add(spConsole);

		// add txt area showing a simple console
		txtrConsole = new JTextArea();
		txtrConsole.setLineWrap(true);
		txtrConsole.setFont(new Font("Monospaced", Font.PLAIN, 18));
		spConsole.setViewportView(txtrConsole);

		// add Phase lable
		lblPhase = new JLabel("Powered On");
		lblPhase.setFont(new Font("宋体", Font.PLAIN, 20));
		lblPhase.setForeground(Color.BLUE);
		lblPhase.setBounds(145, 601, 314, 18);
		frmComputerSimulator.getContentPane().add(lblPhase);

		// add indirect address radio
		rdbtnIndirect = new JRadioButton("Indirect addressing");
		rdbtnIndirect.setFont(new Font("宋体", Font.BOLD, 20));
		rdbtnIndirect.setBounds(45, 424, 242, 27);
		frmComputerSimulator.getContentPane().add(rdbtnIndirect);
		rdbtnIndirect.addActionListener(this);
		cpu.SetIndirectAddress(rdbtnIndirect.isSelected());
		memory.SetIndirectAddress(rdbtnIndirect.isSelected());

		// add PC load textfiled
		tfLoadPC = new JTextField();
		tfLoadPC.setFont(new Font("宋体", Font.PLAIN, 20));
		tfLoadPC.setBounds(304, 73, 79, 24);
		frmComputerSimulator.getContentPane().add(tfLoadPC);
		tfLoadPC.setColumns(10);

		// add PC load button
		btnLoadToPC = new JButton("PC Load");
		btnLoadToPC.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadToPC.setBounds(396, 72, 123, 27);
		frmComputerSimulator.getContentPane().add(btnLoadToPC);

		// add R0 label
		JLabel lblR0 = new JLabel("R0");
		lblR0.setFont(new Font("宋体", Font.BOLD, 20));
		lblR0.setBounds(279, 300, 23, 18);
		frmComputerSimulator.getContentPane().add(lblR0);
		// add R1 label
		JLabel lblR1 = new JLabel("R1");
		lblR1.setFont(new Font("宋体", Font.BOLD, 20));
		lblR1.setBounds(279, 331, 23, 18);
		frmComputerSimulator.getContentPane().add(lblR1);
		// add R2 label
		JLabel lblR2 = new JLabel("R2");
		lblR2.setFont(new Font("宋体", Font.BOLD, 20));
		lblR2.setBounds(279, 362, 23, 18);
		frmComputerSimulator.getContentPane().add(lblR2);
		// add R3 label
		JLabel lblR3 = new JLabel("R3");
		lblR3.setFont(new Font("宋体", Font.BOLD, 20));
		lblR3.setBounds(279, 393, 23, 18);
		frmComputerSimulator.getContentPane().add(lblR3);

		// add text field for load R0
		tfR0 = new JTextField();
		tfR0.setFont(new Font("宋体", Font.PLAIN, 20));
		tfR0.setBounds(304, 297, 79, 24);
		frmComputerSimulator.getContentPane().add(tfR0);
		tfR0.setColumns(10);
		// add text field for load R1
		tfR1 = new JTextField();
		tfR1.setFont(new Font("宋体", Font.PLAIN, 20));
		tfR1.setBounds(304, 330, 79, 24);
		frmComputerSimulator.getContentPane().add(tfR1);
		tfR1.setColumns(10);
		// add text field for load R2
		tfR2 = new JTextField();
		tfR2.setFont(new Font("宋体", Font.PLAIN, 20));
		tfR2.setBounds(304, 361, 79, 24);
		frmComputerSimulator.getContentPane().add(tfR2);
		tfR2.setColumns(10);

		// add text field for load R3
		tfR3 = new JTextField();
		tfR3.setFont(new Font("宋体", Font.PLAIN, 20));
		tfR3.setBounds(304, 393, 79, 24);
		frmComputerSimulator.getContentPane().add(tfR3);
		tfR3.setColumns(10);

		// add button for load PC
		lblloadPC = new JLabel("PC");
		lblloadPC.setFont(new Font("宋体", Font.BOLD, 20));
		lblloadPC.setBounds(279, 76, 23, 18);
		frmComputerSimulator.getContentPane().add(lblloadPC);
		btnLoadToPC.addActionListener(this);

		// add button for load R0
		btnR0Load = new JButton("R0 Load");
		btnR0Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnR0Load.setBounds(396, 295, 123, 27);
		frmComputerSimulator.getContentPane().add(btnR0Load);
		btnR0Load.addActionListener(this);
		// add button for load R1
		btnR1Load = new JButton("R1 Load");
		btnR1Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnR1Load.setBounds(396, 329, 123, 27);
		frmComputerSimulator.getContentPane().add(btnR1Load);
		btnR1Load.addActionListener(this);
		// add button for load R2
		btnR2Load = new JButton("R2 Load");
		btnR2Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnR2Load.setBounds(396, 360, 123, 27);
		frmComputerSimulator.getContentPane().add(btnR2Load);
		btnR2Load.addActionListener(this);
		// add button for load R3
		btnR3Load = new JButton("R3 Load");
		btnR3Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnR3Load.setBounds(396, 391, 123, 27);
		frmComputerSimulator.getContentPane().add(btnR3Load);
		btnR3Load.addActionListener(this);

		// add label for X1
		lblX1 = new JLabel("X1");
		lblX1.setFont(new Font("宋体", Font.BOLD, 20));
		lblX1.setBounds(279, 463, 23, 18);
		frmComputerSimulator.getContentPane().add(lblX1);
		// add label for X2
		lblX2 = new JLabel("X2");
		lblX2.setFont(new Font("宋体", Font.BOLD, 20));
		lblX2.setBounds(279, 492, 23, 18);
		frmComputerSimulator.getContentPane().add(lblX2);
		// add label for X3
		lblX3 = new JLabel("X3");
		lblX3.setFont(new Font("宋体", Font.BOLD, 20));
		lblX3.setBounds(279, 519, 23, 18);
		frmComputerSimulator.getContentPane().add(lblX3);

		// add text field for X1
		tfX1 = new JTextField();
		tfX1.setFont(new Font("宋体", Font.PLAIN, 20));
		tfX1.setBounds(304, 462, 79, 24);
		frmComputerSimulator.getContentPane().add(tfX1);
		tfX1.setColumns(10);

		// add text field for X2
		tfX2 = new JTextField();
		tfX2.setFont(new Font("宋体", Font.PLAIN, 20));
		tfX2.setBounds(304, 491, 79, 24);
		frmComputerSimulator.getContentPane().add(tfX2);
		tfX2.setColumns(10);

		// add text filed for X3
		tfX3 = new JTextField();
		tfX3.setFont(new Font("宋体", Font.PLAIN, 20));
		tfX3.setBounds(304, 518, 79, 24);
		frmComputerSimulator.getContentPane().add(tfX3);
		tfX3.setColumns(10);

		// add button for X1 load
		btnX1Load = new JButton("X1 Load");
		btnX1Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnX1Load.setBounds(396, 460, 123, 27);
		frmComputerSimulator.getContentPane().add(btnX1Load);
		btnX1Load.addActionListener(this);

		// add button for X2 load
		btnX2Load = new JButton("X2 Load");
		btnX2Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnX2Load.setBounds(396, 490, 123, 27);
		frmComputerSimulator.getContentPane().add(btnX2Load);
		btnX2Load.addActionListener(this);

		// add button for X3 load
		btnX3Load = new JButton("X3 Load");
		btnX3Load.setFont(new Font("宋体", Font.BOLD, 20));
		btnX3Load.setBounds(396, 517, 123, 27);
		frmComputerSimulator.getContentPane().add(btnX3Load);
		btnX3Load.addActionListener(this);

		// add label for adding instruction
		lblInstruction = new JLabel("Instruction");
		lblInstruction.setFont(new Font("宋体", Font.BOLD, 20));
		lblInstruction.setBounds(531, 45, 126, 18);
		frmComputerSimulator.getContentPane().add(lblInstruction);

		// add text field for instruction
		tfInstruction = new JTextField();
		tfInstruction.setFont(new Font("宋体", Font.PLAIN, 20));
		tfInstruction.setBounds(657, 42, 255, 24);
		frmComputerSimulator.getContentPane().add(tfInstruction);
		tfInstruction.setColumns(10);

		// add label for instruction's address
		JLabel lblInstructionAddress = new JLabel("Address");
		lblInstructionAddress.setFont(new Font("宋体", Font.BOLD, 20));
		lblInstructionAddress.setBounds(913, 45, 79, 18);
		frmComputerSimulator.getContentPane().add(lblInstructionAddress);

		// add text field for instruction's address
		tfInstructionAddress = new JTextField();
		tfInstructionAddress.setFont(new Font("宋体", Font.PLAIN, 20));
		tfInstructionAddress.setBounds(993, 42, 86, 24);
		frmComputerSimulator.getContentPane().add(tfInstructionAddress);
		tfInstructionAddress.setColumns(10);

		// add button for loading instruction
		btnInstructionLoad = new JButton("Load");
		btnInstructionLoad.setFont(new Font("宋体", Font.BOLD, 20));
		btnInstructionLoad.setBounds(1082, 41, 86, 27);
		frmComputerSimulator.getContentPane().add(btnInstructionLoad);
		btnInstructionLoad.addActionListener(this);

		// add label for adding data
		lblData = new JLabel("Data");
		lblData.setFont(new Font("宋体", Font.BOLD, 20));
		lblData.setBounds(531, 96, 72, 18);
		frmComputerSimulator.getContentPane().add(lblData);

		// add text field for data
		tfData = new JTextField();
		tfData.setFont(new Font("宋体", Font.PLAIN, 20));
		tfData.setBounds(657, 95, 255, 24);
		frmComputerSimulator.getContentPane().add(tfData);
		tfData.setColumns(10);

		// add label for data's address
		lblDataAddress = new JLabel("Address");
		lblDataAddress.setFont(new Font("宋体", Font.BOLD, 20));
		lblDataAddress.setBounds(913, 98, 79, 18);
		frmComputerSimulator.getContentPane().add(lblDataAddress);

		// add button for loading data
		btnDataLoad = new JButton("Load");
		btnDataLoad.setFont(new Font("宋体", Font.BOLD, 20));
		btnDataLoad.setBounds(1082, 94, 86, 27);
		frmComputerSimulator.getContentPane().add(btnDataLoad);
		btnDataLoad.addActionListener(this);

		// add text field for data's address
		tfDataAddress = new JTextField();
		tfDataAddress.setFont(new Font("宋体", Font.PLAIN, 20));
		tfDataAddress.setBounds(993, 95, 86, 24);
		frmComputerSimulator.getContentPane().add(tfDataAddress);
		tfDataAddress.setColumns(10);
		// add button to clear the console
		btnCleanConsole = new JButton("Clear");
		btnCleanConsole.setFont(new Font("宋体", Font.BOLD, 20));
		btnCleanConsole.setBounds(498, 599, 113, 27);
		frmComputerSimulator.getContentPane().add(btnCleanConsole);
		btnCleanConsole.addActionListener(this);
		// add label for loading data to IR
		JLabel lblIR2 = new JLabel("IR");
		lblIR2.setFont(new Font("宋体", Font.BOLD, 20));
		lblIR2.setBounds(279, 113, 26, 18);
		frmComputerSimulator.getContentPane().add(lblIR2);
		// add text field for loading data to IR
		tfLoadIR = new JTextField();
		tfLoadIR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfLoadIR.setBounds(304, 112, 79, 24);
		frmComputerSimulator.getContentPane().add(tfLoadIR);
		tfLoadIR.setColumns(10);
		// add button for loading data to IR
		btnLoadToIR = new JButton("IR Load");
		btnLoadToIR.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadToIR.setBounds(396, 111, 123, 27);
		frmComputerSimulator.getContentPane().add(btnLoadToIR);
		btnLoadToIR.addActionListener(this);
		// add label for loading data to MAR
		JLabel lblMAR2 = new JLabel("MAR");
		lblMAR2.setFont(new Font("宋体", Font.BOLD, 20));
		lblMAR2.setBounds(266, 150, 39, 18);
		frmComputerSimulator.getContentPane().add(lblMAR2);
		// add text field for loading data to MAR
		tfLoadToMAR = new JTextField();
		tfLoadToMAR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfLoadToMAR.setBounds(304, 149, 79, 24);
		frmComputerSimulator.getContentPane().add(tfLoadToMAR);
		tfLoadToMAR.setColumns(10);
		// add button for loading data to MAR
		btnLoadToMAR = new JButton("MAR Load");
		btnLoadToMAR.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadToMAR.setBounds(396, 148, 123, 27);
		frmComputerSimulator.getContentPane().add(btnLoadToMAR);
		btnLoadToMAR.addActionListener(this);
		// add label for loading data to MBR
		JLabel lblMBR2 = new JLabel("MBR");
		lblMBR2.setFont(new Font("宋体", Font.BOLD, 20));
		lblMBR2.setBounds(266, 182, 39, 18);
		frmComputerSimulator.getContentPane().add(lblMBR2);
		// add text field for loading data to MBR
		tfLoadToMBR = new JTextField();
		tfLoadToMBR.setFont(new Font("宋体", Font.PLAIN, 20));
		tfLoadToMBR.setBounds(304, 179, 79, 24);
		frmComputerSimulator.getContentPane().add(tfLoadToMBR);
		tfLoadToMBR.setColumns(10);
		// add button to loading data to MBR
		btnLoadToMBR = new JButton("MBR Load");
		btnLoadToMBR.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadToMBR.setBounds(396, 178, 123, 27);
		frmComputerSimulator.getContentPane().add(btnLoadToMBR);
		btnLoadToMBR.addActionListener(this);
		// add button for loading data to memory
		btnLoadData = new JButton("Load MBR To Memory");
		btnLoadData.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadData.setBounds(531, 178, 242, 27);
		frmComputerSimulator.getContentPane().add(btnLoadData);
		btnLoadData.addActionListener(this);
		// add text field for keyboard input
		tfKeyboard = new JTextField();
		tfKeyboard.setFont(new Font("宋体", Font.PLAIN, 20));
		tfKeyboard.setBounds(375, 228, 144, 24);
		frmComputerSimulator.getContentPane().add(tfKeyboard);
		tfKeyboard.setColumns(10);
		// add label for keybaord input
		JLabel lblkeyboard = new JLabel("KeyBoard");
		lblkeyboard.setFont(new Font("宋体", Font.BOLD, 20));
		lblkeyboard.setBounds(266, 228, 95, 21);
		frmComputerSimulator.getContentPane().add(lblkeyboard);
		// add button for keyboard input
		btnKeyboardInput = new JButton("Enter");
		btnKeyboardInput.setFont(new Font("宋体", Font.BOLD, 20));
		btnKeyboardInput.setBounds(531, 227, 113, 27);
		frmComputerSimulator.getContentPane().add(btnKeyboardInput);
		btnKeyboardInput.addActionListener(this);
		
		//button of loading test1 
		btnLoadTest1 = new JButton("Load Test1");
		btnLoadTest1.setFont(new Font("宋体", Font.BOLD, 20));
		btnLoadTest1.setBounds(584, 282, 189, 27);
		frmComputerSimulator.getContentPane().add(btnLoadTest1);
		btnLoadTest1.addActionListener(this);
		
		//button of executing test1
		btnExecuteTest1 = new JButton("Execute Test1");
		btnExecuteTest1.setFont(new Font("宋体", Font.BOLD, 20));
		btnExecuteTest1.setBounds(584, 327, 190, 27);
		frmComputerSimulator.getContentPane().add(btnExecuteTest1);
		btnExecuteTest1.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRun) {// Respond to clicking the Run button
			if (status == false) {
				txtrConsole.append("The computer hasn't started yet\n");
				return;
			}

			isRunning = true;
			this.run();
		} else if (e.getSource() == btnIPL) { // Respond to clicking the IPL button
			boolean result = loader.LoadBootStrap();
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
			}
		} else if (e.getSource() == btnSingleStep) { // Respond to clicking the Single Step button
			if (status == true && !isRunning) {
				txtrConsole.append("Single step\n");
				int result = cpu.Execute();
				if (result == -1) { // complete boostrap program
					cpu.SetPC(8); // when finish executing boostrap program, Returning to the boot program means
									// that it prompts the user to either run the currently loaded program again or
									// to load a new program and run it.
					txtrConsole.append("Finish executing boot program, CPU is idle\n");
					this.updatePhase("CPU is idel");
				} else if (result == -2) {
					txtrConsole.append("Failed to execute instructions, something is wrong###\n");
				} else if (result == -3) {
					txtrConsole.append("No instructions to execuate, CPU is idel!!!\n");
					this.updatePhase("CPU is idel");
				}
			} else if (isRunning) {
				txtrConsole.append("The computer is in running, cannot execute instruction with single step\n");
			} else {
				txtrConsole.append("The computer hasn't started yet\n");
			}
		} else if (e.getSource() == btnHalt) { // Respond to clicking the Halt button
			if (isRunning || status) {
				StopMachine();
			}
		} else if (e.getSource() == rdbtnIndirect) { // Respond to clicking the Indirect addressing radio button
			if (rdbtnIndirect.isSelected()) {
				this.updateUserConsole("Indirect Addressing\n");
			} else {
				this.updateUserConsole("No indirect Addressing\n");
			}
			this.cpu.SetIndirectAddress(rdbtnIndirect.isSelected());
			this.memory.SetIndirectAddress(rdbtnIndirect.isSelected());
		} else if (e.getSource() == btnLoadToPC) { // Respond to clicking the Load button to load data into PC
			String pc_ori = cpu.GetPC();
			try {
				int pc_update = Integer.parseInt(tfLoadPC.getText());
				txtrConsole.append("update PC from " + pc_ori + " to " + pc_update + "\n");
				cpu.SetPC(pc_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfLoadPC.getText() + "\n");
			}
		} else if (e.getSource() == btnR0Load) { // Respond to clicking the Load button to load data into R0
			Integer r0_ori = cpu.GetGPR(0);
			try {
				int r0_update = Integer.parseInt(tfR0.getText());
				txtrConsole.append("update R0 from " + r0_ori.intValue() + " to " + r0_update + "\n");
				this.cpu.SetGPR(0, r0_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfR0.getText() + "\n");
			}
		} else if (e.getSource() == btnR1Load) { // Respond to clicking the Load button to load data into R1
			Integer r1_ori = cpu.GetGPR(1);
			try {
				int r1_update = Integer.parseInt(tfR1.getText());
				txtrConsole.append("update R1 from " + r1_ori.intValue() + " to " + r1_update + "\n");
				this.cpu.SetGPR(1, r1_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfR1.getText() + "\n");
			}
		} else if (e.getSource() == btnR2Load) { // Respond to clicking the Load button to load data into R1
			Integer r2_ori = cpu.GetGPR(2);
			try {
				int r2_update = Integer.parseInt(tfR2.getText());
				txtrConsole.append("update R2 from " + r2_ori.intValue() + " to " + r2_update + "\n");
				this.cpu.SetGPR(2, r2_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfR2.getText() + "\n");
			}
		} else if (e.getSource() == btnR3Load) { // Respond to clicking the Load button to load data into R3
			Integer r3_ori = cpu.GetGPR(3);
			try {
				int r3_update = Integer.parseInt(tfR3.getText());
				txtrConsole.append("update R2 from " + r3_ori.intValue() + " to " + r3_update + "\n");
				this.cpu.SetGPR(3, r3_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfR3.getText() + "\n");
			}
		} else if (e.getSource() == btnX1Load) { // Respond to clicking the Load button to load data into X1
			Integer x1_ori = cpu.GetIX(1).intValue();
			try {
				int x1_update = Integer.parseInt(tfX1.getText());
				txtrConsole.append("update X1 from " + x1_ori.intValue() + " to " + x1_update + "\n");
				this.cpu.SetIX(1, x1_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfX1.getText() + "\n");
			}
		} else if (e.getSource() == btnX2Load) { // Respond to clicking the Load button to load data into X2
			Integer x2_ori = cpu.GetIX(2).intValue();
			try {
				int x2_update = Integer.parseInt(tfX2.getText());
				txtrConsole.append("update X2 from " + x2_ori.intValue() + " to " + x2_update + "\n");
				this.cpu.SetIX(2, x2_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfX2.getText() + "\n");
			}
		} else if (e.getSource() == btnX3Load) { // Respond to clicking the Load button to load data into X3
			Integer x3_ori = cpu.GetIX(3).intValue();
			try {
				int x3_update = Integer.parseInt(tfX3.getText());
				txtrConsole.append("update X3 from " + x3_ori.intValue() + " to " + x3_update + "\n");
				this.cpu.SetIX(3, x3_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfX3.getText() + "\n");
			}
		} else if (e.getSource() == btnInstructionLoad) { // Respond to clicking the Load button to load instruction
															// into specified memory location
			try {
				int address = Integer.parseInt(tfInstructionAddress.getText());
				cpu.SetMAR(address);
				BitSet instruction = cpu.Encode(tfInstruction.getText());
				if (instruction == null) {
					this.updateMFR(7);
					this.updateUserConsole("Encoding instruction error. Invalid instruction!!!\n");
					return;
				}
				cpu.SetMBR(InstructionCodec.GetValueWithInt(instruction));//
				boolean result = memory.Set(address, instruction);
				if (result == true) {
					txtrConsole.append(
							"Loading instruction " + tfInstruction.getText() + " to Memory " + address + " sucess\n");
				}
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input address " + tfInstructionAddress.getText() + "\n");
			}
		} else if (e.getSource() == btnDataLoad) { // Respond to clicking the Load button to load data into specified
													// memory location
			try {
				int address = Integer.parseInt(tfDataAddress.getText());
				cpu.SetMAR(address);
				try {
					int data = Integer.parseInt(tfData.getText());
					cpu.SetMBR(data);
					boolean result = memory.LoadData(address, data);
					if (result == true) {
						txtrConsole.append("Loading data " + tfData.getText() + " to Memory " + address + " sucess\n");
					}
				} catch (NumberFormatException exception) {
					txtrConsole.append("invalid input data " + tfData.getText() + "\n");
				}
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input address " + tfDataAddress.getText() + "\n");
			}
		} else if (e.getSource() == btnCleanConsole) { // Respond to clicking Clear botton to clean all content in the
														// console
			txtrConsole.setText("");
		} else if (e.getSource() == btnLoadToIR) {
			String IR_ori = cpu.GetIR();
			try {
				int IR_update = Integer.parseInt(tfLoadIR.getText());
				this.cpu.SetIR(IR_update);
				txtrConsole.append("update IR from " + IR_ori + " to " + this.cpu.GetIR() + "\n");
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfLoadIR.getText() + "\n");
			}
		} else if (e.getSource() == btnLoadToMAR) {
			String mar_ori = cpu.GetMAR();
			try {
				int mar_update = Integer.parseInt(tfLoadToMAR.getText());
				txtrConsole.append("update MAR from " + mar_ori + " to " + mar_update + "\n");
				this.cpu.SetMAR(mar_update);
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfLoadToMAR.getText() + "\n");
			}
		} else if (e.getSource() == btnLoadToMBR) {
			String mbr_ori = cpu.GetMBR();
			try {
				int mbr_update = Integer.parseInt(tfLoadToMBR.getText());
				this.cpu.SetMBR(mbr_update);
				txtrConsole.append("update MBR from " + mbr_ori + " to " + this.cpu.GetMBR() + "\n");
			} catch (NumberFormatException exception) {
				txtrConsole.append("invalid input number " + tfLoadToMBR.getText() + "\n");
			}
		} else if (e.getSource() == btnLoadData) {
			int mar = Integer.parseInt(cpu.GetMAR());
			int mbr = cpu.GetMBRWithInt();
			boolean result = memory.LoadData(mar, mbr);
			if (result == true) {
				txtrConsole.append("Loading data " + cpu.GetMBR() + " to Memory " + mar + " sucess\n");
			}
		} else if (e.getSource() == btnKeyboardInput) {
			this.cpu.InputNotify(0, tfKeyboard.getText());
			this.txtrConsole.append(tfKeyboard.getText() + "\n");
		} else if (e.getSource() == btnLoadTest1) {
			boolean result = loader.LoadProgram(18, "Test1.txt", false);
			if (result == true) {
				txtrConsole.append("Success to load the Test1 program to memory !!!\n");
				updatePhase("Test1 Loaded");
			}
		}
	}

	public void run() {
		while (isRunning) {
			// return -1 means sucessfully finish executing boostrap program
			// return 0 means successfully execute one instruction
			// return -2 means failed to executing one instruction
			// return -3 means no instruction to execute
			// return -4 means waiting for input
			int result = cpu.Execute();
			if (result == -1) { // complete boostrap program
				cpu.SetPC(8); // when finish executing boostrap program, Returning to the boot program means
								// that it prompts the user to either run the currently loaded program again or
								// to load a new program and run it.
				txtrConsole.append("Finish executing boot program, CPU is idle\n");
				txtrConsole.append("Finish executing boot program, CPU is idle\n");
				this.updatePhase("CPU is idel");
				isRunning = false;
			} else if (result == -2) {
				txtrConsole.append("Failed to execute instructions, something is wrong!!!\n");
			} else if (result == -3) {
				cpu.Execute(); // to let CPU execute the machine fault instruction since there is invalid
								// memory access error.
				txtrConsole.append("No instructions to execuate, CPU is idel!!!\n");
				this.updatePhase("CPU is idel");
				isRunning = false;
			} else if (result == 0) {
				isRunning = true;
				txtrConsole.append("Successfully execuate instructions\n");
			} else if (result == -4) { // -4 means waiting for input, stop here
				isRunning = false;	
			} else {
				isRunning = true;
				txtrConsole.append("No return code to be defined, abnormal\n");
			}
		}
	}

	/**
	 * Stop the machine
	 */
	void StopMachine() {
		isRunning = false;
		status = false;
		ResetRegisters(); // Reset all registers to 0
		ResetTextFields(); // Reset all displayed text fields to empty
		ResetMemory(); // Reset memory space to null
		this.updatePhase("Powered off");
		txtrConsole.setText("Computer is powered off\n");
	}

	/**
	 * Reset memory space to null
	 */
	void ResetMemory() {
		this.memory.Reset();
	}

	/**
	 * Reset all registers to 0
	 */
	void ResetRegisters() {
		cpu.SetGPR(0, 0);
		cpu.SetGPR(1, 0);
		cpu.SetGPR(2, 0);
		cpu.SetGPR(3, 0);
		cpu.SetIX(1, 0);
		cpu.SetIX(2, 0);
		cpu.SetIX(3, 0);
		cpu.SetMFR(0);
		cpu.SetPC(0);
		cpu.SetMSR(0);
		cpu.SetMBR(0);
		cpu.SetMAR(0);
		cpu.SetIR(0);
	}

	/**
	 * Reset all text fields to empty
	 */
	void ResetTextFields() {
		tfR0.setText("");
		tfR1.setText("");
		tfR2.setText("");
		tfR3.setText("");

		tfX1.setText("");
		tfX2.setText("");
		tfX3.setText("");
		tfLoadPC.setText("");
		tfInstruction.setText("");
		tfData.setText("");
		tfInstructionAddress.setText("");
		tfDataAddress.setText("");
		txtrConsole.setText("");
		tfLoadPC.setText("");
		tfLoadIR.setText("");
		tfLoadToMAR.setText("");
		tfLoadToMBR.setText("");
	}

	@Override
	public void updateData(Object obj) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
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
				} else if (obj instanceof MSR) {
					if (tfMSR != null) {
						tfMSR.setText(cpu.GetMSR());
					}
				} else if (obj instanceof CCR) {
					if (tfCCR != null) {
						tfCCR.setText(cpu.GetCCR());
					}
				}
			}
		});
	}

	@Override
	public void updateUserConsole(String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				txtrConsole.append(message);
			}
		});
	}

	@Override
	public void updateMFR(int value) {
		cpu.SetMFR(value);
	}

	@Override
	public void updatePhase(String message) {
		lblPhase.setText(message);
	}

	@Override
	public void updateMBR(int buffer_content) {
		cpu.SetMBR(buffer_content);
	}

	@Override
	public void updateMAR(int value) {
		cpu.SetMAR(value);
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

	@Override
	public void stop() {
		StopMachine();
	}

	@Override
	public String GetKeyboardInput() {
		String keyboardInput = tfKeyboard.getText();
		return keyboardInput;
	}
}
