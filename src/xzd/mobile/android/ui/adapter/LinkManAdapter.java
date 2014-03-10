package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.model.LinkMan;
import xzd.mobile.android.ui.MgrLinkMan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LinkManAdapter extends BaseAdapter {
	private LayoutInflater inflator = null;
	private List<LinkMan> datas = null;
	private final String TAG = "AdapterTerminate";
	private boolean DEBUG = true;
	private LinkMan tmp = null;
	private int rs_id = R.layout.xzd_linkman_item;
	private static Activity activity = null;

	public LinkManAdapter(List<LinkMan> _datas, Activity _activity) {
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
	public LinkMan getItem(int position) {
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
			holder.sales_man_name = (TextView) convertView
					.findViewById(R.id.sales_man_name);
			holder.terminate_name = (TextView) convertView
					.findViewById(R.id.terminate_name);

			holder.contact_man = (TextView) convertView
					.findViewById(R.id.contact_man);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);

			holder.edit = (TextView) convertView.findViewById(R.id.modify);

			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.edit.setVisibility(View.INVISIBLE);
		// 判定权限

		// if (AppContext.user != null) {
		//
		// if (AppContext.user.getCounterMain_ID() == 0) {
		// holder.edit.setVisibility(View.VISIBLE);
		// holder.edit.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (position < 0 || position > datas.size()) {
		// if (DEBUG) {
		// Log.d(TAG, "illegal position ");
		// }
		// return;
		// }
		//
		// doEditOrder(datas.get(position));
		// }
		// });
		// }
		//
		// }

		holder.sales_man_name.setText(tmp.getLinkManName());
		holder.terminate_name.setText("" + tmp.getBirthDay());
		holder.contact_man.setText(tmp.getSalerName());
		holder.phone.setText(tmp.getTelephone());

		return convertView;
	}

	public static void doEditOrder(LinkMan customerModel) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();

		// 跳转到详细界面
		bundle.putSerializable(MgrLinkMan.EXTRAS_LINKMAN,
				customerModel);
		bundle.putInt(MgrLinkMan.START_METHOD,
				MgrLinkMan.METHOD_EDIT);
		UIHelper.getInstance().showLinkManDetailActivity(activity, bundle);

		bundle = null;
	}

	private final class Holder {
		TextView sales_man_name;
		TextView terminate_name;
		TextView terminate_area;
		TextView contact_man;
		TextView phone;
		TextView edit = null;
	};
}
