package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class VisitRecord extends Base {
	private int VisitRecord_id;
	private String Saler_Name;
	private int Customer_ID;
	private String Customer_Name;
	private String Visit_Address;
	private String Visit_Date;

	public int getVisitRecord_id() {
		return VisitRecord_id;
	}

	public VisitRecord(int visitRecord_id, String saler_Name, int customer_ID,
			String customer_Name, String visit_Address, String visit_Date) {
		super();
		VisitRecord_id = visitRecord_id;
		Saler_Name = saler_Name;
		Customer_ID = customer_ID;
		Customer_Name = customer_Name;
		Visit_Address = visit_Address;
		Visit_Date = visit_Date;
	}

	public void setVisitRecord_id(int visitRecord_id) {
		VisitRecord_id = visitRecord_id;
	}

	public String getSaler_Name() {
		return Saler_Name;
	}

	public void setSaler_Name(String saler_Name) {
		Saler_Name = saler_Name;
	}

	public int getCustomer_ID() {
		return Customer_ID;
	}

	public void setCustomer_ID(int customer_ID) {
		Customer_ID = customer_ID;
	}

	public String getCustomer_Name() {
		return Customer_Name;
	}

	public void setCustomer_Name(String customer_Name) {
		Customer_Name = customer_Name;
	}

	public String getVisit_Address() {
		return Visit_Address;
	}

	public void setVisit_Address(String visit_Address) {
		Visit_Address = visit_Address;
	}

	public String getVisit_Date() {
		return Visit_Date;
	}

	public void setVisit_Date(String visit_Date) {
		Visit_Date = visit_Date;
	}

	public VisitRecord() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "VisitRecord [VisitRecord_id=" + VisitRecord_id
				+ ", Saler_Name=" + Saler_Name + ", Customer_ID=" + Customer_ID
				+ ", Customer_Name=" + Customer_Name + ", Visit_Address="
				+ Visit_Address + ", Visit_Date=" + Visit_Date + "]";
	}

	public static List<VisitRecord> GetVisitRecords(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<VisitRecord> datas = null;
		VisitRecord tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<VisitRecord>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new VisitRecord();
				tmp_obj = array.getJSONObject(i);

				tmp.VisitRecord_id = tmp_obj.getInt("VisitRecordID");
				tmp.Saler_Name = tmp_obj.getString("SalerName");
				tmp.Customer_ID = tmp_obj.getInt("CustomerID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");
				tmp.Visit_Address = tmp_obj.getString("VisitAddress");
				tmp.Visit_Date = tmp_obj.getString("VisitDate");

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

}
