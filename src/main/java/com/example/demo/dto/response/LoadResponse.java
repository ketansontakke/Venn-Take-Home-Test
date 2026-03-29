package com.example.demo.dto.response;

public class LoadResponse {

	private String id;
	private String customer_id;
	private boolean accepted;

	private LoadResponse() {
		// Empty constructor needed for jackson mapping in FileProcessorTest
	}

	public LoadResponse(String id, String customerId, boolean accepted) {
		this.id = id;
		this.customer_id = customerId;
		this.accepted = accepted;
	}

	public String getId() {
		return id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public boolean isAccepted() {
		return accepted;
	}

}