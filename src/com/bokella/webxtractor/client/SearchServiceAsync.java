package com.bokella.webxtractor.client;

import java.util.List;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface SearchServiceAsync {
	void find(String baseUrl, String query, Boolean fresh, Boolean resolve, AsyncCallback<List<XtrImage>> callback);
}
