package com.test;

public interface IProcessor {
	
	int Process(Object data);
	
	IProcessor GetNext();
	
	IProcessor AddNext(IProcessor processor);
	
}
