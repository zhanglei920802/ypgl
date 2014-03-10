package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.model.Base;
import xzd.mobile.android.model.CustomerInfoModel;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.ui.adapter.AdapterTerminate;
import xzd.mobile.android.ui.intf.ActivityItf;

public class TerminateDetailActivity extends BaseActivity implements
		ActivityItf, OnClickListener {
	private String TAG = "TerminateDetailActivity";

	public static final int HANDLER_GET_DETAIL_SUCCESS = 1;
	public static final int HANDLER_GET_DETAIL_FAILED = 2;
	public static final int HANDLER_GET_DETAIL_EXCEPTION = -1;

	public static final String TERMINATE_DATA = "terminate_data";

	private AppContext appcontext = null;
	private UIHelper helper = null;
	private TerminateBiz terminateBiz = null;
	private GroupAdapter mgGroupAdapter = null;
	// Content view
	private TextView contact_content = null;
	private TextView fax_content = null;
	private TextView address = null;
	private TextView tel_content = null;
	private TextView desc = null;
	private TextView area = null;
	private TextView sales_man = null;
	private TextView label = null;

	private ScrollView wrap_main = null;

	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;

	private FrameLayout frameLayout0 = null;
	private LinearLayout ly_detail_refresh = null;
	private LinearLayout ly_detail_loading = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView confrim = null;
	private TextView add_order = null;
	private TextView visit_cus = null;
	private TextView edit = null;

	private List<OrderBean> mDatas = null;
	private CustomerInfoModel data = null;
	private TerminateDetailHandler terminateDetailHandler = null;
	private ListView lv_order_list = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalArgumentException("参数传递为空");
		}
		data = new CustomerInfoModel(
				(CustomerModel) bundle.getSerializable(TERMINATE_DATA));

		bundle = null;
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		loading.setVisibility(View.VISIBLE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("客户详情");
		frameLayout0 = (FrameLayout) findViewById(R.id.frameLayout0);
		ly_detail_refresh = (LinearLayout) findViewById(R.id.ly_detail_refresh);
		ly_detail_loading = (LinearLayout) findViewById(R.id.ly_detail_loading);
		frameLayout0.setVisibility(View.VISIBLE);
		ly_detail_refresh.setVisibility(View.GONE);
		ly_detail_loading.setVisibility(View.VISIBLE);
		ly_detail_refresh.setOnClickListener(this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setVisibility(View.GONE);
		area = (TextView) findViewById(R.id.area);
		sales_man = (TextView) findViewById(R.id.sales_man);
		label = (TextView) findViewById(R.id.label);

		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		text.setText("正在加载努力加载中");
		ly_detail_refresh.setVisibility(View.GONE);
		ly_detail_loading.setVisibility(View.VISIBLE);

		// 初始化内容
		contact_content = (TextView) findViewById(R.id.contact_content);
		fax_content = (TextView) findViewById(R.id.fax_content);
		address = (TextView) findViewById(R.id.address);
		tel_content = (TextView) findViewById(R.id.tel_content);
		desc = (TextView) findViewById(R.id.desc);
		wrap_main = (ScrollView) findViewById(R.id.wrap_main);
		wrap_main.setVisibility(View.GONE);

		// listview
		{
			lv_order_list = (ListView) findViewById(R.id.lv_order_list);
			lv_order_list.setAdapter(mgGroupAdapter);
			lv_order_list.setVisibility(View.GONE);
		}
		{
			add_order = (TextView) findViewById(R.id.add_order);
			visit_cus = (TextView) findViewById(R.id.visit_cus);
			edit = (TextView) findViewById(R.id.edit);
			add_order.setOnClickListener(this);
			visit_cus.setOnClickListener(this);
			edit.setOnClickListener(this);

		}

		{
			/**
			 * ，编辑 按钮的作用是修改客户信息，按照修改客户进行处理(编辑按钮只有 登陆用户为业务员时才显示)
			 */

			if (AppContext.user.getCounterMain_ID() == 0) {
				edit.setVisibility(View.VISIBLE);
			} else {
				edit.setVisibility(View.GONE);
			}

		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		terminateDetailHandler = new TerminateDetailHandler();
		mDatas = new ArrayList<TerminateDetailActivity.OrderBean>();
		mgGroupAdapter = new GroupAdapter();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminate_detail);

		appcontext = (AppContext) getApplication();
		helper = UIHelper.getInstance();
		terminateBiz = TerminateBiz.getInstance();

		this.getPreActivityData();
		this.initData();
		this.initView();
		this.ObtainData();
	}

	private void ObtainData() {
		// TODO Auto-generated method stub
		GetTerminateContent(data.getCustomer_id(), false);
	}

	private void GetTerminateContent(final int customer_id,
			final boolean isReresh) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {

				CustomerInfoModel rs;
				Message msg = terminateDetailHandler.obtainMessage();
				try {
					rs = terminateBiz.GetCustomerInfo(appcontext, customer_id,
							AppContext.user.getM_id(), isReresh);

					if (rs == null) {
						throw new AppException();
					}

					Log.d(TAG, "GetTerminateContent get data[" + rs + "]");
					msg.what = HANDLER_GET_DETAIL_SUCCESS;
					msg.obj = rs;

				} catch (Exception e) {
					Log.e(TAG, "getNotificationContent Runnable：获取数据失败");
					e.printStackTrace();
					msg.what = HANDLER_GET_DETAIL_FAILED;
					msg.obj = e;

				}
				terminateDetailHandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		appcontext = null;
		helper = null;
		terminateBiz = null;

		// Content view
		contact_content = null;
		fax_content = null;
		address = null;
		tel_content = null;
		desc = null;
		wrap_main = null;

		loading = null;
		progressbar = null;
		text = null;

		frameLayout0 = null;
		ly_detail_refresh = null;
		ly_detail_loading = null;
		go_back = null;
		detail_title = null;
		confrim = null;

		data = null;
		terminateDetailHandler = null;
		super.onDestroy();
	}

	private final class TerminateDetailHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case TerminateDetailActivity.HANDLER_GET_DETAIL_SUCCESS:

				if (null != msg.obj) {
					data.UpdateContent((CustomerInfoModel) msg.obj);
					Log.d(TAG, "datas[" + data.getCustomerMedicineID() + "]");
					if (!TextUtils.isEmpty(data.getCustomerMedicineName())
							&& !TextUtils.isEmpty(data.getCustomerMedicineID())) {

						String[] medicineName = data.getCustomerMedicineName()
								.split(",");
						String[] medicineID = data.getCustomerMedicineID()
								.split(",");
						mDatas.clear();
						for (int i = 0; i < medicineID.length; i++) {
							mDatas.add(new OrderBean(medicineName[i], Integer
									.valueOf(medicineID[i])));
						}
						LayoutParams tLayoutParams = lv_order_list
								.getLayoutParams();
						tLayoutParams.height = DeviceUtil.dip2px(
								TerminateDetailActivity.this,
								60 * mDatas.size());
						lv_order_list.setLayoutParams(tLayoutParams);
						tLayoutParams = null;
						mgGroupAdapter.notifyDataSetChanged();
						lv_order_list.setVisibility(View.VISIBLE);
					}

					UpdateView();
				}
				loading.setVisibility(View.GONE);
				ly_detail_loading.setVisibility(View.GONE);
				ly_detail_refresh.setVisibility(View.VISIBLE);
				wrap_main.setVisibility(View.VISIBLE);
				AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
				anim.setDuration(1000);
				wrap_main.startAnimation(anim);
				anim = null;
				break;

			case TerminateDetailActivity.HANDLER_GET_DETAIL_EXCEPTION:

			case TerminateDetailActivity.HANDLER_GET_DETAIL_FAILED:

				loading.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("加载数据失败");
				ly_detail_loading.setVisibility(View.GONE);
				ly_detail_refresh.setVisibility(View.VISIBLE);
				break;

			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle;
		CustomerSimple tModel = null;
		switch (v.getId()) {
		case R.id.ly_detail_refresh:
			if (!appcontext.isNetworkConnected()) {// 检测网络连接
				UIHelper.getInstance();
				UIHelper.Toast(appcontext, R.string.http_exception_error);
				return;
			}
			ly_detail_loading.setVisibility(View.VISIBLE);
			ly_detail_refresh.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			progressbar.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
			text.setText("正在努力加载中");
			wrap_main.setVisibility(View.GONE);
			GetTerminateContent(data.getCustomer_id(), true);

			break;

		case R.id.go_back:
			onDestroy();
			break;
		case R.id.add_order:
			/**
			 * 点击其他2个按钮分别进入添加订单 及添加拜访记录界面，因为此处在进入添加订单和添加拜访记录界面
			 * 时已经确定客户了，所以在添加订单界面和添加拜访记录界面中自动 填充客户，不需要选择；
			 */
			bundle = new Bundle();
			tModel = new CustomerSimple();
			tModel.setCustomer_id(data.getCustomer_id());
			tModel.setCustomer_Name(data.getCustomer_Name());
			bundle.putInt(AddOrderActivity.ACTION_TYPE,
					AddOrderActivity.ACTION_ADD);
			bundle.putSerializable(AddOrderActivity.CUS_DATA, tModel);
			UIHelper.getInstance().showOrderActivity(this, bundle);
			break;
		case R.id.visit_cus:
			/**
			 * 点击其他2个按钮分别进入添加订单 及添加拜访记录界面，因为此处在进入添加订单和添加拜访记录界面
			 * 时已经确定客户了，所以在添加订单界面和添加拜访记录界面中自动 填充客户，不需要选择；
			 */
			bundle = new Bundle();
			tModel = new CustomerSimple();
			tModel.setCustomer_id(data.getCustomer_id());
			tModel.setCustomer_Name(data.getCustomer_Name());
			bundle.putInt(ActivityVisitAdd.ACTION_TYPE,
					ActivityVisitAdd.ACTION_ADD);
			bundle.putSerializable(ActivityVisitAdd.CUS_DATA, tModel);
			UIHelper.getInstance().showVisitAddActivity(this, bundle);
			break;
		case R.id.edit:
			if (null == data) {
				return;
			}
			AdapterTerminate.doEditOrder(data);
			break;
		}
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		label.setText(data.getCustomer_Name() + "");
		contact_content.setText(data.getCustomer_LinkMan() + "");
		area.setText("区域:" + data.getCustomer_Area());
		sales_man.setText("业务员:" + data.getSaler_Name());
		fax_content.setText(data.getCustomer_FAX() + "");
		address.setText(data.getCustomer_Address() + "");
		tel_content.setText(data.getCustomer_Tel() + "");
		desc.setText("终端说明:\n  			 " + data.getCustomer_intr());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	};

	private final class GroupAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public OrderBean getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			convertView = getLayoutInflater().inflate(
					R.layout.xzd_list_item_v2, null);

			if (mDatas.size() == 1) {
				convertView
						.setBackgroundResource(R.drawable.ca_selector_listitem_single);

			} else if (mDatas.size() == 2) {
				switch (position) {
				case 0:
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);

					break;

				case 1:
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

					break;
				}
			} else if (mDatas.size() > 2) {

				if (0 == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);
				} else if ((mDatas.size() - 1) == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

				} else {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_content);
				}

			}

			final TextView textView = ((TextView) convertView
					.findViewById(R.id.item1));
			textView.setText(mDatas.get(position).getCustomerMedicineName());

			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2.setText(String.valueOf(mDatas.get(position)
					.getCustomerMedicineID()));

			return convertView;
		}

	}

	private final class OrderBean extends Base {
		private String customerMedicineName = null;
		private int customerMedicineID = 0;

		public String getCustomerMedicineName() {
			return customerMedicineName;
		}

		public void setCustomerMedicineName(String customerMedicineName) {
			this.customerMedicineName = customerMedicineName;
		}

		public int getCustomerMedicineID() {
			return customerMedicineID;
		}

		public void setCustomerMedicineID(int customerMedicineID) {
			this.customerMedicineID = customerMedicineID;
		}

		@Override
		public String toString() {
			return "OrderBean [customerMedicineName=" + customerMedicineName
					+ ", customerMedicineID=" + customerMedicineID + "]";
		}

		public OrderBean(String customerMedicineName, int customerMedicineID) {
			super();
			this.customerMedicineName = customerMedicineName;
			this.customerMedicineID = customerMedicineID;
		}

	}
}
