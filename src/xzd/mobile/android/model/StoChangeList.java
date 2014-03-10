package xzd.mobile.android.model;

import java.util.List;

public class StoChangeList extends Entity {

	private List<StoChange> datas = null;

	public List<StoChange> getDatas() {
		return datas;
	}

	public void setDatas(List<StoChange> datas) {
		this.datas = datas;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "StoChangeList [datas=" + datas + "]";
	}

}
