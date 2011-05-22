package com.bokella.webxtractor.server.util.io;

import com.bokella.webxtractor.server.util.io.exceptions.FileReaderException;

public interface FileContentReader {
	public String readFrom(String filename) throws FileReaderException;
}
