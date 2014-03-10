package xzd.mobile.android.ui.adapter;

import java.util.List;

import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.ui.ActivityAddOrEditConsumer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterTerminate extends BaseAdapter {
	private LayoutInflater inflator = null;
	private List<CustomerModel> datas = null;
	private final String TAG = "AdapterTerminate";
	private boolean DEBUG = true;
	private CustomerModel tmp = null;
	private int rs_id = R.layout.terminate_manage_item;
	private static Activity activity = null;

	public AdapterTerminate(List<CustomerModel> _datas, Activity _activity) {
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
	public CustomerModel getItem(int position) {
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
			holder.terminate_area = (TextView) convertView
					.findViewById(R.id.terminate_area);
			holder.contact_man = (TextView) convertView
					.findViewById(R.id.contact_man);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);

			holder.edit = (TextView) convertView.findViewById(R.id.modify);
			holder.group_name = (TextView) convertView.findViewById(R.id.group_name);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.edit.setVisibility(View.INVISIBLE);
		//判定权限
		
		// if(AppContext.user!=null){
		//
		// if(AppContext.user.getCounterMain_ID()==0){
		// holder.edit.setVisibility(View.VISIBLE);
		// holder.edit.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if(position<0 ||position>datas.size()){
		// if(DEBUG){
		// Log.d(TAG, "illegal position ");
		// }
		// return ;
		// }
		//
		// doEditOrder(datas.get(position));
		// }
		// });
		// }
		//
		// }
		
		
		
		holder.sales_man_name.setText("业务员:" + tmp.getSaler_Name());
		holder.terminate_name.setText("" + tmp.getCustomer_Name());
		holder.terminate_area.setText("区域：" + tmp.getCustomer_Area());
		holder.terminate_name.setText("" + tmp.getCustomer_Name());
		holder.contact_man.setText("联系人：" + tmp.getCustomer_LinkMan());
		holder.phone.setText("联系电话:" + tmp.getCustomer_Tel());
		holder.group_name.setText(""+tmp.getCustomerGroupName());
		return convertView;
	}

	public  static void doEditOrder(CustomerModel customerModel) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();

		// 跳转到详细界面
		bundle.putSerializable(ActivityAddOrEditConsumer.CUSTOM_DATA,
				customerModel);
		bundle.putInt(ActivityAddOrEditConsumer.ACTION_TYPE,
				ActivityAddOrEditConsumer.ACTION_EDIT);
		UIHelper.getInstance().showEditOrAddCustomActivity(activity, bundle);

		bundle = null;
	}

	private final class Holder {
		TextView sales_man_name;
		TextView terminate_name;
		TextView terminate_area;
		TextView contact_man;
		TextView phone;
		TextView edit = null;
		TextView group_name = null;
	};
}
