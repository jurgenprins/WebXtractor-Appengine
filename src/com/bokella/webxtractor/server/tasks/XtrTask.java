package com.bokella.webxtractor.server.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

public class XtrTask implements XtrTaskExecutable {
	private static final Logger log = Logger.getLogger(XtrTask.class.getName());
	
	private String name = null;
	private byte[] payload = null;
	Map<String, Object> attributes = new HashMap<String, Object>();
	
	public XtrTask(
			String name) {
		this.name = name;
	}
	
	public XtrTask(
			String name,
			byte[] payload) {
		this.name = name;
		this.payload = payload;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setPayloadAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}
	
	public Object getPayloadAttribute(String name) {
		Object res = this.attributes.get(name);
		
		if (res == null) {
			if (this.payload != null) {
				InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(this.payload));
				
				try {
					ObjectInput serialStream = new ObjectInputStream(stream);
					this.attributes = (Map<String, Object>)serialStream.readObject();
					res = this.attributes.get(name);
				} catch (Exception e) {
					log.info("Could not deserialize attributes from key: " + e.getMessage());
				}
			}
		}
		
		return res;
	}
	
	public byte[] getPayload() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		try {
			ObjectOutput serialStream = new ObjectOutputStream(stream);
			serialStream.writeObject(this.attributes);
			return Base64.encodeBase64(stream.toByteArray());
		} catch (Exception e) {
			log.info("Could not serialize attributes to key: " + e.getMessage());
		}
		
		return null;
	}
	
	
	
	public void execute() { }
}
