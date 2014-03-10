package xzd.mobile.android.ui.adapter;

import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;

import java.util.List;

import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.ui.AddOrderActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MedicineOrderAdapter extends BaseAdapter {
	public static final String TAG = "MedicineOrderAdapter";
	private LayoutInflater inflator = null;
	private List<MedicineOrder> datas = null;
	private MedicineOrder tmp = null;
	private int rs_id = R.layout.order_list_item_v2;
	private Activity activity = null;

	public MedicineOrderAdapter(List<MedicineOrder> _datas, Activity _activity) {
		inflator = _activity.getLayoutInflater();
		datas = _datas;
		activity = _activity;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public MedicineOrder getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		tmp = datas.get(position);
		Holder holder = null;

		if (null == convertView) {
			convertView = inflator.inflate(rs_id, null);
			holder = new Holder();
			// holder.medicine_name = (TextView) convertView
			// .findViewById(R.id.medicine_name);
			// holder.sales_man_name = (TextView) convertView
			// .findViewById(R.id.sales_man_name);
			// holder.date = (TextView) convertView.findViewById(R.id.date);
			// holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			// holder.modify = (Button) convertView.findViewById(R.id.modify);
			holder.customer = (TextView) convertView
					.findViewById(R.id.customer);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			// holder.modify.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Log.d(TAG, "postion:" + position);
			// doEditOrder(datas.get(position));
			// }
			// });
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		// holder.modify.setVisibility(View.GONE);
		// holder.medicine_name.setText("" + tmp.getMedicine_Name());
		// holder.sales_man_name.setText("销售员：" + tmp.getSaleMan_Name());
		holder.date.setText("时间：" + tmp.getOrder_Date());
		// holder.count.setText("数量：" + tmp.getMedicine_Count());
		holder.status.setText("状态：" + tmp.getOrder_Status());
		holder.customer.setText("客户：" + tmp.getCustomer_Name());
		holder.price.setText("订单金额:" + tmp.getTotalPrice());
		// if (tmp.getOrder_Status_id() == 0
		// && appContext.user.getCounterMain_ID() == 0) {
		// holder.modify.setVisibility(View.VISIBLE);
		// }
		return convertView;
	}

	protected void doEditOrder(MedicineOrder medicineOrder) {
		Bundle bundle = new Bundle();
		Log.d(TAG, "准备发送数据" + medicineOrder.toString());
		// 跳转到详细界面
		bundle.putSerializable(AddOrderActivity.ORDER_DATA, medicineOrder);
		bundle.putInt(AddOrderActivity.ACTION_TYPE,
				AddOrderActivity.ACTION_EDIT);
		UIHelper.getInstance().showOrderActivity(activity, bundle);

		bundle = null;
	}

	private final class Holder {
		TextView price;
		TextView date;
		TextView status;
		TextView customer;

	};
}
