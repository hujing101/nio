package com.sanpower.cloud.socket.hjt212.model;

import java.util.Map;

public class DataSegment {
	private String QN;
	private String PNUM;
	private String PNO;
	private String ST;
	private String CN;//Command
	private String PW;
	private String MN;
	private String Flag;
	private Map<String,Object> CP;//Data Area
	public String getQN() {
		return QN;
	}
	public void setQN(String qN) {
		QN = qN;
	}
	public String getPNUM() {
		return PNUM;
	}
	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}
	public String getPNO() {
		return PNO;
	}
	public void setPNO(String pNO) {
		PNO = pNO;
	}
	public String getST() {
		return ST;
	}
	public void setST(String sT) {
		ST = sT;
	}
	public String getCN() {
		return CN;
	}
	public void setCN(String cN) {
		CN = cN;
	}
	public String getPW() {
		return PW;
	}
	public void setPW(String pW) {
		PW = pW;
	}
	public String getMN() {
		return MN;
	}
	public void setMN(String mN) {
		MN = mN;
	}
	public String getFlag() {
		return Flag;
	}
	public void setFlag(String flag) {
		Flag = flag;
	}
	public Map<String, Object> getCP() {
		return CP;
	}
	public void setCP(Map<String, Object> cP) {
		CP = cP;
	}
	
}
