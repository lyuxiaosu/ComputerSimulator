package com.test;

import java.util.BitSet;

/* An instruction will be transfered and processed in a chain path. All chain nodes will implement IProcessor interface and hold
 * one member variable of next IProcessor 
 */
public class AbstrctProcessor implements IProcessor {

	protected IProcessor next;
	protected IUpdate subject;
	protected BitSet bitset;
	protected int value;
	protected int nbits;

	@Override
	public int Process(Object data) {
		// TODO Auto-generated method stub
		Object result = this.doProcess(data);
		if (result != null && result instanceof Integer) {
			int i = (Integer) result;

			// The result should be the instruction data that the processor returns, but
			// some particular negative value will be returned if
			// error happens or no instruction to execute
			if (i == -1 || i == -2 || i == -3) {
				return i;
			}
		} else if (result == null) {
			// NO next node, return
			return 0;
		}

		if (this.next != null) {
			// If next node is not null, transfer this instruction to the next node and let
			// it to process it
			return this.next.Process(result);
		}
		return 0;
	}

	@Override
	public IProcessor GetNext() {
		// TODO Auto-generated method stub
		return next;
	}

	public IProcessor AddNext(IProcessor processor) {
		this.next = processor;

		return this.next;
	}

	protected Object doProcess(Object data) {
		return null;
	}

	/**
	 * define a empty postSetValue fucntion
	 */
	protected void postSetValue(int old_value, int new_value) {
	}

	public void Set(BitSet content) {
		bitset.clear();
		bitset.or(content);
		this.subject.updateData(this);
	}

	public BitSet Get() {
		return bitset;
	}

	// Sets the bit at the specified index to true.
	public void Set(int index) {
		bitset.clear();
		bitset.set(index);
		this.subject.updateData(this);
	}

	public boolean Get(int index) {
		return bitset.get(index);
	}

	// set a long value to bitset
	public void SetValue(long value) {
		int old_value = this.value;
		int int_value = java.lang.Math.abs((int) value);

		bitset.clear();
		int index = 0;
		while (int_value != 0L) {
			if (int_value % 2L != 0) {
				this.bitset.set(index);
			}
			++index;
			int_value = int_value >>> 1;
		}

		if (value < 0) { // is negative number, set the highest bit to 1
			this.bitset.set(nbits-1);
		}
		
		this.value = (int)value;
		this.subject.updateData(this);
		this.postSetValue(old_value, this.value);
	}

	public int GetValueWithInt() {
		int bitInteger = 0;
		for (int i = 0; i < nbits -1; i++)
			if (this.bitset.get(i))
				bitInteger |= (1 << i);
		if (this.bitset.get(nbits-1) == false) {
			value = bitInteger;
		} else {
			value = -bitInteger;
		}
		return value;
	}

	public String GetBinaryString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nbits; i++) {
			s.append(bitset.get(i) == true ? 1 : 0);
		}

		return s.toString();
	}
}
