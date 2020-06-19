package com.shaw.mock.object;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NestedObject extends ParentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7771501955826267618L;

	public static class InteriorObject {
		String istr1;
		Double d1;
		LocalDateTime ldt;

		public InteriorObject() {
			super();
			// TODO Auto-generated constructor stub
		}

		public InteriorObject(String istr1, Double d1, LocalDateTime ldt) {
			super();
			this.istr1 = istr1;
			this.d1 = d1;
			this.ldt = ldt;
		}

		public String getIstr1() {
			return istr1;
		}

		public void setIstr1(String istr1) {
			this.istr1 = istr1;
		}

		public Double getD1() {
			return d1;
		}

		public void setD1(Double d1) {
			this.d1 = d1;
		}

		public LocalDateTime getLdt() {
			return ldt;
		}

		public void setLdt(LocalDateTime ldt) {
			this.ldt = ldt;
		}

		@Override
		public String toString() {
			return "InteriorObject [istr1=" + istr1 + ", d1=" + d1 + ", ldt=" + ldt + "]";
		}
		
	}

	String str1;
	String str2;
	LocalDate ldate;
	Integer i;
	
	InteriorObject io;

	public NestedObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
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

	public InteriorObject getIo() {
		return io;
	}

	public void setIo(InteriorObject io) {
		this.io = io;
	}

	@Override
	public String toString() {
		return "NestedObject [parentObject = " + super.toString() + " str1=" + str1 + ", str2=" + str2 + ", ldate=" + ldate + ", i=" + i + ", io=" + io + "]";
	}
	
	
}
