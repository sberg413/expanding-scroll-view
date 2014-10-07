package com.stevebergamini.expandingscrolltest;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class AdjustingScrollView extends ScrollView {



	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.d(LOGTAG, "onInterceptTouchEvent = " + ev.toString() );
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void fling(int velocityY) {
		Log.d(LOGTAG, "Fling velocityY = " + velocityY );
		isFling = true;
		flingVelocity = velocityY;
		super.fling(velocityY);
	}

	private static final String LOGTAG = "AdjustingScrollView";
	
	private boolean isFling = false;
	private int flingVelocity = 0;
	
	private int childCount;
	private ViewGroup viewGroup;
	
	private Integer actionBarOffset = null;
	
	private boolean paddingWasAdded = false;
	
	private static final int MIN_HEIGHT_DP_VAL = 100;
	private float minViewHeight = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, MIN_HEIGHT_DP_VAL, 
					getResources().getDisplayMetrics());
	
	private static final int MAX_HEIGHT_DP_VAL = 260;
	private float maxViewHeight = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, MAX_HEIGHT_DP_VAL, 
					getResources().getDisplayMetrics());
	
	private OnScrollViewListener mOnScrollViewListener; 
	
	public interface OnScrollViewListener {
	    void onScrollChanged( AdjustingScrollView v, int l, int t, int oldl, int oldt );
	}
	
	public void setOnScrollViewListener(OnScrollViewListener l) {
	    this.mOnScrollViewListener = l;
	}
		
	public AdjustingScrollView(Context context) {
		super(context);
	}
	
	public AdjustingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AdjustingScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void onFinishInflate(){
		
		super.onFinishInflate();
		
		getChildViewCount();
		// maxViewHeight = viewGroup.getChildAt(0).getHeight();
		Log.d(LOGTAG, "maxViewHeight = " + maxViewHeight);
		
		int[] location = new int[2];
	    this.getLocationInWindow(location);
	    actionBarOffset = location[1];
	   	 		
	    Log.d(LOGTAG, "actionBarOffset = " + actionBarOffset);
	    
	}
	
	public void addContainerPadding() {
		
//		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//		Display display = wm.getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		int width = size.x;
//		int height = size.y;
		
		int svHeight = this.getHeight();
		Log.d(LOGTAG,"ScrollView height = " + svHeight );
		
		// Enough padding is needed to allow for the last panel to
		// be able to be scrolled to the top of the view port.  Additionally,
		// more padding equal to the minViewHeight is add to allow for the
		// snapp scrolling to work and to give it a better feel.
		int bottomPadding = (int) (svHeight - maxViewHeight + minViewHeight);
		
		
		viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop(), viewGroup.getPaddingRight(),bottomPadding );
		
	}
	
	private void getChildViewCount(){
		viewGroup = (LinearLayout) this.getChildAt(0); // Get the LinearLayout inside the ScrollView;
		childCount = viewGroup.getChildCount();
		
		Log.d(LOGTAG, "child count = " + childCount);
	}

	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        
        // Log.d(LOGTAG, "Scroll changed y = " + y );
        
        if (!paddingWasAdded){
        	addContainerPadding();
        	paddingWasAdded = true;
        }
        
        resizeViews (); 
        checkScrollAndSnapView(y);
       
        // mOnScrollViewListener.onScrollChanged( this, x, y, oldx, oldy );
        
        
    }
	
	private boolean isScrolling = false;
	
	private void onFling(){
		
		Log.d(LOGTAG, "in onFling();" );
		
		int y = this.getScrollY();
		float f = y / maxViewHeight;
		int index = (int) Math.floor(f);
		
		index = flingVelocity > 0  ? index++ : index--;
		
		int scrollPosition = (int) (index * maxViewHeight);
							
		this.smoothScrollTo(0, scrollPosition );
		isFling = false;
		
		
	}
	
	private void onEndScroll(){
		
//		if (isFling)
//			return;
		
		int y = this.getScrollY();
		
		Log.d(LOGTAG, "scrollY = " + y + " svHeight = " + this.getHeight() );
		
		float f = y / maxViewHeight;
		int index = (int) Math.floor(f);
		float remainder = f - index;
		
		Log.d(LOGTAG, "index = " + index + " and remainder = " + remainder);
		
		if (remainder > 0.5){
			index++;
			
		} 
		int scrollPosition = (int) (index * maxViewHeight);
		
		Log.d(LOGTAG, "scrollPosition = " + scrollPosition);
			
		this.smoothScrollTo(0, scrollPosition );
		
	}
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//	    switch (event.getAction()) {
//	        case MotionEvent.ACTION_DOWN:
//	            currentlyTouching = true;
//	    }
//	    return super.onInterceptTouchEvent(event);
//	}
//	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//	    switch (event.getAction()) {
//	        case MotionEvent.ACTION_UP:
//	        case MotionEvent.ACTION_CANCEL:
//	            currentlyTouching = false;
//	            if (!currentlyScrolling) {
//	                //I handle the release from a drag here
//	            	Log.d(LOGTAG, "Drag released!!!");
//	                return true;
//	            }
//	    }
//	    return false;
//	}
	
	
	private void checkScrollAndSnapView(int y){
		
		
		
	}
	
	
	private void resizeViews () {
		
		int[] location = new int[2];
	    this.getLocationInWindow(location);
	    actionBarOffset = location[1];
	 		
	    // Log.d(LOGTAG, "actionBarOffset = " + actionBarOffset);	
		
	  
		for (int i = 0; i < childCount; i++) {
			View contentView = viewGroup.getChildAt(i);
			
			float newHeight = (int) maxViewHeight;

			location = new int[2];
			contentView.getLocationInWindow(location); 
			int viewY = location[1] - actionBarOffset;
			
			// Log.d(LOGTAG, "View " + i + " viewY = " + viewY);
			
			if ( viewY  <= 0 || viewY  > maxViewHeight)
				continue;
						
//			Log.d(LOGTAG, "view y location = " + viewY);
			
	    	newHeight = minViewHeight + ((maxViewHeight - minViewHeight) * ( 1 - (viewY / maxViewHeight)));
			 
	    	if (newHeight < minViewHeight)
	    		newHeight = minViewHeight; 
			
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
			params.height = (int) newHeight;
			contentView.setLayoutParams(params);

		}
	}
	
	Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
//			   if (isFling)
//				   AdjustingScrollView.this.onFling();
//			   else
				   AdjustingScrollView.this.onEndScroll();
		   }
	};
	
	final Handler handler = new Handler();
	


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Log.d(LOGTAG,"Touch Event = " + ev.toString() );
		
		switch (ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			isScrolling = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			isScrolling = false;
			handler.post(runnable);
			break;
		}
		
		return super.onTouchEvent(ev);
	}
	
	

}
