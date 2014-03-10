package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Notice;
import xzd.mobile.android.model.StoChange;
import xzd.mobile.android.model.StoChangeList;
import xzd.mobile.android.ui.PullToRefreshListView.OnRefreshListener;
import xzd.mobile.android.ui.adapter.AdapterStoChange;
import xzd.mobile.android.ui.intf.ActivityItf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 库存列表
 * 
 * @author ZhangLei
 * 
 */
public class ActivityLibList extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener, OnScrollListener,
		OnRefreshListener {
	public static final String TAG = "ActivityLibList";
	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;

	public static final int HANDLER_QUERY_ERROR = 4;
	public static final int HANDLER_QUERY_SUCCESS = 5;

	public static final String LIBDATA = "LIBDATA";
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
	private List<StoChange> datas = null;
	private AdapterStoChange adpter = null;
	private LisHandler libHandler = null;
	private TextView detail_title = null;
	private PullToRefreshListView lv_lib = null;
	private TextView add = null;
	private TextView go_back = null;

	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;

	private int visit_id = -1;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			onDestroy();
			break;

		default:
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new NullPointerException();
		}

		visit_id = intent.getIntExtra(LIBDATA, -1);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("库存列表");
		lv_lib = (PullToRefreshListView) findViewById(R.id.lv_lib);

		listview_footer_more = getLayoutInflater().inflate(
				R.layout.listview_foot_more, null);
		listview_foot_progress = (ProgressBar) listview_footer_more
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) listview_footer_more
				.findViewById(R.id.listview_foot_more);

		lv_lib.addFooterView(listview_footer_more);
		lv_lib.setAdapter(adpter);
		lv_lib.setOnItemClickListener(this);
		lv_lib.setOnScrollListener(this);
		lv_lib.setOnRefreshListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);
		add.setVisibility(View.GONE);
		loading = (LinearLayout) findViewById(R.id.loading);
		text = (TextView) findViewById(R.id.text);
		text.setText("正在努力加载中");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		lv_lib.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		libHandler = new LisHandler();
		datas = new ArrayList<StoChange>();
		adpter = new AdapterStoChange(datas, this);
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
		setContentView(R.layout.activity_lib_list);

		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		getData();
	}

	private void getData() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Init Data page=" + 1);
		LoadData(1, libHandler, UIHelper.LISTVIEW_ACTION_INIT);
	}

	private void LoadData(final int pageIndex, final LisHandler handler,
			final int tag) {
		// TODO Auto-generated method stub
		/**
		 * 开启线程加载数据
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				boolean isRefresh = false;

				if (tag == UIHelper.LISTVIEW_ACTION_REFRESH
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				StoChangeList datas = null;
				try {
					Log.d(TAG, "Load DataIng please wait.....");
					datas = TerminateBiz.GetStoChanges(appcontext, pageIndex,
							AppContext.PAGE_SIZE, visit_id, isRefresh);
					if (datas == null) {
						throw new NullPointerException();
					}
					msg.what = HANDLER_SUCCESS;
					msg.obj = datas;
					msg.arg1 = datas.getDatas().size();
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	private final class LisHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_QUERY_SUCCESS:

			case HANDLER_SUCCESS:
				Notice notice = HandleData(msg.obj, msg.arg1, msg.arg2);

				if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
					lv_lib.setTag(UIHelper.LISTVIEW_DATA_FULL);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_full));
				} else if (msg.arg1 == AppContext.PAGE_SIZE) {
					lv_lib.setTag(UIHelper.LISTVIEW_DATA_MORE);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_more));
				}
				UpdateView();
				if (adpter.getCount() < 1) {
					lv_lib.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					listview_foot_more.setText(getString(R.string.load_empty));
					lv_lib.setVisibility(View.GONE);
					loading.setVisibility(View.VISIBLE);
					progressbar.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					text.setText("没有数据");
				}

				listview_foot_progress.setVisibility(View.GONE);

				if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv_lib.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv_lib.setSelection(0);
				}
				break;

			case HANDLER_ERROR:
				lv_lib.setTag(UIHelper.LISTVIEW_DATA_MORE);
				listview_foot_more.setText(R.string.loading_error);
				UIHelper.Toast(ActivityLibList.this,
						R.string.http_exception_error);
				break;

			}
		}

	}

	public Notice HandleData(final Object obj, final int size, final int type) {
		Notice notice = null;
		switch (type) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			// Notice notice = new Notice();
			int newData = 0;// 新数据
			StoChangeList newList = (StoChangeList) obj;
			datasum = size;
			if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

				if (datas.size() > 0) {

					for (StoChange medicineorder : newList.getDatas()) {
						boolean tag = false;

						for (StoChange simplepartimejobbean2 : datas) {
							if (medicineorder.getSto_ID() == simplepartimejobbean2
									.getSto_ID()) {
								tag = true;
								break;
							}
						}
						if (!tag)
							newData++;
					}
					// datas.addAll(0, newList.getMedicine_list());
				} else {

					newData = size;
				}

			}
			datas.clear();
			datas.addAll(newList.getDatas());
			if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

			}
			break;

		case UIHelper.LISTVIEW_ACTION_SCROLL:
			// Notice list = new Notice();

			StoChangeList mlist = (StoChangeList) obj;
			datasum += size;

			if (datas.size() > 0) {

				for (StoChange medicineorder : mlist.getDatas()) {
					boolean tag = false;

					for (StoChange simplepartimejobbean2 : datas) {
						if (medicineorder.getSto_ID() == simplepartimejobbean2
								.getSto_ID()) {
							tag = true;
							break;
						}
					}
					if (!tag)
						datas.add(medicineorder);
				}

			} else {

				datas.addAll(datas.size() - 1, mlist.getDatas());
			}

			break;
		}

		return notice;
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		loading.setVisibility(View.GONE);
		lv_lib.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		LoadData(1, libHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		lv_lib.onScrollStateChanged(view, scrollState);
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

		int lvDataState = stringutils.toInt(lv_lib.getTag());

		if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {

			lv_lib.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			listview_foot_more.setText(R.string.loading);
			listview_foot_progress.setVisibility(View.VISIBLE);
			// 当前pageIndex
			int pageIndex = 1 + (datasum / AppContext.PAGE_SIZE);// 计算pagesize
			Log.d(TAG, "load data scroll,page=" + pageIndex + ".....");

			LoadData(pageIndex, libHandler, UIHelper.LISTVIEW_ACTION_SCROLL);

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lv_lib.onScroll(view, firstVisibleItem, visibleItemCount,
				totalItemCount);
	}

}
