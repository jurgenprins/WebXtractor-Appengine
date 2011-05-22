package com.bokella.webxtractor.server.services;

import com.bokella.webxtractor.server.tasks.XtrTask;

public interface TaskService {
	public void addTask(XtrTask task, String queueName);
	public void addTask(XtrTask task);
	public void process(String queueName);
}
