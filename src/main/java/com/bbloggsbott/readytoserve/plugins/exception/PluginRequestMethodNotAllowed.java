package com.bbloggsbott.readytoserve.plugins.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginRequestMethodNotAllowed extends Exception{

	private String method;
	private String endpoint;

	public PluginRequestMethodNotAllowed(String method, String endpoint) {
		super(method + " method not allowed for " + endpoint);
		this.method = method;
		this.endpoint = endpoint;
	}
}
