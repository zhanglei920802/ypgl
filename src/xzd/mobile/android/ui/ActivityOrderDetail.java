package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.model.MedicineOrderDetail;
import xzd.mobile.android.ui.intf.ActivityItf;
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

public class ActivityOrderDetail extends BaseActivity implements ActivityItf,
		OnClickListener {
	private String TAG = "ActivityOrderDetail";

	public static final int HANDLER_GET_DETAIL_SUCCESS = 1;
	public static final int HANDLER_GET_DETAIL_FAILED = 2;
	public static final int HANDLER_GET_DETAIL_EXCEPTION = -1;

	public static final String ORDER_DATA = "order_data";

	private AppContext appcontext = null;
	private UIHelper helper = null;
	private OrderHelper orderHelper = null;

	// Content view
	private TextView order_medcine_name = null;
	private TextView salesman_name = null;
	private TextView order_date = null;
	private TextView custom_name = null;
	private TextView order_address = null;
	private TextView order_count = null;
	private TextView order_detail_ramrk = null;
	private TextView status_title = null;
	private TextView province_check = null;
	private TextView city_check = null;
	private TextView process_date = null;
	private TextView order_detail_process_ramrk = null;
	private ListView lv_medicine_list = null;
	private List<MedicineBean> mDatas = null;
	private ScrollView wrap_main = null;
	private MedicineAdapter mAdapter = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;

	private FrameLayout frameLayout0 = null;
	private LinearLayout ly_detail_refresh = null;
	private LinearLayout ly_detail_loading = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView confrim = null;

	private MedicineOrderDetail data = null;
	private OrderDetailHandler orderdetailhandler = null;
	private TextView order_edit = null;
	private TextView order_review_true = null;
	private TextView order_review_false = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.ly_detail_refresh:
			if (!appcontext.isNetworkConnected()) {// 检测网络连接
				UIHelper.getInstance();
				UIHelper.Toast(appcontext,
						R.string.http_exception_error);
				return;
			}
			ly_detail_loading.setVisibility(View.VISIBLE);
			ly_detail_refresh.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			progressbar.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
			text.setText("正在努力加载中");
			wrap_main.setVisibility(View.GONE);
			GetOrderDetailContent(data.getOrder_id(), true);
			break;

		case R.id.go_back:
			onDestroy();
			break;
		case R.id.order_edit:
			if (null == data) {
				return;
			}
			MedicineOrderDetail medicineOrderDetail = new MedicineOrderDetail();
			medicineOrderDetail.setOrder_id(data.getOrder_id());
			bundle = new Bundle();
			bundle.putSerializable(AddOrderActivity.ORDER_DATA,
					medicineOrderDetail);
			bundle.putInt(AddOrderActivity.ACTION_TYPE,
					AddOrderActivity.ACTION_EDIT);
			UIHelper.getInstance().showOrderActivity(this, bundle);
			break;
		case R.id.order_review_true:
			if (!AppContext.is_login() || !appcontext.isNetworkConnected()) {
				return;
			}

			doReview(data.getOrder_id(), AppContext.user.getSaler_level(), 1,
					"无");
			break;
		case R.id.order_review_false:
			if (!AppContext.is_login() || !appcontext.isNetworkConnected()) {
				return;
			}
			doReview(data.getOrder_id(), AppContext.user.getSaler_level(), 2,
					"无");
			break;
		}
	}

	

	private void doReview(final int order_id, final int saler_level,
			final int i, final String string) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int code = 0;
					code = OrderHelper.SaveOrderReview(order_id, saler_level,
							i, string);
					if (code > 0) {
						orderdetailhandler.sendEmptyMessage(21);
					} else {
						orderdetailhandler.sendEmptyMessage(-21);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalArgumentException("参数传递为空");
		}
		data = new MedicineOrderDetail(
				(MedicineOrder) bundle.getSerializable(ORDER_DATA));

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
		detail_title.setText("订单详情");
		frameLayout0 = (FrameLayout) findViewById(R.id.frameLayout0);
		ly_detail_refresh = (LinearLayout) findViewById(R.id.ly_detail_refresh);
		ly_detail_loading = (LinearLayout) findViewById(R.id.ly_detail_loading);
		frameLayout0.setVisibility(View.VISIBLE);
		ly_detail_refresh.setVisibility(View.GONE);
		ly_detail_loading.setVisibility(View.VISIBLE);
		ly_detail_refresh.setOnClickListener(this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setVisibility(View.GONE);
		lv_medicine_list = (ListView) findViewById(R.id.lv_medicine_list);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		text.setText("正在努力加载中...");
		ly_detail_refresh.setVisibility(View.GONE);
		ly_detail_loading.setVisibility(View.VISIBLE);

		// 初始化内容
		order_medcine_name = (TextView) findViewById(R.id.order_medcine_name);
		salesman_name = (TextView) findViewById(R.id.salesman_name);
		order_date = (TextView) findViewById(R.id.order_date);
		custom_name = (TextView) findViewById(R.id.custom_name);
		order_address = (TextView) findViewById(R.id.order_address);
		province_check = (TextView) findViewById(R.id.province_check);
		city_check = (TextView) findViewById(R.id.city_check);
		order_count = (TextView) findViewById(R.id.order_total_price);
		order_detail_ramrk = (TextView) findViewById(R.id.order_detail_ramrk);
		status_title = (TextView) findViewById(R.id.status_title);

		process_date = (TextView) findViewById(R.id.process_date);
		order_detail_process_ramrk = (TextView) findViewById(R.id.order_detail_process_ramrk);

		wrap_main = (ScrollView) findViewById(R.id.wrap_main);
		wrap_main.setVisibility(View.GONE);
		lv_medicine_list.setAdapter(mAdapter);

		{
			order_edit = (TextView) findViewById(R.id.order_edit);
			order_review_true = (TextView) findViewById(R.id.order_review_true);
			order_review_false = (TextView) findViewById(R.id.order_review_false);
			if (AppContext.user.getCounterMain_ID() == 0) {
				order_edit.setVisibility(View.VISIBLE);
				order_review_true.setVisibility(View.GONE);
				order_review_false.setVisibility(View.GONE);
				order_edit.setOnClickListener(this);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 1) {
				order_edit.setVisibility(View.GONE);
				order_review_true.setVisibility(View.VISIBLE);
				order_review_false.setVisibility(View.VISIBLE);
				order_review_true.setOnClickListener(this);
				order_review_false.setOnClickListener(this);
			}

		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		orderdetailhandler = new OrderDetailHandler();
		mDatas = new ArrayList<ActivityOrderDetail.MedicineBean>();
		mAdapter = new MedicineAdapter();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!AppContext.is_login() || AppContext.user.getCounterMain_ID() == -1) {
			UIHelper.Toast(this, "初始化失败");
			onDestroy();
		}
		setContentView(R.layout.activity_order_detail);
		appcontext = (AppContext) getApplication();
		helper = UIHelper.getInstance();
		orderHelper = OrderHelper.getInstance();

		this.getPreActivityData();
		this.initData();
		this.initView();
		this.ObtainData();
	}

	private void ObtainData() {
		// TODO Auto-generated method stub
		GetOrderDetailContent(data.getOrder_id(), false);
	}

	private void GetOrderDetailContent(final int order_id,
			final boolean isReresh) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {

				MedicineOrderDetail rs;
				Message msg = orderdetailhandler.obtainMessage();
				try {
					rs = orderHelper.GetMedicineOrderDetail(order_id);
					if (rs == null) {
						throw new AppException();
					}
					msg.what = HANDLER_GET_DETAIL_SUCCESS;
					msg.obj = rs;

				} catch (Exception e) {
					Log.e(TAG, "getNotificationContent Runnable：获取数据失败");
					e.printStackTrace();
					msg.what = HANDLER_GET_DETAIL_FAILED;
					msg.obj = e;

				}
				orderdetailhandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		super.onBackPressed();
		onDestroy();
	}

	private final class OrderDetailHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case TerminateDetailActivity.HANDLER_GET_DETAIL_SUCCESS:

				if (null != msg.obj) {
					data.UpdateContent((MedicineOrderDetail) msg.obj);
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
			case 21:
				UIHelper.Toast(ActivityOrderDetail.this, "操作成功");
				onDestroy();
				break;
			case -21:
				UIHelper.Toast(ActivityOrderDetail.this, "操作失败");
				break;
			}
		}

	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		order_medcine_name.setText(data.getMedicine_Name() + " ");
		salesman_name.setText("业务员   " + data.getSaleMan_Name() + " ");
		order_date.setText(data.getOrder_Date() + " ");
		custom_name.setText(data.getCustomer_Name() + " ");
		order_address.setText("订单位置  " + data.getOrder_Location() + " ");
		order_count.setText("总金额:  " + data.getTotalPrice() + " ");
		if (StringUtils.getInstance().isEmpty(data.getOrder_Remark())) {
			order_detail_ramrk.setText("无");
		} else {
			order_detail_ramrk.setText(data.getOrder_Remark());
		}

		status_title.setText(data.getOrder_Status() + "");

		process_date.setText("处理  " + data.getResult_Date() + " ");
		if (data.getOrder_Status_id() == 3) {
			order_detail_process_ramrk.setText("处理意见    "
					+ data.getOrder_Result() + " ");
			process_date.setVisibility(View.INVISIBLE);
			order_detail_process_ramrk.setVisibility(View.VISIBLE);
		} else {
			order_detail_process_ramrk.setVisibility(View.GONE);
		}
		province_check.setText(data.getProvinceCheck() + "");
		city_check.setText(data.getCityCheck() + "");
		// 判定订单位置
		if (AppContext.user.getSale_id() != data.getSaleMan_id()) {
			order_address.setVisibility(View.VISIBLE);
		} else {
			order_address.setVisibility(View.GONE);
		}

		// /数据处理
		if (TextUtils.isEmpty(data.getMedicinesName())
				|| TextUtils.isEmpty(data.getMedicinesNUM())) {
			return;
		}
		String[] arrayName = data.getMedicinesName().split(",");
		String[] arrayNum = data.getMedicinesNUM().split(",");

		if (arrayName == null || arrayName.length == 0
				|| arrayName.length != arrayNum.length || arrayNum == null
				|| arrayNum.length == 0) {
			return;
		}

		for (int i = 0; i < arrayName.length; i++) {
			mDatas.add(new MedicineBean(arrayName[i], Integer
					.parseInt(arrayNum[i])));
		}
		if (mDatas != null) {
			LayoutParams mParams = lv_medicine_list.getLayoutParams();
			mParams.height = DeviceUtil.dip2px(this, 60 * mDatas.size());
			lv_medicine_list.setLayoutParams(mParams);
			lv_medicine_list.setVisibility(View.VISIBLE);
			mParams = null;
			mAdapter.notifyDataSetChanged();
		}

	}

	private final class MedicineAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public MedicineBean getItem(int position) {
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
			textView.setText(mDatas.get(position).getMedicineName());

			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2.setText(String.valueOf(mDatas.get(position)
					.getMedicineNum() + ""));

			return convertView;
		}

	}

	private final class MedicineBean {
		private String medicineName = "";
		private int medicineNum = 0;

		public MedicineBean(String medicineName, int medicineNum) {
			super();
			this.medicineName = medicineName;
			this.medicineNum = medicineNum;
		}

		public String getMedicineName() {
			return medicineName;
		}

		public int getMedicineNum() {
			return medicineNum;
		}

	}
}
