package xzd.mobile.android.ui.adapter;

import xzd.mobile.android.R;
import java.util.List;

import xzd.mobile.android.model.CustomerSimple;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomersAdapter extends BaseAdapter {
	private Context context = null;
	private LayoutInflater inflator = null;
	private List<CustomerSimple> datas = null;
	private CustomerSimple tmp = null;
	private int rs_id = R.layout.simple_list_item;

	public MyCustomersAdapter(Activity activity, List<CustomerSimple> _datas) {

		datas = _datas;
		context = activity;
		inflator = activity.getLayoutInflater();
		// super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public CustomerSimple getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		tmp = datas.get(position);
		Holder holder = null;
		if (null == convertView) {
			convertView = inflator.inflate(rs_id, null);
			holder = new Holder();

			holder.tv = (TextView) convertView
					.findViewById(R.id.simple_tv_item);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.tv.setText("" + tmp.getCustomer_Name());
		return convertView;
	}

	private final class Holder {
		TextView tv;
	}
}
