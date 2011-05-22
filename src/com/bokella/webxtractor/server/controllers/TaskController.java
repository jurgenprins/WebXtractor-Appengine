package com.bokella.webxtractor.server.controllers;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bokella.webxtractor.server.services.web.WebPageService;
import com.bokella.webxtractor.server.services.xtr.XtrGalleryService;
import com.bokella.webxtractor.server.services.ExtractorService;
import com.bokella.webxtractor.server.services.TaskService;
import com.bokella.webxtractor.server.tasks.ExtractorTask;
import com.bokella.webxtractor.server.tasks.XtrImageResolveTask;

@Controller
@RequestMapping("/task/**")
public class TaskController {
	private static final Logger log = Logger.getLogger(TaskController.class.getName());
	ExtractorService extractorService = null;
	WebPageService webPageService = null;
	XtrGalleryService xtrGalleryService = null;
	TaskService taskService = null;

	public TaskController (
			ExtractorService extractorService,
			WebPageService webPageService,
			XtrGalleryService xtrGalleryService,
			TaskService taskService) {
		this.extractorService = extractorService;
		this.webPageService = webPageService;
		this.xtrGalleryService = xtrGalleryService;
		this.taskService = taskService;
	}
	/*
	@RequestMapping("/image/")
    protected void show(@RequestParam("id") String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
		Thumbnail thumb;
		try {
			 thumb = this.thumbnailService.getByUrl(id);
		} catch (Exception e) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			httpServletResponse.flushBuffer();
			return;
		}
		
		httpServletResponse.setContentType("image/jpeg");
        
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(thumb.getData().getBytes());
        responseOutputStream.flush();
        responseOutputStream.close();
	}
*/
	@RequestMapping("/task/com.bokella.webxtractor.server.tasks.ExtractorTask")
	public void executeExtractorTask(@RequestParam("payload") String payload, HttpServletResponse resp) {
		resp.setStatus(HttpServletResponse.SC_OK);
		
		try {
			ExtractorTask extractorTask = new ExtractorTask(
					this.extractorService,
					payload.getBytes());
			
			log.info("Worker for task " + extractorTask.getName());
			
			extractorTask.execute();
		} catch (Exception e) { 
			log.severe("Failed to create/execute task " + payload + " : " + e.getMessage());
			try {
				resp.getWriter().write("Failed to create/execute task " + payload + " : " + e.getMessage());
			} catch (Exception e2) {}
		}
	}
	
	@RequestMapping("/task/com.bokella.webxtractor.server.tasks.XtrImageResolveTask")
	public void executeXtrImageResolveTask(@RequestParam("payload") String payload, HttpServletResponse resp) {
		resp.setStatus(HttpServletResponse.SC_OK);
		
		try {
			XtrImageResolveTask xtrImageResolveTask = new XtrImageResolveTask(
					this.webPageService,
					this.xtrGalleryService,
					this.taskService,
					payload.getBytes());
			
			log.info("Worker for task " + xtrImageResolveTask.getName());
			
			xtrImageResolveTask.execute();
		} catch (Exception e) { 
			log.severe("Failed to create/execute task " + payload + " : " + e.getMessage());
			try {
				resp.getWriter().write("Failed to create/execute task " + payload + " : " + e.getMessage());
			} catch (Exception e2) {}
		}
	}
}
