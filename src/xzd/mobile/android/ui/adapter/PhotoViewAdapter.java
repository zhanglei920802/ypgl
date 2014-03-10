package xzd.mobile.android.ui.adapter;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import xzd.mobile.android.common.ImageUtils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * 组图图片适配器 2013年6月3日15:21:00
 * 
 * @author ZhangLei
 * 
 */
public class PhotoViewAdapter extends PagerAdapter {
	private List<String> _urls = null;
	private Context _context = null;
	private ImageUtils imageutils = null;

	public PhotoViewAdapter(Context context, List<String> urls) {
		this._urls = urls;
		this._context = context;

		imageutils = ImageUtils.getInstance();
	}

	@Override
	public int getCount() {
		return _urls.size();

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public PhotoView instantiateItem(ViewGroup container, int position) {

		PhotoView photoView = new PhotoView(_context);
		photoView
				.setImageBitmap(imageutils.getBitmapByPath(_urls.get(position)));
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return photoView;
	}
}
