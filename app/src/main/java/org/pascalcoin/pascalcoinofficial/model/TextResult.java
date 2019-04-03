package org.pascalcoin.pascalcoinofficial.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextResult {
	@JsonProperty("name")
    String name;
	@JsonProperty("value")
    String value;
	
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("value")
	public String getValue() {
		return value;
	}
	
	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}
}
