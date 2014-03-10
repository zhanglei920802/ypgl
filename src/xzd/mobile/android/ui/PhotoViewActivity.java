package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.ui.adapter.PhotoViewAdapter;
import xzd.mobile.android.ui.wdiget.HackyViewPager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

/**
 * 组图 图片查看器 2013年6月3日15:00:29
 * 
 * 
 * useage：要使用该类.必须传递一个List<String>对象。
 * 
 * 
 * @author ZhangLei
 * 
 */
public class PhotoViewActivity extends BaseActivity {
	public static final String TO_PHOTOVIEWACTIVITY = "to_photoviewactivity";

	private ViewPager viewpager = null;
	private List<String> urls = null;// 图片链接数组
	private PhotoViewAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void getPreActivityData() {

		urls = getIntent().getStringArrayListExtra(TO_PHOTOVIEWACTIVITY);
		if (null == urls) {
			urls = new ArrayList<String>();
		}
	}

	@Override
	public void initView() {
		viewpager = new HackyViewPager(this);
		setContentView(viewpager);
		adapter = new PhotoViewAdapter(this, urls);
	}

	@Override
	public void initData() {
		viewpager.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewpager = null;
		urls = null;
		adapter = null;

	}

}
