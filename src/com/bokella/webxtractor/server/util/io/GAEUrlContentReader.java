package com.bokella.webxtractor.server.util.io;

import com.bokella.webxtractor.server.util.io.gae.GAEConnectionManager;

import java.util.logging.Logger;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

public class GAEUrlContentReader extends DefaultUrlContentReader {
	private static final Logger log = Logger.getLogger(GAEUrlContentReader.class.getName());

	public GAEUrlContentReader () {
		super();
		
		this.httpClient = new DefaultHttpClient(new GAEConnectionManager(), new BasicHttpParams());
	}
}
