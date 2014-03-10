package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.R;
import xzd.mobile.android.model.StoChange;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterStoChange extends BaseAdapter {
	private Context context = null;
	private LayoutInflater inflator = null;
	private List<StoChange> datas = null;

	private StoChange tmp = null;
	private int rs_id = R.layout.terminate_lib_item;
	private Activity activity = null;

	public AdapterStoChange(List<StoChange> _datas, Activity _activity) {
		// TODO Auto-generated constructor stub
		context = _activity;
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
	public StoChange getItem(int position) {
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
		tmp = datas.get(position);
		Holder holder = null;

		if (null == convertView) {
			convertView = inflator.inflate(rs_id, null);
			holder = new Holder();
			holder.salesman_name = (TextView) convertView
					.findViewById(R.id.salesman_name);
			holder.custom_name = (TextView) convertView
					.findViewById(R.id.custom_name);
			holder.visit_date = (TextView) convertView
					.findViewById(R.id.visit_date);
			holder.visit_address = (TextView) convertView
					.findViewById(R.id.visit_address);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.salesman_name.setText("客户 " + tmp.getCustomer_Name());
		holder.custom_name.setText("" + tmp.getMedicine_Name());
		holder.visit_date.setText("" + tmp.getInput_Date());
		holder.visit_address.setText("数量:" + tmp.getMedicine_Num());

		return convertView;
	}

	private final class Holder {
		TextView salesman_name;
		TextView custom_name;
		TextView visit_date;
		TextView visit_address;

	};
}
