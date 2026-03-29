package com.example.demo.dto.request;

public class LoadRequest {

    private String id;
    private String customer_id;
    private String load_amount;
    private String time;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getLoad_amount() {
		return load_amount;
	}
	public void setLoad_amount(String load_amount) {
		this.load_amount = load_amount;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}