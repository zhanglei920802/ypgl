package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.R;
import xzd.mobile.android.model.CustomerConsume;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaperConsume extends BaseAdapter {
	private Context context = null;
	private LayoutInflater inflator = null;
	private List<CustomerConsume> datas = null;

	private CustomerConsume tmp = null;
	private int rs_id = R.layout.terminate_consume_item;
	private Activity activity = null;

	public AdaperConsume(List<CustomerConsume> _datas, Activity _activity) {
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
	public CustomerConsume getItem(int position) {
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
			holder.consumer_name = (TextView) convertView
					.findViewById(R.id.consumer_name);
			holder.visit_date = (TextView) convertView
					.findViewById(R.id.visit_date);
			holder.medicine_name = (TextView) convertView
					.findViewById(R.id.medicine_name);
			holder.medicine_count = (TextView) convertView
					.findViewById(R.id.medicine_count);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.consumer_name.setText("终端客户" + tmp.getCustomer_name());
		holder.visit_date
				.setText(tmp.getBegin_date() + "~" + tmp.getEnd_date());
		holder.medicine_name.setText("药品名称：" + tmp.getMedicine_name());
		holder.medicine_count.setText("销量：" + tmp.getConsume_count());
		return convertView;
	}

	private final class Holder {
		TextView consumer_name;
		TextView visit_date;
		TextView medicine_name;
		TextView medicine_count;
	};
}
