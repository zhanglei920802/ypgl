package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class StoChange extends Base {
	private int Sto_ID;
	private int Customer_ID;
	private String Customer_Name;
	private String Medicine_Name;
	private int Medicine_Num;
	private int Saler_ID;
	private String Saler_Name;
	private String Input_Time;
	private String Input_Date;

	public String getCustomer_Name() {
		return Customer_Name;
	}

	public void setCustomer_Name(String customer_Name) {
		Customer_Name = customer_Name;
	}

	public String getSaler_Name() {
		return Saler_Name;
	}

	public void setSaler_Name(String saler_Name) {
		Saler_Name = saler_Name;
	}

	public String getInput_Time() {
		return Input_Time;
	}

	public void setInput_Time(String input_Time) {
		Input_Time = input_Time;
	}

	public int getSto_ID() {
		return Sto_ID;
	}

	public void setSto_ID(int sto_ID) {
		Sto_ID = sto_ID;
	}

	public int getCustomer_ID() {
		return Customer_ID;
	}

	public void setCustomer_ID(int customer_ID) {
		Customer_ID = customer_ID;
	}

	public String getMedicine_Name() {
		return Medicine_Name;
	}

	public void setMedicine_Name(String medicine_Name) {
		Medicine_Name = medicine_Name;
	}

	public int getMedicine_Num() {
		return Medicine_Num;
	}

	public void setMedicine_Num(int medicine_Num) {
		Medicine_Num = medicine_Num;
	}

	public int getSaler_ID() {
		return Saler_ID;
	}

	public void setSaler_ID(int saler_ID) {
		Saler_ID = saler_ID;
	}

	public String getInput_Date() {
		return Input_Date;
	}

	public void setInput_Date(String input_Date) {
		Input_Date = input_Date;
	}

	@Override
	public String toString() {
		return "StoChange [Sto_ID=" + Sto_ID + ", Customer_ID=" + Customer_ID
				+ ", Medicine_Name=" + Medicine_Name + ", Medicine_Num="
				+ Medicine_Num + ", Saler_ID=" + Saler_ID + ", Input_Date="
				+ Input_Date + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static List<StoChange> GetStoChanges(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<StoChange> datas = null;
		StoChange tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<StoChange>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new StoChange();
				tmp_obj = array.getJSONObject(i);

				tmp.Sto_ID = tmp_obj.getInt("StoID");
				tmp.Customer_ID = tmp_obj.getInt("CustomerID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");
				tmp.Medicine_Name = tmp_obj.getString("MedicineName");
				tmp.Medicine_Num = tmp_obj.getInt("MedicineNum");
				tmp.Saler_ID = tmp_obj.getInt("SalerID");
				tmp.Saler_Name = tmp_obj.getString("SalerName");
				tmp.Input_Time  = tmp_obj.getString("InputTime");
				tmp.Input_Date = tmp_obj.getString("InputDate");
				
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
