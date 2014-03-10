package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import xzd.mobile.android.business.CommonBiz;
import xzd.mobile.android.business.LinkManBiz;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.SalerSimple;
import xzd.mobile.android.ui.PullToRefreshListView.OnRefreshListener;
import xzd.mobile.android.ui.adapter.LinkManAdapter;
import xzd.mobile.android.ui.intf.ActivityItf;

public class LinkMan extends BaseActivity implements ActivityItf,
		OnItemClickListener, OnClickListener, OnScrollListener,
		OnRefreshListener {

	public static final String TAG = "TerminateManageActivity";

	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;
	public static final int HANDLER_GET_MAN_ID_SUCCESS = 2;
	public static final int HANDLER_GET_MAIN_ID_FAILED = 3;
	// Helper
	private UIHelper uihelper = null;
	private StringUtils stringutils = null;
	private AppContext appcontext = null;
	private TerminateBiz terminateBiz = null;
	// ListView
	private View listview_footer_more = null;// 加载更多View
	private ProgressBar listview_foot_progress = null;// 加载更多进度条
	private TextView listview_foot_more = null;// 加载更多文字

	private int datasum = 0;// 总的数据
	private List<xzd.mobile.android.model.LinkMan> datas = null;
	private LinkManAdapter adpter = null;
	private LinkManHandler mLinkManHandler = null;
	private TextView detail_title = null;
	private PullToRefreshListView lv_link_man = null;
	private TextView add = null;
	private TextView go_back = null;
	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;

	// group data
	public static final String GROUP_DATA = "groupdata";
	public static final String CONTERMAN_DATA = "countermandata";
	private SalerSimple chooedCounterMan = null;
	// search
	private EditText cusmgr_input1 = null;
	private Button cusmgr_search = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		if (!AppContext.is_login()) {
			onDestroy();
			return;
		}

		if (AppContext.user.getCounterMain_ID() == -1) {
			CommonBiz.isCounerMan(appcontext);
		}
		
		Log.d(TAG,"counter main id["+AppContext.user.getCounterMain_ID()+"]");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("联系人管理");
		lv_link_man = (PullToRefreshListView) findViewById(R.id.lv_link_man);

		listview_footer_more = getLayoutInflater().inflate(
				R.layout.listview_foot_more, null);
		listview_foot_progress = (ProgressBar) listview_footer_more
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) listview_footer_more
				.findViewById(R.id.listview_foot_more);

		lv_link_man.addFooterView(listview_footer_more);
		lv_link_man.setAdapter(adpter);
		lv_link_man.setOnItemClickListener(this);
		lv_link_man.setOnScrollListener(this);
		lv_link_man.setOnRefreshListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.loading);
		text = (TextView) findViewById(R.id.text);
		text.setText("正在努力加载中");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		lv_link_man.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);

		{
			cusmgr_input1 = (EditText) findViewById(R.id.cusmgr_input1);

			cusmgr_search = (Button) findViewById(R.id.cusmgr_search);
			if (AppContext.user.getCounterMain_ID() == 0) {

			} else if (AppContext.user.getCounterMain_ID() != -1
					&& AppContext.user.getCounterMain_ID() != 0) {
				cusmgr_input1.setOnClickListener(this);
				cusmgr_input1.setHint("业务员");
				// cusmgr_input1.setin
			}

			cusmgr_search.setOnClickListener(this);
		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mLinkManHandler = new LinkManHandler();
		datas = new ArrayList<xzd.mobile.android.model.LinkMan>();
		adpter = new LinkManAdapter(datas, this);
		if(AppContext.user.getCounterMain_ID()==-1){
			CommonBiz.isCounerMan(appcontext);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xzd_linkman);
		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		
		// ConfirmIsCounterMan();
	}

	private void getData() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Init Data page=" + 1);
		if (AppContext.user == null) {
			return;
		}

		if (AppContext.user.getCounterMain_ID() == -1) {
			return;
		}
		if (AppContext.user.getCounterMain_ID() == 0) {
			LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_INIT,
					AppContext.user.getM_id(), cusmgr_input1.getText()
							.toString(), 1, 0);
		} else {
			LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_INIT,
					AppContext.user.getM_id(), "", 0,
					chooedCounterMan.getSaler_id());
		}

	}

	private boolean isSearchMode(String name) {
		return !TextUtils.isEmpty(name);
	}

	private void LoadData(final int pageIndex, final Handler handler,
			final int tag, final int userID, final String name, final int type,
			final int salerid) {
		// TODO Auto-generated method stub
		/**
		 * 开启线程加载数据
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				if (tag == UIHelper.LISTVIEW_ACTION_REFRESH
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL
						|| tag == UIHelper.LISTVIEW_ACTION_INIT) {
				}
				List<xzd.mobile.android.model.LinkMan> datas = null;
				try {
					Log.d(TAG, "Load DataIng please wait.....");
					if (isSearchMode(name)) {
						datas = LinkManBiz.QueryLinkMan(pageIndex,
								AppContext.PAGE_SIZE, userID, type, salerid,
								name);
					} else {
						datas = LinkManBiz.GetLinkMan(pageIndex,
								AppContext.PAGE_SIZE, userID);
					}

					if (datas == null) {
						throw new AppException();
					}

					msg.what = HANDLER_SUCCESS;
					msg.obj = datas;
					msg.arg1 = datas.size();
					Log.d(TAG, "Load Data successfully......");
				} catch (Exception e) {
					Log.d(TAG, "Load Data Faild......");
					e.printStackTrace();
					msg.what = HANDLER_ERROR;
					msg.obj = e;
				}

				msg.arg2 = tag;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_link_man:

			if (position == 0 || listview_footer_more == view) {
				return;
			}

			Bundle bundle = new Bundle();
			// 跳转到详细界面
			bundle.putSerializable(MgrLinkMan.EXTRAS_LINKMAN,
					datas.get(position - 1));
			bundle.putInt(MgrLinkMan.START_METHOD,
					MgrLinkMan.METHOD_VIEW);
			uihelper.showLinkManDetailActivity(this, bundle);
			bundle = null;
			break;

		default:
			break;
		}
	}

	private final class LinkManHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			List<xzd.mobile.android.model.LinkMan> t_datas = (List<xzd.mobile.android.model.LinkMan>) msg.obj;
			switch (msg.arg2) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
				datasum = msg.arg2;
				if (datas != null && t_datas != null && !t_datas.isEmpty()) {
					datas.clear();
					datas.addAll(t_datas);
				}

			case UIHelper.LISTVIEW_ACTION_SCROLL:
				// Notice list = new Notice();

				datasum += msg.arg2;
				if (datas.size() > 0) {

					for (xzd.mobile.android.model.LinkMan medicineorder : datas) {
						boolean tag = false;

						for (xzd.mobile.android.model.LinkMan simplepartimejobbean2 : t_datas) {
							if (medicineorder.getID() == simplepartimejobbean2
									.getID()) {
								tag = true;
								break;
							}
						}
						if (!tag)
							datas.add(medicineorder);
					}

				} else {

					datas.addAll(datas.size() - 1,t_datas);
				}
			default:
				break;
			}
			switch (msg.what) {
			case HANDLER_SUCCESS:

				if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
					lv_link_man.setTag(UIHelper.LISTVIEW_DATA_FULL);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_full));
				} else if (msg.arg1 == AppContext.PAGE_SIZE) {
					lv_link_man.setTag(UIHelper.LISTVIEW_DATA_MORE);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_more));
				}
				UpdateView();
				if (adpter.getCount() < 1) {
					lv_link_man.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					listview_foot_more.setText(getString(R.string.load_empty));
					lv_link_man.setVisibility(View.GONE);
					loading.setVisibility(View.VISIBLE);
					progressbar.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					text.setText("没有数据");
				}

				listview_foot_progress.setVisibility(View.GONE);

				if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv_link_man
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					lv_link_man.setSelection(0);
				}
				break;

			case HANDLER_ERROR:
				lv_link_man.setTag(UIHelper.LISTVIEW_DATA_MORE);
				listview_foot_more.setText(R.string.loading_error);
				UIHelper.Toast(LinkMan.this, R.string.http_exception_error);
				break;
			case HANDLER_GET_MAIN_ID_FAILED:
				break;
			case HANDLER_GET_MAN_ID_SUCCESS:

				break;
			}
		}

	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		loading.setVisibility(View.GONE);
		lv_link_man.setVisibility(View.VISIBLE);
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Load data reresh:page=1");

		if (AppContext.user.getCounterMain_ID() == 0) {
			LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_REFRESH,
					AppContext.user.getM_id(), cusmgr_input1.getText()
							.toString(), 1, 0);
		} else {
			LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_REFRESH,
					AppContext.user.getM_id(), "", 0,
					chooedCounterMan.getSaler_id());
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		lv_link_man.onScrollStateChanged(view, scrollState);
		if (datas.isEmpty()) {
			return;
		}

		// 判断是否滚动到底部
		boolean scrollEnd = false;

		try {
			if (view.getPositionForView(listview_footer_more) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		int lvDataState = stringutils.toInt(lv_link_man.getTag());

		if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {

			lv_link_man.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			listview_foot_more.setText(R.string.loading);
			listview_foot_progress.setVisibility(View.VISIBLE);
			// 当前pageIndex
			int pageIndex = 1 + (datasum / AppContext.PAGE_SIZE);// 计算pagesize
			Log.d(TAG, "load data scroll,page=" + pageIndex + ".....");
			if (AppContext.user.getCounterMain_ID() == 0) {
				LoadData(pageIndex, mLinkManHandler,
						UIHelper.LISTVIEW_ACTION_SCROLL,
						AppContext.user.getM_id(), cusmgr_input1.getText()
								.toString(), 1, 0);
			} else {
				LoadData(pageIndex, mLinkManHandler,
						UIHelper.LISTVIEW_ACTION_SCROLL,
						AppContext.user.getM_id(), "", 0,
						chooedCounterMan.getSaler_id());
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		try {
			lv_link_man.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.add:
			if (AppContext.is_login()) {
				// uihelper.showOrderActivity(this);
				Bundle bundle = new Bundle();
				bundle.putInt(MgrLinkMan.START_METHOD,
						MgrLinkMan.METHOD_ADD);
				uihelper.showLinkManDetailActivity(this, bundle);

			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}

			break;
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.cusmgr_input2:
			if (AppContext.user.getCounterMain_ID() < 0) {
				return;
			}

			if (AppContext.user.getCounterMain_ID() == 0) {
				intent = new Intent(this, GroupManager.class);
				intent.putExtra(GroupManager.START_METHOD,
						GroupManager.METHOD_VIEW);
				startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_GROUP);
			} else {

			}

			intent = null;
			break;
		case R.id.cusmgr_search:
			if (!AppContext.is_login()) {
				UIHelper.Toast(this, "对不起,您还未登陆");
				return;
			}
			AppContext.user.getM_id();
			if (!appcontext.isNetworkConnected()) {
				UIHelper.Toast(this, "对不起,暂无网络连接");
				return;
			}
			
			if(AppContext.user.getCounterMain_ID()==1){
				if (chooedCounterMan == null
						|| TextUtils.isEmpty(chooedCounterMan.getSaler_name())) {
					UIHelper.Toast(this, "联系人不能为空");
					return;
				}
			}else if(AppContext.user.getCounterMain_ID()==0){
				if (  TextUtils.isEmpty(cusmgr_input1.getText())) {
					UIHelper.Toast(this, "联系人不能为空");
					return;
				}
			}
			

			if (AppContext.user.getCounterMain_ID() == 0) {
				LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_REFRESH,
						AppContext.user.getM_id(), cusmgr_input1.getText()
								.toString(), 1, 0);
			} else if(AppContext.user.getCounterMain_ID()==1) {
				LoadData(1, mLinkManHandler, UIHelper.LISTVIEW_ACTION_REFRESH,
						AppContext.user.getM_id(), "", 0,
						chooedCounterMan.getSaler_id());
			}
			break;
		case R.id.cusmgr_input1:
			if (AppContext.user == null) {
				return;
			}

			if (AppContext.user.getCounterMain_ID() < 1) {
				UIHelper.Toast(this, "无权限");
				return;
			}

			intent = new Intent(this, ActivityCounterManChoose.class);
			intent.putExtra(ActivityCounterManChoose.COUNTERMANDATA,
					AppContext.user.getM_id());
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CONTERMAN);
			intent = null;

			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getData();
		super.onResume();
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {

		case RESULT_CANCELED:

			break;
		case Config.RESULT_CODE_CHOOSE_COUNTMAN:
			if (null == data) {
				return;
			}
			chooedCounterMan = (SalerSimple) data
					.getSerializableExtra(OrderManageActivity.COUNTER_MAN_DATA);
			if (null != chooedCounterMan && AppContext.user != null
					&& AppContext.user.getCounterMain_ID() > 0) {

				cusmgr_input1.setText(chooedCounterMan.getSaler_name());
			}

			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	};

}
