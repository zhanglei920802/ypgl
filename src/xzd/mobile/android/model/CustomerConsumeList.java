package xzd.mobile.android.model;

import java.util.List;

public class CustomerConsumeList extends Base {

	private List<CustomerConsume> datas = null;

	public List<CustomerConsume> getDatas() {
		return datas;
	}

	public void setDatas(List<CustomerConsume> datas) {
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
		return "CustomerConsumeList [datas=" + datas + "]";
	}

}
