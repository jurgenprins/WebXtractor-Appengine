package com.bokella.webxtractor.server.util.io;

import com.bokella.webxtractor.server.util.io.exceptions.UrlReaderException;

import java.net.URL;

public interface UrlContentReader {
	public String readString(URL url) throws UrlReaderException;
	public byte[] readBinary(URL url) throws UrlReaderException;
}
