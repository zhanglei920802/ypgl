package xzd.mobile.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class MedicineOrderDetail extends MedicineOrder {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1547398244489060679L;
	private String Order_Result;
	private String Order_Location;
	private String Result_Date;

	public int CityAnswer; // 地区经理审核意见,0为 未审核，1为审核同意，2为拒绝

	public String CityAnswerTime; // 地区经理审核时间

	public String CityLocation; // 地区经理审核地点

	public int ProvAnswer; // 省级经理审核意见,0为 未审核，1为审核同意，2为拒绝

	public String ProvAnswerTime; // 省区经理审核时间

	public String ProvLocation; // 省区经理审核地点

	public String MedicinesName; // 订单药品名称组合

	public String MedicinesID; // 订单药品编号组合

	public String MedicinesNUM; // 订单药品数量组合

	public String MedicinesPrice; // 订单药品单价组合

	public MedicineOrderDetail(MedicineOrder data) {
		// TODO Auto-generated constructor stub
		Order_id = data.Order_id;
		SaleMan_id = data.SaleMan_id;
		SaleMan_Name = data.SaleMan_Name;
		Medicine_Name = data.Medicine_Name;
		Medicine_Count = data.Medicine_Count;
		Order_Date = data.Order_Date;
		Order_Status = data.Order_Status;
		Order_Status_id = data.Order_Status_id;
		Customer_Name = data.Customer_Name;
		Order_Remark = data.Order_Remark;
	}

	public int getCityAnswer() {
		return CityAnswer;
	}

	public void setCityAnswer(int cityAnswer) {
		CityAnswer = cityAnswer;
	}

	public String getCityAnswerTime() {
		return CityAnswerTime;
	}

	public void setCityAnswerTime(String cityAnswerTime) {
		CityAnswerTime = cityAnswerTime;
	}

	public String getCityLocation() {
		return CityLocation;
	}

	public void setCityLocation(String cityLocation) {
		CityLocation = cityLocation;
	}

	public int getProvAnswer() {
		return ProvAnswer;
	}

	public void setProvAnswer(int provAnswer) {
		ProvAnswer = provAnswer;
	}

	public String getProvAnswerTime() {
		return ProvAnswerTime;
	}

	public void setProvAnswerTime(String provAnswerTime) {
		ProvAnswerTime = provAnswerTime;
	}

	public String getProvLocation() {
		return ProvLocation;
	}

	public void setProvLocation(String provLocation) {
		ProvLocation = provLocation;
	}

	@Override
	public String getMedicinesName() {
		return MedicinesName;
	}

	@Override
	public void setMedicinesName(String medicinesName) {
		MedicinesName = medicinesName;
	}

	@Override
	public String getMedicinesID() {
		return MedicinesID;
	}

	@Override
	public void setMedicinesID(String medicinesID) {
		MedicinesID = medicinesID;
	}

	@Override
	public String getMedicinesNUM() {
		return MedicinesNUM;
	}

	@Override
	public void setMedicinesNUM(String medicinesNUM) {
		MedicinesNUM = medicinesNUM;
	}

	@Override
	public String getMedicinesPrice() {
		return MedicinesPrice;
	}

	@Override
	public void setMedicinesPrice(String medicinesPrice) {
		MedicinesPrice = medicinesPrice;
	}

	public MedicineOrderDetail() {
		// TODO Auto-generated constructor stub
	}

	public String getOrder_Result() {
		return Order_Result;
	}

	public void setOrder_Result(String order_Result) {
		Order_Result = order_Result;
	}

	public String getOrder_Location() {
		return Order_Location;
	}

	public void setOrder_Location(String order_Location) {
		Order_Location = order_Location;
	}

	public String getResult_Date() {
		return Result_Date;
	}

	public void setResult_Date(String result_Date) {
		Result_Date = result_Date;
	}

	@Override
	public String toString() {
		return "MedicineOrderDetail [Order_Result=" + Order_Result
				+ ", Order_Location=" + Order_Location + ", Result_Date="
				+ Result_Date + "]";
	}

	public static MedicineOrderDetail GetMedicineOrderDetail(byte[] responseData)
			throws AppException {
		if (null == responseData) {
			return null;

		}

		JSONObject obj = null;
		MedicineOrderDetail data = null;

		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");
			System.out
					.println("MedicineOrderDetail.GetMedicineOrderDetail() json_data{"
							+ obj + "}");
			if (null == obj) {
				throw AppException.http(new Exception());
			}
			data = new MedicineOrderDetail();

			data.Order_id = obj.getInt("OrderID");
			data.SaleMan_id = obj.getInt("SaleManID");
			data.SaleMan_Name = obj.getString("SaleManName");
			data.Order_Date = obj.getString("OrderDate");
			data.Order_Remark = obj.getString("OrderRemark");
			data.Order_Status_id = obj.getInt("OrderStatusID");
			data.Customer_Name = obj.getString("CustomerName");

			data.Order_Location = obj.getString("OrderLocation");
			data.Result_Date = obj.getString("ResultDate");
			// data.Order_Result = obj.getString("Order_Result");

			data.CityAnswer = obj.getInt("CityAnswer");

			data.CityAnswerTime = obj.getString("CityAnswerTime");
			data.CityLocation = obj.getString("CityLocation");
			data.ProvAnswer = obj.getInt("ProvAnswer");
			data.CityAnswer = obj.getInt("CityAnswer");
			data.ProvAnswerTime = obj.getString("ProvAnswerTime");
			data.ProvLocation = obj.getString("ProvLocation");
			data.MedicinesName = obj.getString("MedicinesName");

			data.MedicinesID = obj.getString("MedicinesID");
			data.MedicinesNUM = obj.getString("MedicinesNUM");
			data.MedicinesPrice = obj.getString("MedicinesPrice");
			data.Order_id = obj.getInt("OrderID");
			data.SaleMan_id = obj.getInt("SaleManID");
			//
			data.SaleMan_Name = obj.getString("SaleManName");
			// data.Medicine_Name = obj.getString("MedicineName");
			// data.medicineid = obj.getInt("MedicineID");
			// data.Medicine_Count = obj.getInt("MedicineCount");

			data.Order_Date = obj.getString("OrderDate");
			data.Order_Status = obj.getString("OrderStatus");
			data.Order_Status_id = obj.getInt("OrderStatusID");

			data.Customer_Name = obj.getString("CustomerName");
			data.Order_Remark = obj.getString("OrderRemark");
			data.MedicinesName = obj.getString("MedicinesName");
			data.setCustomerid(obj.getInt("CustomerID"));
			data.MedicinesID = obj.getString("MedicinesID");
			data.MedicinesNUM = obj.getString("MedicinesNUM");
			data.MedicinesPrice = obj.getString("MedicinesPrice");
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.IO(e);
		}

		return data;
	}

	public void UpdateContent(MedicineOrderDetail obj) {
		// TODO Auto-generated method stub
		Order_Result = obj.Order_Result;
		Order_Location = obj.Order_Location;
		Result_Date = obj.Result_Date;
		// public int CityAnswer; // 地区经理审核意见,0为 未审核，1为审核同意，2为拒绝
		CityAnswer = obj.getCityAnswer();
		//
		// public String CityAnswerTime; // 地区经理审核时间
		CityAnswerTime = obj.getCityAnswerTime();
		//
		// public String CityLocation; // 地区经理审核地点
		CityLocation = obj.getCityLocation();
		//
		// public int ProvAnswer; // 省级经理审核意见,0为 未审核，1为审核同意，2为拒绝
		//
		ProvAnswer = obj.getProvAnswer();
		// public String ProvAnswerTime; // 省区经理审核时间
		ProvAnswerTime = obj.getProvAnswerTime();
		//
		// public String ProvLocation; // 省区经理审核地点
		ProvLocation = obj.getProvLocation();
		//
		// public String MedicinesName; // 订单药品名称组合
		MedicinesName = obj.getMedicinesName();
		//
		// public String MedicinesID; // 订单药品编号组合
		MedicinesID = obj.getMedicinesID();
		//
		// public String MedicinesNUM; // 订单药品数量组合
		MedicinesNUM = obj.getMedicinesNUM();

		//
		// public String MedicinesPrice; // 订单药品单价组合
		MedicinesPrice = obj.getMedicinesPrice();

	}

	public String getProvinceCheck() {
		switch (ProvAnswer) {
		case 0:
			return "未审核";

		case 1:
			return "审核同意";
		case 2:
			return "审核不同意";
		}
		return null;
	}

	public String getCityCheck() {
		switch (CityAnswer) {
		case 0:
			return "未审核";

		case 1:
			return "审核同意";
		case 2:
			return "审核不同意";
		}
		return null;
	}
}
