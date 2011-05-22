package com.bokella.webxtractor.server.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import com.bokella.webxtractor.server.tasks.XtrTask;

public class DefaultTaskService implements TaskService {
	private static final Logger log = Logger.getLogger(DefaultTaskService.class.getName());
	
	Map<String, java.util.Queue<XtrTask>> queues = new HashMap<String, java.util.Queue<XtrTask>>();

	public void addTask(XtrTask task) {
		this.addTask(task, task.getClass().getName());
	}
		
	public void addTask(XtrTask task, String queueName) {
		log.info("Adding task " + task.getName() + " to queue " + queueName);
				
		java.util.Queue<XtrTask> taskQueue = queues.get(queueName);
		if (taskQueue == null) {
			queues.put(queueName, new LinkedList<XtrTask>());
			taskQueue = queues.get(queueName);
		}
				
		taskQueue.add(task);
	}

	
	public void process(String queueName) {
		XtrTask xtrTask;
		
		java.util.Queue<XtrTask> taskQueue = queues.get(queueName);
		if (taskQueue == null) {
			log.warning("Queue " + queueName + " does not exist");
			return;
		}
		
		while ((xtrTask = taskQueue.poll()) != null) {
			xtrTask.execute();
		}
	}
}
