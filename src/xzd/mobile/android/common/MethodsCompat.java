package xzd.mobile.android.common;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

/**
 * Android各版本的兼容方法
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-8-6
 */
public class MethodsCompat {

	private static MethodsCompat mCompat = null;

	public synchronized static MethodsCompat getInstance() {
		if (mCompat == null) {
			mCompat = new MethodsCompat();
		}
		return mCompat;
	}
	public static void releaseMem() {
		if (mCompat != null) {
			mCompat = null;
		}

	}
	@TargetApi(5)
	public void overridePendingTransition(Activity activity, int enter_anim,
			int exit_anim) {
		activity.overridePendingTransition(enter_anim, exit_anim);
	}

	@TargetApi(7)
	public Bitmap getThumbnail(ContentResolver cr, long origId, int kind,
			Options options) {
		return MediaStore.Images.Thumbnails.getThumbnail(cr, origId, kind,
				options);
	}

	@TargetApi(8)
	public File getExternalCacheDir(Context context) {

		return context.getExternalCacheDir();
	}

	@TargetApi(11)
	public void recreate(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.recreate();
		}
	}

	@TargetApi(11)
	public void setLayerType(View view, int layerType, Paint paint) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(layerType, paint);
		}
	}

	@TargetApi(14)
	public void setUiOptions(Window window, int uiOptions) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			window.setUiOptions(uiOptions);
		}
	}

}