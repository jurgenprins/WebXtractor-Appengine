package com.bokella.webxtractor.server.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.google.gwt.user.client.rpc.RemoteService;

public class GWTController extends RemoteServiceServlet implements Controller,
ServletContextAware
{
  // Instance fields
  private RemoteService remoteService;
  private Class remoteServiceClass;

  private ServletContext servletContext;

  // Public methods
  /**
  * Implements Spring Controller interface method.
  *
  * Call GWT's RemoteService doPost() method and return null.
  *
  * @param request  current HTTP request
  * @param response current HTTP response
  * @return a ModelAndView to render, or null if handled directly
  * @throws Exception in case of errors
  */
  public ModelAndView handleRequest(HttpServletRequest request,
                                 HttpServletResponse response)
                                 throws Exception
  {
    doPost(request, response);
    return null; // response handled by GWT RPC over XmlHttpRequest
  }
  
  /**
  * Process the RPC request encoded into the payload string and return a string
  * that encodes either the method return or an exception thrown by it.
  */
  public String processCall(String payload) throws SerializationException
  {
    try
    {
       RPCRequest rpcRequest =
           RPC.decodeRequest(payload, this.remoteServiceClass);

       // delegate work to the spring injected service
       return RPC.invokeAndEncodeResponse(this.remoteService,
                                          rpcRequest.getMethod(),
                                          rpcRequest.getParameters() );
    }
    catch (IncompatibleRemoteServiceException e)
    {
      return RPC.encodeResponseForFailure(null, e);
    }
  }

  /**
  * Setter for Spring injection of the GWT RemoteService object.
  * @param RemoteService the GWT RemoteService implementation
  * that will be delegated to by
  * the {@code GWTController}.
  */
  public void setRemoteService( RemoteService remoteService )
  {
    this.remoteService = remoteService;
    this.remoteServiceClass = this.remoteService.getClass();
  }
  
  public ServletContext getServletContext() {
      return servletContext;
  }

	public void setServletContext(ServletContext servletContext) {
	      this.servletContext = servletContext;
	}

}