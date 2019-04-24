package net.kaicong.ipcam.bean;

import java.io.Serializable;

public class PositionModel implements Serializable{
	
	
	public String LandmarkName;// 地标名称
	public String LandmarkDesc;// 地标介绍
	public String LandmarkPic;// 地标图片
	public int IsVerify;// 是否申请通过
	public String ApplyTime;// 地标申请时间
	public String YesOrNoTime;// 地标申请通过或者拒绝时间
	public String Reason;// 拒绝原因

}
