package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.R;
import xzd.mobile.android.model.SalerSimple;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaterCouterMan extends BaseAdapter {

	private LayoutInflater inflator = null;
	private List<SalerSimple> datas = null;
	private SalerSimple tmp = null;
	private int rs_id = R.layout.simple_list_item;

	public AdaterCouterMan(Activity activity, List<SalerSimple> _datas) {
		// TODO Auto-generated constructor stub
		datas = _datas;

		inflator = activity.getLayoutInflater();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public SalerSimple getItem(int position) {
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

		holder.tv.setText("" + tmp.getSaler_name());
		return convertView;
	}

	private final class Holder {
		TextView tv;
	}
}
