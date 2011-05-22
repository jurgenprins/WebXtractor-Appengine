package com.bokella.webxtractor.server.util.io;

import com.bokella.webxtractor.server.util.io.exceptions.FileReaderException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultFileContentReader implements FileContentReader {

	public String readFrom(String filename) throws FileReaderException {
		InputStream is = null;
	    BufferedInputStream bis = null;
	    BufferedReader br = null;
	    
	    String line = null;
		StringBuffer data = new StringBuffer();
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(filename);
			bis = new BufferedInputStream(is);
			br = new BufferedReader(new InputStreamReader(bis));

			while ((line = br.readLine()) != null){
				data.append(line);
				data.append(System.getProperty("line.separator"));
	        }
			
			return data.toString();
		} catch (Exception e) {
			throw new FileReaderException(e.getMessage());
	    } 
	}

}
