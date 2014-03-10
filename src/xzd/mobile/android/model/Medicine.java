package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class Medicine extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642393860115647062L;
	private int Medicine_id;
	private String Medicine_Name;
	private double MedicinePrice = 0;
	 
	public int getMedicine_id() {
		return Medicine_id;
	}

	public void setMedicine_id(int medicine_id) {
		Medicine_id = medicine_id;
	}

	public String getMedicine_Name() {
		return Medicine_Name;
	}

	public void setMedicine_Name(String medicine_Name) {
		Medicine_Name = medicine_Name;
	}

	@Override
	public String toString() {
		return "Medicine [Medicine_id=" + Medicine_id + ", Medicine_Name="
				+ Medicine_Name + "]";
	}

	public double getMedicinePrice() {
		return MedicinePrice;
	}

	public void setMedicinePrice(double medicinePrice) {
		MedicinePrice = medicinePrice;
	}

	public static List<Medicine> ParseMedicineList(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}

		JSONArray array = null;
		List<Medicine> datas = null;
		Medicine tmp = null;
		JSONObject tmp_obj = null;

		try {
			datas = new ArrayList<Medicine>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp_obj = array.getJSONObject(i);
				tmp = new Medicine();

				tmp.Medicine_id = tmp_obj.getInt("MedicineID");
				tmp.Medicine_Name = tmp_obj.getString("MedicineName");
//				tmp.MedicinePrice = tmp_obj.getDouble("MedicinePrice");
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
