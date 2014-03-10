package xzd.mobile.android.model;

import java.util.List;

public class CustomList extends Entity {
	
	public List<CustomerModel> getCuLists() {
		return cuLists;
	}

	public void setCuLists(List<CustomerModel> cuLists) {
		this.cuLists = cuLists;
	}

	private List<CustomerModel> cuLists = null;
}
