package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

public class MedicineList extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2187596018121074860L;
	private List<MedicineOrder> medicine_list = new ArrayList<MedicineOrder>();

	public MedicineList(List<MedicineOrder> medicine_list) {
		super();
		this.medicine_list = medicine_list;
	}

	public MedicineList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<MedicineOrder> getMedicine_list() {
		return medicine_list;
	}

	public void setMedicine_list(List<MedicineOrder> medicine_list) {
		this.medicine_list = medicine_list;
	}

}
