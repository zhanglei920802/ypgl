package xzd.mobile.android.model;

import java.util.List;

import xzd.mobile.android.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterCommon extends BaseAdapter {
	private Context context = null;
	private LayoutInflater inflator = null;
	private List<AreaInfo> datas = null;
	private AreaInfo tmp = null;
	private int rs_id = R.layout.simple_list_item;

	public AdapterCommon(Activity activity, List<AreaInfo> _datas) {
		// TODO Auto-generated constructor stub
		datas = _datas;
		context = activity;
		inflator = activity.getLayoutInflater();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public AreaInfo getItem(int position) {
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

			holder.simple_tv_item = (TextView) convertView
					.findViewById(R.id.simple_tv_item);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.simple_tv_item.setText("" + tmp.getArea_Name());

		return convertView;
	}

	private final class Holder {
		TextView simple_tv_item;

	}
}
