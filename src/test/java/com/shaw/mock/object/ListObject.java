package com.shaw.mock.object;

import java.io.Serializable;
import java.math.BigDecimal;

public class ListObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3423680111781866223L;
	String ls1;
	BigDecimal ld1;

	public ListObject() {
		super();
		// TODO Auto-generated constructor stub
	}


	
	public String getLs1() {
		return ls1;
	}



	public void setLs1(String ls1) {
		this.ls1 = ls1;
	}



	public BigDecimal getLd1() {
		return ld1;
	}



	public void setLd1(BigDecimal ld1) {
		this.ld1 = ld1;
	}



	@Override
	public String toString() {
		return "ParentObject [ls1=" + ls1 + ", ld1=" + ld1 + "]";
	}


}
