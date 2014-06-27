package com.sanpower.cloud.socket.hjt212.model;

public class DataPackage {
	private String packageHeader;
	private int dataLength;
	private DataSegment data;
	private String CRC;
	public String getPackageHeader() {
		return packageHeader;
	}
	public void setPackageHeader(String packageHeader) {
		this.packageHeader = packageHeader;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public DataSegment getData() {
		return data;
	}
	public void setData(DataSegment data) {
		this.data = data;
	}
	
	public String getCRC() {
		return CRC;
	}
	public void setCRC(String cRC) {
		CRC = cRC;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataPackage [packageHeader=");
		builder.append(packageHeader);
		builder.append(", dataLength=");
		builder.append(dataLength);
		builder.append(", data=");
		builder.append(data);
		builder.append(", CRC=");
		builder.append(CRC);
		builder.append("]");
		return builder.toString();
	}
	
}
