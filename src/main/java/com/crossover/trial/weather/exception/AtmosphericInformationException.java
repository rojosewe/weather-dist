package com.crossover.trial.weather.exception;

public class AtmosphericInformationException extends Exception{

	public AtmosphericInformationException() {
		super();
	}
	
	public AtmosphericInformationException(String message) {
		super(message);
	}
	
	public AtmosphericInformationException(String message, Throwable t) {
		super(message, t);
	}

}
