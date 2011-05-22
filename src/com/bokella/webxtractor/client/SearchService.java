package com.bokella.webxtractor.client;

import java.util.List;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {
	public List<XtrImage>  find(String baseUrl, String qry, Boolean fresh, Boolean resolve);
}
