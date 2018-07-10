package com.test;

import java.util.BitSet;

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
		} 

		if (this.next != null) {
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
		int int_value = (int)value;
		bitset.clear();
		int index = 0;
		while (value != 0L) {
			if (value % 2L != 0) {
				this.bitset.set(index);
			}
			++index;
			value = value >>> 1;
		}
		this.value = int_value;
		this.subject.updateData(this);
	}

	public int GetValueWithInt() {
		int bitInteger = 0;
		for (int i = 0; i < nbits; i++)
			if (this.bitset.get(i))
				bitInteger |= (1 << i);
		value = bitInteger;
		return bitInteger;
	}

	public String GetBinaryString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nbits; i++) {
			s.append(bitset.get(i) == true ? 1 : 0);
		}

		return s.toString();
	}
}
