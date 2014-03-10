package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import xzd.mobile.android.common.ImageUtils;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * 单张图片查看器
 * 
 * 要使用该类,必须传递一个String 对象
 * 
 * 否则将会出现NullPointerException
 * 
 * 2013年6月3日15:00:32
 * 
 * @author ZhangLei
 * 
 */
public class SinglePhotoViewActivity extends BaseActivity implements
		OnMatrixChangedListener, OnPhotoTapListener {
	public static final String TO_SINGLEPHOTOVIEWACTIVITY = "to_singlephotoviewactivity";

	private ImageView singlephotoview = null;
	private String url = null;
	private ImageUtils imageutils = null;
	private PhotoViewAttacher mAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlepgotoview);
		imageutils = ImageUtils.getInstance();

	}

	@Override
	public void getPreActivityData() {
		url = getIntent().getStringExtra(TO_SINGLEPHOTOVIEWACTIVITY);
		if (null == url) {
			try {
				throw new NullPointerException("the img url is null!!!");
			} catch (Exception e) {
				finish();
			}

		}
	}

	@Override
	public void initView() {
		singlephotoview = (ImageView) findViewById(R.id.singlephotoview);
		singlephotoview.setImageBitmap(imageutils.getBitmapByPath(url));

		mAttacher = new PhotoViewAttacher(singlephotoview);
		mAttacher.setOnMatrixChangeListener(this);
		mAttacher.setOnPhotoTapListener(this);
	}

	@Override
	public void initData() {

	}

	@Override
	public void onPhotoTap(View arg0, float x, float y) {

	}

	@Override
	public void onMatrixChanged(RectF arg0) {

	}

}
