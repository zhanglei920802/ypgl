package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class LinkMan extends Base {

	public LinkMan() {
		// TODO Auto-generated constructor stub
	}

	private int ID = 0;
	private String LinkManName = null;
	private String BirthDay = null;
	private int Sex = 0;
	private String Telephone = null;
	private String Email = null;
	private String QQ = null;
	private int SalerID = 0;
	private String SalerName = null;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getLinkManName() {
		return LinkManName;
	}

	public void setLinkManName(String linkManName) {
		LinkManName = linkManName;
	}

	public String getBirthDay() {
		return BirthDay;
	}

	public void setBirthDay(String birthDay) {
		BirthDay = birthDay;
	}

	public int getSex() {
		return Sex;
	}

	public void setSex(int sex) {
		Sex = sex;
	}

	public String getTelephone() {
		return Telephone;
	}

	public void setTelephone(String telephone) {
		Telephone = telephone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getQQ() {
		return QQ;
	}

	public void setQQ(String qQ) {
		QQ = qQ;
	}

	public int getSalerID() {
		return SalerID;
	}

	public void setSalerID(int salerID) {
		SalerID = salerID;
	}

	public String getSalerName() {
		return SalerName;
	}

	public void setSalerName(String salerName) {
		SalerName = salerName;
	}

	@Override
	public String toString() {
		return "LinkMan [ID=" + ID + ", LinkManName=" + LinkManName
				+ ", BirthDay=" + BirthDay + ", Sex=" + Sex + ", Telephone="
				+ Telephone + ", Email=" + Email + ", QQ=" + QQ + ", SalerID="
				+ SalerID + ", SalerName=" + SalerName + "]";
	}

	public LinkMan(int iD, String linkManName, String birthDay, int sex,
			String telephone, String email, String qQ, int salerID,
			String salerName) {
		super();
		ID = iD;
		LinkManName = linkManName;
		BirthDay = birthDay;
		Sex = sex;
		Telephone = telephone;
		Email = email;
		QQ = qQ;
		SalerID = salerID;
		SalerName = salerName;
	}

	public static List<LinkMan> GetLinkMan(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<LinkMan> datas = null;
		LinkMan tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<LinkMan>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new LinkMan();
				tmp_obj = array.getJSONObject(i);
				tmp.BirthDay = tmp_obj.getString("BirthDay");
				tmp.ID = tmp_obj.getInt("ID");
				tmp.LinkManName = tmp_obj.getString("LinkManName");
				tmp.Sex = tmp_obj.getInt("Sex");
				tmp.Telephone = tmp_obj.getString("Telephone");
				tmp.Email = tmp_obj.getString("Email");
				tmp.QQ = tmp_obj.getString("QQ");
				tmp.SalerID = tmp_obj.getInt("SalerID");
				tmp.SalerName = tmp_obj.getString("SalerName");

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

	public static LinkMan GetLinkManInfo(byte[] responseData) throws AppException {
		// TODO Auto-generated method stub
		if (null == responseData) {
			return null;

		}

		JSONObject obj = null;
		LinkMan tmp = null;

		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");

			if (null == obj) {
				throw AppException.http(new Exception());
			}
			tmp = new LinkMan();

			tmp.BirthDay = obj.getString("BirthDay");
			tmp.ID = obj.getInt("ID");
			tmp.LinkManName = obj.getString("LinkManName");
			tmp.Sex = obj.getInt("Sex");
			tmp.Telephone = obj.getString("Telephone");
			tmp.Email = obj.getString("Email");
			tmp.QQ = obj.getString("QQ");
			tmp.SalerID = obj.getInt("SalerID");
			tmp.SalerName = obj.getString("SalerName");

		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.IO(e);
		}

		return tmp;
	}

}
