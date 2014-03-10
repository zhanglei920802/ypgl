package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.ConsumeBiz;
import xzd.mobile.android.business.CustomerConsumeDetail;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.model.CustomerConsume;
import xzd.mobile.android.ui.intf.ActivityItf;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConsDetail extends BaseActivity implements ActivityItf,
		OnClickListener {
	private String TAG = "ActivityOrderDetail";

	public static final int HANDLER_GET_DETAIL_SUCCESS = 1;
	public static final int HANDLER_GET_DETAIL_FAILED = 2;
	public static final int HANDLER_GET_DETAIL_EXCEPTION = -1;

	public static final String EXTRAS_DATA = "consume_data";

	private AppContext appcontext = null;
	private UIHelper helper = null;
	private OrderHelper orderHelper = null;

	public static final String EXTRAS_CUSTUMID = "custom";
	private int customID = -1;
	private int medicineID = -1;
	public static final String EXTRAS_MEDICINEID = "medicine";

	private ScrollView wrap_main = null;
	private CustomerConsumeDetail mConsumeDetail = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;

	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView confrim = null;

	// Content
	private ListView lv_detail_list = null;
	private CustomerConsume mConsume = null;
	private ConsHandler mConsHandler = null;
	private List<CustomerConsumeDetail> mDatas = null;
	private ConsAdapter mAdapter = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.go_back:
			finish();
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalArgumentException("参数传递为空");
		}
		mConsume = (CustomerConsume) getIntent().getSerializableExtra(
				EXTRAS_DATA);
		customID = getIntent().getIntExtra(EXTRAS_CUSTUMID, -1);
		medicineID = getIntent().getIntExtra(EXTRAS_MEDICINEID, -1);
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
		detail_title.setText("消费详情");
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		text.setText("正在努力加载中");
		wrap_main = (ScrollView) findViewById(R.id.wrap_main);
		wrap_main.setVisibility(View.GONE);

		{
			lv_detail_list = (ListView) findViewById(R.id.lv_detail_list);
			lv_detail_list.setAdapter(mAdapter);
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mConsHandler = new ConsHandler();
		mDatas = new ArrayList<CustomerConsumeDetail>();
		mAdapter = new ConsAdapter();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xzd_consume_detail);

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
		// GetVisitContent(mConsume.get/, true);
		if (null == mConsume) {
			return;
		}
		getDetailList(customID, medicineID, mConsume.getBegin_date(),
				mConsume.getEnd_date());
	}

	private void getDetailList(final int customerid, final int medicineid,
			final String begintime, final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<CustomerConsumeDetail> tmp = null;
				try {
					if (!appcontext.isNetworkConnected()) {

						mConsHandler.sendEmptyMessage(-1);
						return;
					}

					if (!AppContext.is_login()) {
						mConsHandler.sendEmptyMessage(-2);
						return;
					}

					tmp = ConsumeBiz.GetCustomerConsumeDetail(customerid,
							medicineid, begintime, endtime);

					if (null == confrim || tmp.isEmpty()) {
						mConsHandler.sendEmptyMessage(-3);
						return;
					}

					Message msg = mConsHandler.obtainMessage();
					msg.what = 1;
					msg.obj = tmp;
					mConsHandler.sendMessage(msg);

				} catch (Exception e) {

					e.printStackTrace();
					mConsHandler.sendEmptyMessage(-4);
				}

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private final class ConsHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:

				if (null != msg.obj) {
					if (mDatas == null || mDatas.isEmpty()) {
						return;
					}

					mDatas.clear();
					mDatas.addAll(mDatas);
					mAdapter.notifyDataSetChanged();
					loading.setVisibility(View.GONE);

					wrap_main.setVisibility(View.VISIBLE);
					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					anim.setDuration(1000);
					wrap_main.startAnimation(anim);
					anim = null; 
				}

				break;

			case -1:
			case -2:
			case -3:
			case -4:

				loading.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("网络连接不正常，请检查网络后重试！");

				break;

			}
		}

	}

	private final class ConsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
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
			// return null;
			ViewHolder viewHolder = null;
			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
						R.layout.xzd_cons_detail_item, null);
				viewHolder = new ViewHolder();
				viewHolder.cus_name = (TextView) convertView
						.findViewById(R.id.cus_name);
				// private TextView medicine_name = null;
				viewHolder.medicine_name = (TextView) convertView
						.findViewById(R.id.medicine_name);
				// private TextView cons_num = null;
				viewHolder.cons_num = (TextView) convertView
						.findViewById(R.id.cons_num);
				// private TextView cons_date =null;

				viewHolder.cons_date = (TextView) convertView
						.findViewById(R.id.cons_date);
				// private TextView cons_type =null;

				viewHolder.cons_type = (TextView) convertView
						.findViewById(R.id.cons_type);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			if (mDatas != null) {
				viewHolder.cons_date.setText(mDatas.get(position).getTime());
				viewHolder.cons_num.setText(mDatas.get(position).getNum());
				viewHolder.cons_type.setText(mDatas.get(position)
						.getConsumetype());
				viewHolder.cus_name.setText(mDatas.get(position)
						.getCustomername());
				viewHolder.medicine_name.setText(mDatas.get(position)
						.getMedicinename());

			}

			return convertView;
		}

	}

	private final class ViewHolder {
		TextView cus_name = null;
		TextView medicine_name = null;
		TextView cons_num = null;
		TextView cons_date = null;
		TextView cons_type = null;
	}
}
