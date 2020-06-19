package com.shaw.mock.object;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class ParentObject implements Serializable{

	String ps1;
	BigDecimal bd1;

	public ParentObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPs1() {
		return ps1;
	}

	public void setPs1(String ps1) {
		this.ps1 = ps1;
	}

	public BigDecimal getBd1() {
		return bd1;
	}

	public void setBd1(BigDecimal bd1) {
		this.bd1 = bd1;
	}

	@Override
	public String toString() {
		return "ParentObject [ps1=" + ps1 + ", bd1=" + bd1 + "]";
	}


}
