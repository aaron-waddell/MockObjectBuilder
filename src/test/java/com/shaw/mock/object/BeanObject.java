package com.shaw.mock.object;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BeanObject {

	String bstr1;
	
//	private BeanObject() {
//		super();
//	}

	public String getBstr1() {
		return bstr1;
	}

	public void setBstr1(String bstr1) {
		this.bstr1 = bstr1;
	}
	
	public static BeanObject getInstance() {
		return new BeanObject();
	}
	
}
