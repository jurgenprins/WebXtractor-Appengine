package com.bokella.webxtractor.server.services.exceptions;

import java.lang.Exception;

public class ExtractorServiceException extends Exception {
	public ExtractorServiceException(String message) {
		super(message);
	}
}