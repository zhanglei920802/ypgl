package xzd.mobile.android.ui;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyLocationMapView extends MapView {
	public static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView(Context context) {
		super(context);

	}

	public MyLocationMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}
