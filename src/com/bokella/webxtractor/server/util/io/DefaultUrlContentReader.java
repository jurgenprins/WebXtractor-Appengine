package com.bokella.webxtractor.server.util.io;

import com.bokella.webxtractor.server.util.io.exceptions.UrlReaderException;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class DefaultUrlContentReader implements UrlContentReader {
	private static final Logger log = Logger.getLogger(DefaultUrlContentReader.class.getName());

	HttpClient httpClient;
	ResponseHandler<String> responseStringHandler;
	ResponseHandler<byte[]> responseBinaryHandler;
	
	public DefaultUrlContentReader () {
		this.httpClient = new DefaultHttpClient();
		
		this.responseStringHandler = new ResponseHandler<String>() {
		    public String handleResponse(
		            HttpResponse response) throws ClientProtocolException, IOException {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            return EntityUtils.toString(entity, EntityUtils.getContentCharSet(entity));
		        } else {
		            return null;
		        }
		    }
		};
		
		this.responseBinaryHandler = new ResponseHandler<byte[]>() {
		    public byte[] handleResponse(
		            HttpResponse response) throws ClientProtocolException, IOException {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            return EntityUtils.toByteArray(entity);
		        } else {
		            return null;
		        }
		    }
		};
	}
	
	public String readString(URL url) throws UrlReaderException {
		try {
			HttpUriRequest request = new HttpGet(url.toURI());
			
			log.info("fetching.. " + url.toURI());
			return this.httpClient.execute(request, this.responseStringHandler);
		} catch (Exception e) {
			log.severe("Could not fetch " + url.toString() + ": " + e.getMessage());
			e.printStackTrace();
			throw new UrlReaderException("Could not fetch " + url.toString() + ": " + e.getMessage());
		}
	}

	public byte[] readBinary(URL url) throws UrlReaderException {
		try {		
			HttpUriRequest request = new HttpGet(url.toURI());
			
			return this.httpClient.execute(request, this.responseBinaryHandler);
		} catch (Exception e) {
			log.severe("Could not fetch " + url.toString() + ": " + e.getMessage());
			throw new UrlReaderException("Could not fetch " + url.toString() + ": " + e.getMessage());
		}
	}
}
