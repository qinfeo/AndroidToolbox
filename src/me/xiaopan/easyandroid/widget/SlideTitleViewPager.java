package me.xiaopan.easyandroid.widget;

import java.util.List;

import me.xiaopan.easyandroid.util.AndroidLogger;
import me.xiaopan.easyandroid.util.Colors;
import me.xiaopan.easyandroid.util.ViewUtils;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 带有滑动标题的ViewPager，你只需调用setTabs
 */
public class SlideTitleViewPager extends LinearLayout implements OnPageChangeListener{
	private HorizontalScrollView titleHorizontalScrollView;
	private FrameLayout titleLayout;	//标题布局
	private LinearLayout titleItemLayout;	//标题项布局
	private LinearLayout sliderLayout;
	private View titleSliderView;	//标题滑块视图
	private ViewPager viewPager;
	private int center;	//中间位置
	private int width;	//宽度
	private View lastTitleItem;	//上一个标题项
	private View currentTitleItem;	//当前标题项
	private View nextTitleItem;	//下一个标题项
	private int leftDistance;
	private int rightDistance;
	private OnPageChangeListener onPageChangeListener;	//页面改变监听器
	private int sliderColor = Colors.SKYBLUE_DARK;	//滑块颜色
	private boolean draggingState;	//拖动状态
	private boolean settlingState;	//沉淀状态
	private boolean idleState;	//静止状态
	private int lastPosition;
	private boolean slideToLeft;	//向左滑动
	
	public SlideTitleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * 初始话
	 */
	private void init(){
		setOrientation(LinearLayout.VERTICAL);
		
		/*
		 * 初始化滑动标题栏
		 */
		//初始化标题布局，包括标题内容布局和滑块
		titleLayout = new FrameLayout(getContext());
		titleLayout.addView(titleItemLayout = onCreateTitleViewsLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));	//添加标题容器
		titleLayout.addView(sliderLayout = new LinearLayout(getContext()), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		sliderLayout.setOrientation(LinearLayout.VERTICAL);
		sliderLayout.setGravity(Gravity.BOTTOM);
		titleSliderView = onCreateSilderView();
		if(titleSliderView != null){
			sliderLayout.addView(titleSliderView);
		}
		
		//初始化标题横向滚动布局
		titleHorizontalScrollView = onCreateHorizontalScrollView();
		titleHorizontalScrollView.setHorizontalScrollBarEnabled(false);	//隐藏横向滑动提示条
		titleHorizontalScrollView.addView(titleLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));	//添加标题布局
		addView(titleHorizontalScrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));	//添加到LinearLayout中
		
		/* 初始化内容ViewPager */
		viewPager = onCreateViewPager();
		viewPager.setOnPageChangeListener(this);
		addView(viewPager, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
	}
	
	protected ViewPager onCreateViewPager(){
		return new ViewPager(getContext());
	}
	
	protected HorizontalScrollView onCreateHorizontalScrollView(){
		HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
		horizontalScrollView.setBackgroundColor(Colors.WHITE);
		return horizontalScrollView;
	}
	
	protected LinearLayout onCreateTitleViewsLayout(){
		return new LinearLayout(getContext());
	}
	
	protected View onCreateSilderView(){
		View sliderView = new View(getContext());
		sliderView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 10));
		sliderView.setBackgroundColor(sliderColor);
		return sliderView;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		AndroidLogger.d("onLayout");
		/* 如果当前的标题内容在横向上不能够将SlideTitlebar充满，那么就试图更改所有标题的宽度，来充满SlideTitlebar */
		width = r - l;
		center = (r + l) / 2;
		View titleView;
		
		/*
		 * 初始化所有标题视图的宽度
		 */
		if(ViewUtils.getMeasureWidth(titleItemLayout) < width){
			int averageWidth = width/titleItemLayout.getChildCount();	//初始化平均宽度
			
			/* 先计算出剩余可平分的宽度 */
			int currentTitleViewWidth;
			int number = 0;	//记录减去的个数，待会儿算平均值的时候要减去此数
			for(int w = 0; w < titleItemLayout.getChildCount(); w++){
				titleView = titleItemLayout.getChildAt(w);
				ViewUtils.measure(titleView);	//测量一下
				currentTitleViewWidth = titleView.getMeasuredWidth();
				if(currentTitleViewWidth > averageWidth){	//如果当前视图的宽度大于平均宽度，就从总宽度中减去当前视图的宽度
					width -= currentTitleViewWidth;
					number++;
				}
			}
			int newAverageWidth = width/(titleItemLayout.getChildCount() - number);	//重新计算宽度
			
			//重新设置宽度小于平均宽度的视图的宽度为新的平均宽度
			for(int w = 0; w < titleItemLayout.getChildCount(); w++){
				titleView = titleItemLayout.getChildAt(w);
				ViewGroup.LayoutParams layoutParams = titleView.getLayoutParams();
				if(titleView.getMeasuredWidth() < averageWidth){
					layoutParams.width = newAverageWidth;
				}else{
					layoutParams.width = titleView.getMeasuredWidth();
				}
				titleView.setLayoutParams(layoutParams);
			}
		}else{
			/* 重新设置宽度小于平均宽度的视图的宽度为新的平均宽度 */
			for(int w = 0; w < titleItemLayout.getChildCount(); w++){
				titleView = titleItemLayout.getChildAt(w);
				ViewGroup.LayoutParams layoutParams = titleView.getLayoutParams();
				layoutParams.width = titleView.getMeasuredWidth();
				titleView.setLayoutParams(layoutParams);
			}
		}
		
		//初始化滑块的宽度
		if(currentTitleItem != null){
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleSliderView.getLayoutParams();
			layoutParams.width = currentTitleItem.getLayoutParams().width;
			titleSliderView.setLayoutParams(layoutParams);
		}
		
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	public void onPageSelected(int position) {
		if(position > -1 && position < titleItemLayout.getChildCount()){
			if(currentTitleItem != null){
				currentTitleItem.setSelected(false);	//取消上一次的选中的标题视图的选中状态
			}
			
			/* 处理当前选中视图 */
			currentTitleItem = titleItemLayout.getChildAt(position);
			currentTitleItem.setSelected(true);	//将当前点击的TabView设为选中状态
			if(position > 0){//更新当前选中标题视图的上一个视图
				lastTitleItem = titleItemLayout.getChildAt(position - 1);
				leftDistance = currentTitleItem.getLeft() - lastTitleItem.getLeft();
			}else{
				lastTitleItem = null;
				leftDistance = 0;
			}
			if(position < titleItemLayout.getChildCount() - 1){//更新当前选中标题视图的下一个视图
				nextTitleItem = titleItemLayout.getChildAt(position + 1);
				rightDistance = nextTitleItem.getLeft() - currentTitleItem.getLeft();
			}else{
				nextTitleItem = null;
				rightDistance = 0;
			}
			
			//处理滑动
			titleHorizontalScrollView.smoothScrollTo((currentTitleItem.getLeft() + currentTitleItem.getRight())/2 - center, currentTitleItem.getTop());
		}
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		AndroidLogger.e("arg2："+arg2);
		slideToLeft = lastPosition < arg2;
		lastPosition = arg2;	
		if(!settlingState || arg2 != 0 ){
			if(slideToLeft){
				if(nextTitleItem != null){
					arg2 = width/(nextTitleItem.getLeft() - currentTitleItem.getLeft()) * arg2;
				}
				int newLeft = currentTitleItem.getLeft() + arg2;
				if(newLeft > width){
					newLeft = width;
				}
//				AndroidLogger.e("往左滑动："+newLeft);
				sliderLayout.scrollTo(newLeft * -1, sliderLayout.getScrollY());
			}else{
				if(lastTitleItem != null){
					arg2 = (width/(currentTitleItem.getLeft() - lastTitleItem.getLeft())*arg2);
				}
				int newLeft = currentTitleItem.getLeft() + arg2;
				if(newLeft < 0){
					newLeft = 0;
				}
//				AndroidLogger.e("往右滑动："+newLeft);
				sliderLayout.scrollTo(newLeft, sliderLayout.getScrollY());
			}
		}
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrolled(arg0, arg1, arg2);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		draggingState = arg0 == ViewPager.SCROLL_STATE_DRAGGING;//记录是否是拖动状态
		settlingState = arg0 == ViewPager.SCROLL_STATE_SETTLING;//记录是否是沉淀状态
		idleState = arg0 == ViewPager.SCROLL_STATE_IDLE;//记录是否是静止状态
		
		if(draggingState){}
		if(settlingState){}
		if(idleState){
			lastPosition = 0;
		}
		
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrollStateChanged(arg0);
		}
	}

	public void setTitles(List<View> titleViews) {
		titleItemLayout.removeAllViews();
		View titleView = null;
		for(int w = 0; w < titleViews.size(); w++){
			titleView = titleViews.get(w);
			titleView.setTag(w);
			final int tabViewPosition = w;
			titleView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					viewPager.setCurrentItem(tabViewPosition, true);	//更改ViewPager的选中项
				}
			});
			titleItemLayout.addView(titleView);
		}
		setCurrentItem(0);
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
	}
	
	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	public int getSliderColor() {
		return sliderColor;
	}

	public void setSliderColor(int sliderColor) {
		this.sliderColor = sliderColor;
	}
	
	public boolean setCurrentItem(int currentItemPosition){
		if(currentItemPosition > -1 && currentItemPosition < titleItemLayout.getChildCount()){
			viewPager.setCurrentItem(currentItemPosition, true);
			if(!titleSliderView.isShown()){
				onPageSelected(currentItemPosition);
			}
			return true;
		}else{
			return false;
		}
	}
}