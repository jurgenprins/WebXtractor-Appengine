package com.bokella.webxtractor.client;

import java.util.List;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Webxtractor implements EntryPoint {
	private static final String SERVER_ERROR = "An error occurred";

	final FlowPanel resultsPanel = new FlowPanel();

	final Label statusLabel = new Label();

	private final SearchServiceAsync searchService = 
		GWT.create(SearchService.class);
	
	public void onModuleLoad() {
		final VerticalPanel mainPanel = new VerticalPanel();

		final HorizontalPanel queryPanel = new HorizontalPanel();
		final Button querySubmitButton = new Button("Find");
		final TextBox urlField = new TextBox();
		final TextBox queryField = new TextBox();
		final CheckBox queryFresh = new CheckBox("clear gallery first");
		final CheckBox queryResolve = new CheckBox("resolve originals");
		final CheckBox queryUpdate = new CheckBox("auto update");
		final TextBox thumbSize = new TextBox();
		thumbSize.setWidth("50px");
		thumbSize.setMaxLength(3);
		
		querySubmitButton.addStyleName("sendButton");

		queryPanel.add(urlField);
		queryPanel.add(queryField);
		queryPanel.add(queryFresh);
		queryPanel.add(queryResolve);
		queryPanel.add(querySubmitButton);
		queryPanel.add(queryUpdate);
		queryPanel.add(thumbSize);
		queryPanel.add(statusLabel);
		
		mainPanel.add(queryPanel);
		mainPanel.add(resultsPanel);
		
		RootPanel.get().add(mainPanel);
		
		queryField.setFocus(true);		
		
		// Create a handler for the sendButton and nameField
		class SearchHandler implements ClickHandler, KeyUpHandler {
			private static final int REFRESH_INTERVAL = 15000; // ms
			
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			private void sendNameToServer() {
				searchGallery(urlField.getText(), queryField.getText(), queryFresh.getValue(), queryResolve.getValue());
				
				Timer refreshTimer = new Timer() {
			      @Override
			      public void run() {
			    	if (queryUpdate.getValue() == true) {
			    		searchGallery(urlField.getText(), queryField.getText(), false, false);
			    	}
			      }
			    };
			    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
			}
				
			public void searchGallery(String url, String qry, Boolean fresh, Boolean resolve) {
				querySubmitButton.setEnabled(false);
				statusLabel.setText("Contacting server..");
				
				searchService.find(url, qry, fresh, resolve,
						new AsyncCallback<List<XtrImage>>() {
							public void onFailure(Throwable caught) {
								try {
									statusLabel.setText(SERVER_ERROR + " (" + caught.toString() + " caused by " + caught.getCause().getMessage() + ")");
								} catch (Exception e) {
									try {
										statusLabel.setText(SERVER_ERROR + " (" + caught.toString() + ")");
									} catch (Exception e2) {
										statusLabel.setText(SERVER_ERROR + " (" + e.getMessage() + ")");
									}
								}
								querySubmitButton.setEnabled(true);
							}

							public void onSuccess(List<XtrImage> images) {
								statusLabel.setText(images.size() + " found!");
								querySubmitButton.setEnabled(true);
								resultsPanel.clear();
								
								for(final XtrImage image : images) {
									int imageWidth = image.getThumbWidth();
									int imageHeight = image.getThumbHeight();
									int imageMaxSize = 150;
									try {
										imageMaxSize = new Integer(thumbSize.getValue()).intValue();
									} catch (Exception e) { }
									
									if (imageWidth > imageMaxSize) {
										imageWidth = imageMaxSize;
									}
									if (imageHeight > imageMaxSize) {
										imageHeight = imageMaxSize;
									}
									
									final VerticalPanel imagePanel = new VerticalPanel();
									imagePanel.addStyleName("imagePanel");
									imagePanel.setSize(imageWidth + "px", (imageHeight + 30) + "px");
									
									final Image imageWidget = new Image(image.getThumbUrl());
									imageWidget.setSize(imageWidth + "px", (imageHeight + 30) + "px");
									
									imageWidget.addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											if ((image.getWidth() > 0) ||
												(image.getHeight() > 0) ||
												image.getUrl().endsWith(".jpg") ||
												image.getUrl().endsWith(".gif") ||
												image.getUrl().endsWith(".png")) {
												final PopupPanel p = new PopupPanel(true);
												p.setGlassEnabled(true);
												if (image.getWidth() > 0) {
													p.setWidth(String.valueOf(image.getWidth()));
												}
												if (image.getHeight() > 0) {
													p.setHeight(String.valueOf(image.getHeight()));
												}
												p.setWidget(new Image(image.getUrl()));
												p.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
													public void setPosition(int offsetWidth, int offsetHeight) {
														int left = imageWidget.getAbsoluteLeft() + 10;
														if (left > (Window.getClientWidth() - offsetWidth)) {
															left = Window.getClientWidth() - offsetWidth;
														}
												
														int top = imageWidget.getAbsoluteTop() + 10;
											            if (top > (Window.getClientHeight() - offsetHeight)) {
											            	top = Window.getClientHeight() - offsetHeight;
											            }
											            p.setPopupPosition(left, top);
													}
												});
	
												p.show();
											} else {
												//Window.open(image.getUrl(), image.getKey(), "");
											}
										}
									});
									imagePanel.add(imageWidget);

									final Label imageLabel = new Label();
									imageLabel.setText("score " + image.getThumbMatchScore().toString());
									imageLabel.setWidth((image.getThumbWidth()) + "px");
									imagePanel.add(imageLabel);
									
									resultsPanel.add(imagePanel);
								}
								
							}
						});
			}
		}

		SearchHandler searchHandler = new SearchHandler();
		querySubmitButton.addClickHandler(searchHandler);
		queryField.addKeyUpHandler(searchHandler);
		
		class ThumbSizeHandler implements KeyUpHandler {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					int imageMaxSize = 150;
					try {
						imageMaxSize = new Integer(thumbSize.getValue()).intValue();
					} catch (Exception e) { }
						
					VerticalPanel imagePanel;
					Image imageWidget;
					
					for (int i = 0; i < resultsPanel.getWidgetCount(); i++) {
						imagePanel = (VerticalPanel)resultsPanel.getWidget(i);
						imagePanel.setSize(imageMaxSize + "px", (imageMaxSize + 30) + "px");
						imageWidget = (Image)imagePanel.getWidget(0);
						imageWidget.setSize(imageMaxSize + "px", (imageMaxSize + 30) + "px");
					}
				}
			}
		}
		
		ThumbSizeHandler thumbsizeHandler = new ThumbSizeHandler();
		
		thumbSize.addKeyUpHandler(thumbsizeHandler);
	}

}
