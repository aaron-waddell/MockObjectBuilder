package com.shaw.mock.object;

import java.time.LocalDate;

import javax.persistence.Column;

public class DatabaseObject extends ParentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7771501955826267618L;

	@Column(name="db_str_1")
	String dbstr1;
	
	@Column(name="db_str_2")
	String dbstr2;
	
	@Column(name="loc_dt")
	LocalDate ldate;
	
	@Column(name="i_val")
	Integer i;
	
	public DatabaseObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDbstr1() {
		return dbstr1;
	}

	public void setDbstr1(String dbstr1) {
		this.dbstr1 = dbstr1;
	}

	public String getDbstr2() {
		return dbstr2;
	}

	public void setDbstr2(String dbstr2) {
		this.dbstr2 = dbstr2;
	}

	public LocalDate getLdate() {
		return ldate;
	}

	public void setLdate(LocalDate ldate) {
		this.ldate = ldate;
	}

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	@Override
	public String toString() {
		return "DatabaseObject [dbstr1=" + dbstr1 + ", dbstr2=" + dbstr2 + ", ldate=" + ldate + ", i=" + i + "]";
	}

	
}
