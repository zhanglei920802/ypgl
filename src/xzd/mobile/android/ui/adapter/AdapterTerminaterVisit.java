package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.model.VisitRecord;
import xzd.mobile.android.ui.ActivityVisitAdd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterTerminaterVisit extends BaseAdapter {
	public static final String TAG = "AdapterTerminaterVisit";

	private Context context = null;
	private LayoutInflater inflator = null;
	private List<VisitRecord> datas = null;
	private Activity activity = null;
	private VisitRecord tmp = null;
	private int rs_id = R.layout.terminate_visit_item;
	private AppContext appContext = null;

	public AdapterTerminaterVisit(List<VisitRecord> _datas, Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		context = activity;
		inflator = activity.getLayoutInflater();
		datas = _datas;
		appContext = (AppContext) activity.getApplication();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public VisitRecord getItem(int position) {
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
			holder.salesman_name = (TextView) convertView
					.findViewById(R.id.salesman_name);
			holder.visit_date = (TextView) convertView
					.findViewById(R.id.visit_date);
			holder.custom_name = (TextView) convertView
					.findViewById(R.id.custom_name);
			holder.visit_address = (TextView) convertView
					.findViewById(R.id.visit_address);
			holder.modify = (TextView) convertView.findViewById(R.id.modify);
			holder.modify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					doEdit(tmp);
					Log.d(TAG, "current choose:" + tmp.toString());
				}
			});
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.salesman_name.setText("业务员:" + tmp.getSaler_Name());
		holder.custom_name.setText("终端客户：" + tmp.getCustomer_Name());
		holder.visit_date.setText("" + tmp.getVisit_Date());
		holder.visit_address.setText("拜访地点:" + tmp.getVisit_Address());
		holder.modify.setVisibility(View.GONE);
		// // 日期计算
		// if (xzd.mobile.android.common.DateUtil.getInstance().chkDate(
		// tmp.getVisit_Date(), 7)
		// && appContext.user.getCounterMain_ID() == 0) {
		// holder.modify.setVisibility(View.VISIBLE);
		//
		// }
		return convertView;
	}

	protected void doEdit(VisitRecord tmp2) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putInt(ActivityVisitAdd.ACTION_TYPE, ActivityVisitAdd.ACTION_EDIT);
		data.putSerializable(ActivityVisitAdd.VISIT_DATA, tmp2);
		UIHelper.getInstance().showVisitAddActivity(activity, data);
	}

	private final class Holder {
		TextView salesman_name;
		TextView visit_date;
		TextView custom_name;
		TextView visit_address;
		TextView modify = null;
	};
}
