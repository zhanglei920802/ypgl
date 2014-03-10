package xzd.mobile.android.model;

public class CustomerMedicines extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = -180940707701874408L;

	public int MedicineID; // 客户关联的药品编号

	public String MedicineName; // 客户关联的药品名称

	public double MedicinePrice; // 客户关联的药品价格

	public int getMedicineID() {
		return MedicineID;
	}

	public void setMedicineID(int medicineID) {
		MedicineID = medicineID;
	}

	public String getMedicineName() {
		return MedicineName;
	}

	public void setMedicineName(String medicineName) {
		MedicineName = medicineName;
	}

	public double getMedicinePrice() {
		return MedicinePrice;
	}

	public void setMedicinePrice(double medicinePrice) {
		MedicinePrice = medicinePrice;
	}

	@Override
	public String toString() {
		return "CustomerMedicines [MedicineID=" + MedicineID
				+ ", MedicineName=" + MedicineName + ", MedicinePrice="
				+ MedicinePrice + "]";
	}

}
