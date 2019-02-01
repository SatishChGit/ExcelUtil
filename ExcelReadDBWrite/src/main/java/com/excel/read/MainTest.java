package com.excel.read;

public class MainTest {
	public static void main(String[] args) {
		MSXLSUtil util = new MSXLSUtil();
		util.deleteexistsDatafromDB();
		util.storeEmployeeInfo();
		util.showDataFromDB();
	}

}
