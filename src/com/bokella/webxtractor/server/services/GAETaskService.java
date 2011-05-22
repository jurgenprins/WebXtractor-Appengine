package com.bokella.webxtractor.server.services;

import java.util.logging.Logger;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

import com.bokella.webxtractor.server.tasks.XtrTask;

public class GAETaskService extends DefaultTaskService implements TaskService {
	private static final Logger log = Logger.getLogger(GAETaskService.class.getName());
	
	public void process(String queueName) {
		XtrTask xtrTask;
		Queue gaeQueue = QueueFactory.getDefaultQueue();
		
		log.info("Processing queue " + queueName);
		
		java.util.Queue<XtrTask> taskQueue = queues.get(queueName);
		if (taskQueue == null) {
			log.warning("Queue " + queueName + " does not exist");
			return;
		}
		
		TaskHandle taskHandle = null;
		
		while ((xtrTask = taskQueue.poll()) != null) {
			taskHandle = gaeQueue.add(url("/webxtractor/task/".concat(queueName)).param("payload", xtrTask.getPayload()).method(TaskOptions.Method.GET));
			log.info("GA queue " + taskHandle.getQueueName() + " accepted task " + xtrTask.getName() + " as " + taskHandle.getName() + " estimated to run at " + taskHandle.getEtaMillis());
		}
	}
}
