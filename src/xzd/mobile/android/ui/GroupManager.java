package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.ui.intf.ActivityItf;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GroupManager extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {
	private final String TAG = "GroupManager";
	private final boolean DEBUG = true;
	private GroupAdapter mGroupAdapter = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView add = null;
	private List<CustGroup> mGroups = null;
	private ListView lv_user_manager = null;
	private GroupHandler mGroupHandler = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private AppContext maAppContext = null;
	public static final int HANDLER_WHAT_OBTAIN_SUCCESS = 3;
	public static final int HANDLER_WHAT_OBTAIN_FAILED = 4;
	public static final int HANDLER_WHAT_NO_LOGIN = 5;
	public static final int HANDLER_WHAT_NO_NETWORK = 6;
	public static final int HANDLER_WHAT_OBTAIN_EXCEPTION = 7;
	public static final int HANDLER_WHAT_NULL_DATA = 8;
	public static final String START_METHOD = "startmethod";
	public static final int METHOD_VIEW = 1;
	public static final int METHOD_EDIT = 2;

	private int mStartMethod = 0;
	private View addgroup;

	public GroupManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_user_manager:
			if (mGroups == null || mGroups.isEmpty()) {
				return;
			}

			if (position < 0 || position >= mGroups.size()) {
				if (DEBUG) {
					Log.d(TAG, "illegal position");
				}
				return;
			}

			if (DEBUG) {
				Log.d(TAG, "chooed group[" + mGroups.get(position) + "]");
			}
			if (mStartMethod == 0 || mStartMethod == METHOD_VIEW) {
				Intent intent = new Intent();
				intent.putExtra(TerminateManageActivity.GROUP_DATA,
						mGroups.get(position));
				setResult(Config.RESULT_CODE_CHOOSE_GROUP, intent);
				onDestroy();
				intent = null;
			} else if (mStartMethod == METHOD_EDIT) {
				Bundle pBundle = new Bundle();
				pBundle.putInt(AddGroup.START_METHOD, AddGroup.METHOD_EDIT);
				pBundle.putSerializable(AddGroup.EXTRAS_GROUPDATA,
						mGroups.get(position));
				AppContext.uihelper.ShowAddGroup(this, pBundle);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED);
			onDestroy();
			break;
		case R.id.addgroup:
			if (AppContext.user == null) {
				return;
			}
			if (!maAppContext.isNetworkConnected()) {
				return;
			}
			Bundle pBundle = new Bundle();
			pBundle.putInt(AddGroup.START_METHOD, AddGroup.METHOD_VIEW);
			AppContext.uihelper.ShowAddGroup(this, pBundle);

			break;
		default:
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		if (getIntent() == null) {
			throw new IllegalArgumentException("no parameter [startmethod]");
		}
		mStartMethod = getIntent().getIntExtra(START_METHOD, 0);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mGroupAdapter = new GroupAdapter();
		lv_user_manager = (ListView) findViewById(R.id.lv_user_manager);
		lv_user_manager.setAdapter(mGroupAdapter);
		lv_user_manager.setOnItemClickListener(this);
		// lv_user_manager.setOnItemClickListener(this);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		add = (TextView) findViewById(R.id.add);
		add.setVisibility(View.GONE);
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("分组管理");
		addgroup = findViewById(R.id.addgroup);
		addgroup.setOnClickListener(this);
		if (mStartMethod == METHOD_VIEW) {
			addgroup.setVisibility(View.GONE);
		}
		// LOADING
		{
			loading = (LinearLayout) findViewById(R.id.loading);
			loading.setVisibility(View.VISIBLE);
			progressbar = (ProgressBar) findViewById(R.id.progressbar);
			progressbar.setVisibility(View.VISIBLE);
			text = (TextView) findViewById(R.id.text);
			text.setVisibility(View.VISIBLE);
			text.setText("正在获取分组,请稍后...");
		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mGroupAdapter = new GroupAdapter();
		mGroupHandler = new GroupHandler();
		mGroups = new ArrayList<CustGroup>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xzd_user_manager);
		maAppContext = (AppContext) getApplication();
		getPreActivityData();
		initData();
		initView();

	}

	private void loadGroup() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<CustGroup> tmp = null;
					if (!AppContext.is_login()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_LOGIN);
						return;
					}

					if (!maAppContext.isNetworkConnected()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_NETWORK);
						return;
					}

					if (AppContext.user == null
							|| AppContext.user.getCounterMain_ID() == -1) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NULL_DATA);
						return;
					}
					tmp = TerminateBiz.GetCustGroupBySalerID(AppContext.user
							.getSale_id());

					if (null == tmp || tmp.isEmpty()) {
						mGroupHandler
								.sendEmptyMessage(HANDLER_WHAT_OBTAIN_FAILED);
						return;
					}

					Message msg = mGroupHandler.obtainMessage();
					msg.what = HANDLER_WHAT_OBTAIN_SUCCESS;
					msg.obj = tmp;
					mGroupHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					if (DEBUG) {
						e.printStackTrace();
					}

					mGroupHandler
							.sendEmptyMessage(HANDLER_WHAT_OBTAIN_EXCEPTION);
				}

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		onDestroy();
	}

	private final class GroupAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mGroups.size();
		}

		@Override
		public CustGroup getItem(int position) {
			// TODO Auto-generated method stub
			return mGroups.get(position);
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
					R.layout.xzd_list_item_v1, null);

			if (mGroups.size() == 1) {
				convertView
						.setBackgroundResource(R.drawable.ca_selector_listitem_single);

			} else if (mGroups.size() == 2) {
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
			} else if (mGroups.size() > 2) {

				if (0 == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);
				} else if ((mGroups.size() - 1) == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

				} else {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_content);
				}

			}

			final TextView textView = ((TextView) convertView
					.findViewById(R.id.item1));

			textView.setText(mGroups.get(position).getGroupName());
			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					null);
			return convertView;
		}

	}

	private final class GroupHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_WHAT_OBTAIN_SUCCESS:
				if (msg.obj != null) {
					if (mGroups != null) {

						mGroups.clear();
						mGroups.addAll((List<CustGroup>) msg.obj);
						mGroupAdapter.notifyDataSetChanged();

						if (mGroups.size() > 0) {
							lv_user_manager.setVisibility(View.VISIBLE);
							loading.setVisibility(View.GONE);

						} else {
							progressbar.setVisibility(View.GONE);

							loading.setVisibility(View.VISIBLE);
							progressbar.setVisibility(View.GONE);
							text.setVisibility(View.VISIBLE);
							text.setText("抱歉,没有可用数据,请点击刷新重试");
						}
					}
				}
				break;

			case HANDLER_WHAT_OBTAIN_FAILED:
			case HANDLER_WHAT_NO_LOGIN:
			case HANDLER_WHAT_NULL_DATA:
			case HANDLER_WHAT_NO_NETWORK:
			case HANDLER_WHAT_OBTAIN_EXCEPTION:
				loading.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				String msgCode = "0x" + String.format("%1$,02d", msg.what);

				text.setText("抱歉,没有可用数据,请点击刷新重试.code[" + msgCode + "]");

				break;
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadGroup();
	}

}
