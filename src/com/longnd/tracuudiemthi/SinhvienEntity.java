package com.longnd.tracuudiemthi;

public class SinhvienEntity {
	private String sbd, hoten, tongdiem;
	private String[] diems;

	public SinhvienEntity() {
		super();
	}

	public SinhvienEntity(String hoten, String tongdiem, String[] diems) {
		super();
		this.hoten = hoten;
		this.tongdiem = tongdiem;
		this.diems = diems;
	}

	public String getHoten() {
		return hoten;
	}

	public void setHoten(String hoten) {
		this.hoten = hoten;
	}

	public String getTongdiem() {
		return tongdiem;
	}

	public void setTongdiem(String tongdiem) {
		this.tongdiem = tongdiem;
	}

	public String[] getDiems() {
		return diems;
	}

	public void setDiems(String[] diems) {
		this.diems = diems;
	}

	public String getSbd() {
		return sbd;
	}

	public void setSbd(String sbd) {
		this.sbd = sbd;
	}

}
