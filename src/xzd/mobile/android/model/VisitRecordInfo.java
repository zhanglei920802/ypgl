package xzd.mobile.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class VisitRecordInfo extends VisitRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = 627573249578468696L;
	private String visit_remark;
	private String visit_location;

	public int ManagerID = 0;
	public String ManagerName = "";
	public String Answer = "";
	public String AnswerTime = "";
	public String AnswerLocation = "";
	
	public VisitRecord getVisitRecord(){
		VisitRecord mRecord = new VisitRecord();
		mRecord.setVisitRecord_id(getVisitRecord_id());
		mRecord.setSaler_Name(getSaler_Name());
		mRecord.setCustomer_ID(getCustomer_ID());
		mRecord.setCustomer_Name(getCustomer_Name());
		 mRecord.setVisit_Address(getVisit_Address());
		 mRecord.setVisit_Date(getVisit_Date());
		return mRecord;
	}
	public int getManagerID() {
		return ManagerID;
	}

	public void setManagerID(int managerID) {
		ManagerID = managerID;
	}

	public String getManagerName() {
		return ManagerName;
	}

	public void setManagerName(String managerName) {
		ManagerName = managerName;
	}

	public String getAnswer() {
		return Answer;
	}

	public void setAnswer(String answer) {
		Answer = answer;
	}

	public String getAnswerTime() {
		return AnswerTime;
	}

	public void setAnswerTime(String answerTime) {
		AnswerTime = answerTime;
	}

	public String getAnswerLocation() {
		return AnswerLocation;
	}

	public void setAnswerLocation(String answerLocation) {
		AnswerLocation = answerLocation;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public VisitRecordInfo(VisitRecord data) {
		super(data.getVisitRecord_id(), data.getSaler_Name(), data
				.getCustomer_ID(), data.getCustomer_Name(), data
				.getVisit_Address(), data.getVisit_Date());
	}

	public String getVisit_remark() {
		return visit_remark;
	}

	public void setVisit_remark(String visit_remark) {
		this.visit_remark = visit_remark;
	}

	public String getVisit_location() {
		return visit_location;
	}

	public VisitRecordInfo() {
		// TODO Auto-generated constructor stub
	}

	public void setVisit_location(String visit_location) {
		this.visit_location = visit_location;
	}

	@Override
	public String toString() {
		return "VisitRecordInfo [visit_remark=" + visit_remark
				+ ", visit_location=" + visit_location + ", ManagerID="
				+ ManagerID + ", ManagerName=" + ManagerName + ", Answer="
				+ Answer + ", AnswerTime=" + AnswerTime + ", AnswerLocation="
				+ AnswerLocation + "]";
	}

	public static VisitRecordInfo GetMedicineOrderDetail(byte[] responseData)
			throws AppException {
		if (null == responseData) {
			return null;

		}

		JSONObject obj = null;
		VisitRecordInfo data = null;

		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");

			if (null == obj) {
				throw AppException.http(new Exception());
			}
			data = new VisitRecordInfo();

			data.setVisitRecord_id(obj.getInt("VisitRecordID"));
			data.setSaler_Name(obj.getString("SalerName"));
			data.setCustomer_ID(obj.getInt("CustomerID"));
			data.setCustomer_Name(obj.getString("CustomerName"));
			data.setVisit_Address(obj.getString("VisitAddress"));
			data.setVisit_Date(obj.getString("VisitDate"));
			data.setVisit_location(obj.getString("VisitLocation"));
			data.setVisit_remark(obj.getString("VisitRemark"));
			data.ManagerID = obj.getInt("ManagerID");
			data.ManagerName = obj.getString("ManagerName");
			data.Answer = obj.getString("Answer");
			data.AnswerTime = obj.getString("AnswerTime");
			data.AnswerLocation = obj.getString("AnswerLocation");
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.IO(e);
		}

		return data;
	}

	public void update(VisitRecordInfo obj) {
		// TODO Auto-generated method stub
		// setVisit_remark(obj.getVisit_remark());
		// setVisit_location(obj.getVisit_location());
	}
}
