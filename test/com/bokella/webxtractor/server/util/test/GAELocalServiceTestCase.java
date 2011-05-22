package com.bokella.webxtractor.server.util.test;

import java.io.File;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

import junit.framework.TestCase;

public class GAELocalServiceTestCase extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ApiProxy.setEnvironmentForCurrentThread(new GAETestEnvironment());
        
        ApiProxyLocalImpl proxy = new ApiProxyLocalImpl(new File(".")){};
        proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());
        ApiProxy.setDelegate(proxy);
    }

    @Override
    public void tearDown() throws Exception {
    	ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
    	
        LocalDatastoreService datastoreService = 
            (LocalDatastoreService) proxy.getService(LocalDatastoreService.PACKAGE);
        datastoreService.clearProfiles();
        
        LocalTaskQueue ltq = 
        	(LocalTaskQueue) proxy.getService(LocalTaskQueue.PACKAGE);
        for (String queueName : ltq.getQueueStateInfo().keySet()) {
            ltq.flushQueue(queueName);
        }

        // not strictly necessary to null these out but there's no harm either
		ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
        super.tearDown();
    }
}
