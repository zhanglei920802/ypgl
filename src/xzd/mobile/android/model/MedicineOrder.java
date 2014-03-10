package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class MedicineOrder extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 329901753827643952L;

	protected int Order_id;
	protected int SaleMan_id;
	protected String SaleMan_Name;
	protected String Medicine_Name;
	protected int Medicine_Count;
	protected String Order_Date;
	protected String Order_Status;
	protected int Order_Status_id;
	protected String Customer_Name;
	protected String Order_Remark = "";
	private int medicineid = 0;
	private int customerid = 0;
	private String TotalPrice = "";

	private String MedicinesName = "";
	private String MedicinesID = "";
	private String MedicinesNUM="";
	private String MedicinesPrice ="";
	public String getTotalPrice() {
		return TotalPrice;
	}

	public String getMedicinesName() {
		return MedicinesName;
	}

	public void setMedicinesName(String medicinesName) {
		MedicinesName = medicinesName;
	}

	public String getMedicinesID() {
		return MedicinesID;
	}

	public void setMedicinesID(String medicinesID) {
		MedicinesID = medicinesID;
	}

	public String getMedicinesNUM() {
		return MedicinesNUM;
	}

	public void setMedicinesNUM(String medicinesNUM) {
		MedicinesNUM = medicinesNUM;
	}

	public String getMedicinesPrice() {
		return MedicinesPrice;
	}

	public void setMedicinesPrice(String medicinesPrice) {
		MedicinesPrice = medicinesPrice;
	}

	public void setTotalPrice(String totalPrice) {
		TotalPrice = totalPrice;
	}

	public String getOrder_Remark() {
		return Order_Remark;
	}

	public int getMedicineid() {
		return medicineid;
	}

	public void setMedicineid(int medicineid) {
		this.medicineid = medicineid;
	}

	public int getCustomerid() {
		return customerid;
	}

	public void setCustomerid(int customerid) {
		this.customerid = customerid;
	}

	public void setOrder_Date(String order_Date) {
		Order_Date = order_Date;
	}

	public void setOrder_Remark(String order_Remark) {
		Order_Remark = order_Remark;
	}

	public int getOrder_id() {
		return Order_id;
	}

	public void setOrder_id(int order_id) {
		Order_id = order_id;
	}

	public int getSaleMan_id() {
		return SaleMan_id;
	}

	public void setSaleMan_id(int saleMan_id) {
		SaleMan_id = saleMan_id;
	}

	public String getSaleMan_Name() {
		return SaleMan_Name;
	}

	public void setSaleMan_Name(String saleMan_Name) {
		SaleMan_Name = saleMan_Name;
	}

	public String getMedicine_Name() {
		return Medicine_Name;
	}

	public void setMedicine_Name(String medicine_Name) {
		Medicine_Name = medicine_Name;
	}

	public int getMedicine_Count() {
		return Medicine_Count;
	}

	public void setMedicine_Count(int medicine_Count) {
		Medicine_Count = medicine_Count;
	}

	public String getOrder_Date() {
		return Order_Date;
	}

	public String getOrder_Status() {
		if (Order_Status_id == 0) {
			return "新申请";
		} else if (Order_Status_id == 1) {
			return "已批准";
		} else if (Order_Status_id == 2) {
			return "已发货";
		} else if (Order_Status_id == 3) {
			return "未批准";
		} else {
			return "";
		}

	}

	public void setOrder_Status(String order_Status) {
		Order_Status = order_Status;
	}

	public int getOrder_Status_id() {
		return Order_Status_id;
	}

	public void setOrder_Status_id(int order_Status_id) {
		Order_Status_id = order_Status_id;
	}

	public String getCustomer_Name() {
		return Customer_Name;
	}

	public void setCustomer_Name(String customer_Name) {
		Customer_Name = customer_Name;
	}

	@Override
	public String toString() {
		return "MedicineOrder [Order_id=" + Order_id + ", SaleMan_id="
				+ SaleMan_id + ", SaleMan_Name=" + SaleMan_Name
				+ ", Medicine_Name=" + Medicine_Name + ", Medicine_Count="
				+ Medicine_Count + ", Order_Date=" + Order_Date
				+ ", Order_Status=" + Order_Status + ", Order_Status_id="
				+ Order_Status_id + ", Customer_Name=" + Customer_Name + "]";
	}

	/**
	 * 解析成功，返回真，否则返回null;
	 * 
	 * @param response
	 * @return
	 * @throws AppException
	 */
	public static List<MedicineOrder> parseMediceOrders(byte[] response)
			throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<MedicineOrder> datas = null;
		MedicineOrder tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<MedicineOrder>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new MedicineOrder();
				tmp_obj = array.getJSONObject(i);

				tmp.Order_id = tmp_obj.getInt("OrderID");
				tmp.SaleMan_id = tmp_obj.getInt("SaleManID");
				tmp.SaleMan_Name = tmp_obj.getString("SaleManName");
//				tmp.Medicine_Name = tmp_obj.getString("MedicineName");
//				tmp.Medicine_Count = tmp_obj.getInt("MedicineCount");
				tmp.Order_Date = tmp_obj.getString("OrderDate");
				tmp.Order_Remark = tmp_obj.getString("OrderRemark");
//				tmp.Order_Status = tmp_obj.getString("OrderStatus");
				tmp.Order_Status_id = tmp_obj.getInt("OrderStatusID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");
				tmp.customerid = tmp_obj.getInt("CustomerID");
//				tmp.medicineid = tmp_obj.getInt("MedicineID");
				tmp.TotalPrice = tmp_obj.getString("TotalPrice");
				
				datas.add(tmp);
				tmp = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			datas = null;
			throw AppException.http(new Exception());
		} finally {
			tmp_obj = null;
			tmp = null;
			array = null;

		}

		return datas;

	}

	public static MedicineOrder GetMedicineOrderInfo(byte[] responseData)
			throws AppException {
		if (null == responseData) {
			return null;

		}

		JSONObject obj = null;
		MedicineOrder data = null;

		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");

			if (null == obj) {
				throw AppException.http(new Exception());
			}
			data = new MedicineOrder();
			//
			data.Order_id = obj.getInt("OrderID");
			data.SaleMan_id = obj.getInt("SaleManID");
			//
			data.SaleMan_Name = obj.getString("SaleManName");
//			 data.Medicine_Name = obj.getString("MedicineName");
//			 data.medicineid = obj.getInt("MedicineID");
//			 data.Medicine_Count = obj.getInt("MedicineCount");

			data.Order_Date = obj.getString("OrderDate");
			 data.Order_Status = obj.getString("OrderStatus");
			data.Order_Status_id = obj.getInt("OrderStatusID");
			data.customerid = obj.getInt("CustomerID");
			data.Customer_Name = obj.getString("CustomerName");
			data.Order_Remark = obj.getString("OrderRemark");
			data.MedicinesName =obj.getString("MedicinesName");
			data.MedicinesID =obj.getString("MedicinesID");
			data.MedicinesNUM =obj.getString("MedicinesNUM");
			data.MedicinesPrice =obj.getString("MedicinesPrice");
//			data.TotalPrice = obj.getString("TotalPrice");
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.IO(e);
		}

		return data;
	}
}
