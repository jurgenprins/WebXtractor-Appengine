package com.bokella.webxtractor.server;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SpringDispatchedRemoteServiceServlet extends RemoteServiceServlet implements ServletContextAware {
	private ServletContext servletContext;
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
