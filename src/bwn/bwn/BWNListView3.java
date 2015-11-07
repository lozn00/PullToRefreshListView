package bwn.bwn;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 向下那个拉拽条为什么不居中呢
 * 
 * @author luozheng
 * 
 */
// 默认的刷新状态
/**
 * 我以为这可以实现只能点第一个条目才能实现刷新，结果我错了应该是第一个条目可见就可以
 * 
 * @author luozheng
 *
 */
public class BWNListView3 extends ListView implements OnScrollListener {
	enum State {// 这个枚举如果放到了本类外边那么初始值也应该放到一个地方 等待拖拽下拉可以刷新, 释放可刷新,正在刷新中

		WAIT_REFRESH, PULL_TO_REFRESH, RELREASE_REFRESH, REFRESHING
	}

	private OnStateListener mListener = null;
	private Enum mCurrentState = State.WAIT_REFRESH;// 这个我犯了低级错误应该放到类里面
	/**
	 * 下拉头是否还处于打开状态
	 */
	private boolean mDraging = false;
	/**
	 * 在下拉后 加载数据中 一直在拉 服务器响应了就切换为这个状态，那么弹起的时候会进行隐藏头
	 */
	private boolean mHaveData = false;
	private static final String TAG = "BWNListView";
	private View mHeadView;
	private int mFirstVisibleItem;
	private int mDownY;
	private int mHeadHeight;
	private TextView tvReflushState;
	private TextView mTvUpdateTime;
	private ImageView mIvRefresh;
	private ProgressBar mProgressBar;
	private View mFootView;
	private View mPbFoot;
	private View tvFoot;
	private int mFootHeight;
	private boolean mIsLoadding;
	/**
	 * 阻力因子
	 */
private int mDampFactor=2;

	public BWNListView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
		initHeader();
		initFooter();
	}

	private void initFooter() {// 不能用上下文
		mFootView = LayoutInflater.from(getContext()).inflate(R.layout.foot, null);
		this.addFooterView(mFootView);
		mPbFoot = mFootView.findViewById(R.id.pb_foot);
		tvFoot = mFootView.findViewById(R.id.tv_foot);
		mFootView.measure(0, 0);
		mFootHeight = mFootView.getMeasuredHeight();
		stopLoadMore();
	}

	private void initHeader() {
		mHeadView = LayoutInflater.from(getContext()).inflate(R.layout.head, null);
		mHeadView.measure(0, 0);// 这里面2个参数不知道是干嘛的
		mHeadHeight = mHeadView.getMeasuredHeight();// 获取测量的高度,因为并没有初始化
		addHeaderView(mHeadView);
		tvReflushState = (TextView) findViewById(R.id.tv_state);
		mTvUpdateTime = (TextView) findViewById(R.id.tv_updatetime);
		mIvRefresh = (ImageView) findViewById(R.id.iv_refresh);
		mProgressBar = (ProgressBar) findViewById(R.id.pb);

		stopRefresh();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "按下手势");
			mDraging = true;
			mDownY = (int) ev.getY();
			// Log.i(TAG,"鼠标按下");
			break;
		case MotionEvent.ACTION_MOVE:
			mDraging = true;
			int moveY = (int) ev.getY();
			
			int dY = moveY - mDownY;
			
			// int paddingTop = -mHeadView.getTop() + moveY;//
			// 刚开始是隐藏的，所以是从负数开始累加
			int paddingTop = -mHeadHeight + dY;// 刚开始是隐藏的，所以是从负数开始累加
			paddingTop=paddingTop/mDampFactor;
			// animationView(mHeadView, paddingTop);
			// Log.i(TAG,
			// "MOVEy"+moveY+",getY:"+ev.getY()+"DY:"+dY+",downY"+moveY);
			// mHeadView.setPadding(0, paddingTop, 0, 0);

			if (mFirstVisibleItem == 0 && dY > 0) {// 必须点击第一个条目且 S手势是往下啦
				if (paddingTop > 0) {
					if (mCurrentState == State.PULL_TO_REFRESH) {
						mCurrentState = State.RELREASE_REFRESH;// 第一次往下拉 释放刷新
						tvReflushState.setText("释放刷新");
						animation(mIvRefresh, false);
						Log.i(TAG, "释放刷新");
					} else if (mCurrentState == State.RELREASE_REFRESH)// 第二次往下拉
					{
						// mCurrentState = State.RELREASE_REFRESH;// 释放刷新
						// tvReflushState.setText("释放刷新");
						// animation(mIvRefresh, false);
						// Log.i(TAG, "释放刷新");
					} else if (mCurrentState == State.REFRESHING)// 在这里就可以解决刷新中还是可以拖拽的效果
					{

						// return false;//返回false不让你拉
						// return super.onTouchEvent(ev);// 那么这个复写的抬起事件也交给了..
						// return true;
					}

				} else if (paddingTop < 0 && mCurrentState == State.WAIT_REFRESH || mCurrentState==State.RELREASE_REFRESH) {
					tvReflushState.setText("下拉刷新");
					mCurrentState = State.PULL_TO_REFRESH;
					animation(mIvRefresh, true);
					Log.i(TAG, "下拉刷新");
				}

				mHeadView.setPadding(0, paddingTop, 0, 0);//拖拽的距离
				return true;
			}
		case MotionEvent.ACTION_UP:
//			Log.i(TAG, "抬起手势");
			mDraging = false;
			// mDownY=(int) ev.getY();
			if (mHaveData) {
				mHaveData = false;
				// 数据刷新完毕了，
				resetPullRefreshState();
			} else if (mCurrentState == State.PULL_TO_REFRESH) {
				mHeadView.setPadding(0, -mHeadHeight, 0, 0);// 弹回去
			} else if (mCurrentState == State.RELREASE_REFRESH) // 如果以及在拉的状态了
																// 那么应该切换为刷新中高度位置
			{

				Log.i(TAG, "刷新中状态");
				mHeadView.setPadding(0, 0, 0, 0);
				mCurrentState = State.REFRESHING;
				tvReflushState.setText("刷新中");
				mProgressBar.setVisibility(View.VISIBLE);
				mIvRefresh.clearAnimation();// 一个动画没消失那么无法不能设置为消失，所以要先清除这个动画才能让他消失
				mIvRefresh.setVisibility(View.INVISIBLE);
				if (mListener != null) {
					mListener.onRefresh();
				}
			} else if (mCurrentState == State.REFRESHING) // 如果处于刷新中 弹回去 只显示头的高度
			{

				mHeadView.setPadding(0, 0, 0, 0);
			}
			// Log.i(TAG,"鼠标 放开");
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void resetPullRefreshState() {
		mHeadView.setPadding(0, -mHeadHeight, 0, 0);// 弹回去
		mCurrentState = State.WAIT_REFRESH;// 再重新变为空闲状态也
		mProgressBar.setVisibility(View.INVISIBLE);
		mIvRefresh.setVisibility(View.VISIBLE);
		tvReflushState.setText("下拉刷新");
	
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 后面2个参数解决 某些sdk问题
		Log.i(TAG, "滚动状态改变了"+scrollState);
		//管他什么状态都需要拉
		if (this.getLastVisiblePosition() == getCount() - 1 && mIsLoadding == false){
//			if (this.getLastVisiblePosition() == getCount() - 1 && mIsLoadding == false && (scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_IDLE)) {
			mFootView.setPadding(0, 0, 0, 0);
			Log.i(TAG, "到底部了");
			mIsLoadding = true;
			mListener.onLoadMore();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.mFirstVisibleItem = firstVisibleItem;
	 Log.i(TAG,"当前 的可见"+firstVisibleItem);
		/*
		 * if(firstVisibleItem== 0){
		 * 
		 * Log.i(TAG,"第一个可见的队列"); }
		 */
	}

	/**
	 * 是否是下拉刷新状态
	 * 
	 * @param v
	 * @param isPullRefresh
	 */
	public void animation(View v, boolean isPullRefresh) {
		Log.i(TAG, "调用动画" + isPullRefresh);
		RotateAnimation animation = new RotateAnimation(isPullRefresh ? -180 : 0, isPullRefresh ? 0 : 180, RotateAnimation.RELATIVE_TO_SELF, 0.5F, RotateAnimation.RELATIVE_TO_SELF, 0.5F);
		animation.setDuration(200);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}

	/**
	 * 回弹动画
	 * 
	 * @param v
	 * @param isPullRefresh
	 */
	public void animationView(View v, long top) {
		// 开启移动动画
		TranslateAnimation ta = new TranslateAnimation(0, 0, top, v.getTop());
		ta.setInterpolator(new DecelerateInterpolator());
		ta.setDuration(300);
		v.startAnimation(ta);
		// 设置回到正常的布局位置
		// inner.layout(normal.left, normal.top, normal.right, normal.bottom);

	}

	interface OnStateListener {
		void onRefresh();

		void onLoadMore();
	}

	public void setOnStateListener(OnStateListener listener) {
		this.mListener = listener;
	}

	/**
	 * //修改下拉刷新文本，修改下拉的控件，以及修改枚举current,以及该隐藏的隐藏
	 * 
	 * @param topPadding
	 */
	public void stopRefresh() {
		tvReflushState.setText("刷新完毕");
		// animationView(mHeadView, -mHeadHeight);
		postDelayed(new Runnable() {

			@Override
			public void run() {
				// 如果你没在拉,那门我来关闭它
				if (!mDraging) {
					resetPullRefreshState();
				} else {
					// 如果listview还在移动我这里不应该处理了，如果切换为这个状态也不行,看来得搞一个中间变量了
					mHaveData = true;
					// mCurrentState=State.REFRESH_OVER;

				}

			}
		}, 500);

	}

	/**
	 * 设置下拉加载的时间 本来是手动调用了,为了让大家自定义因此提供公开的方法咯!
	 */
	public void setRefreshTime(String str) {
		mTvUpdateTime.setText(str);
	}

	public void stopLoadMore() {
		mIsLoadding = false;
		Log.i(TAG, "foot的高度为;" + mFootHeight);
		this.setSelection(this.getCount() );//应该不减去1把不然
		mFootView.setPadding(0, -mFootHeight, 0, 0);
		// 正那么是那个脚的上面有空白 负数应该是下面有空白
		// 结果错了，他是把上面list条目拉下来遮住它，如果拉一半那么又一半的list条目懒猪他了。
		// 设置底部的padding一样是有效果的，只要变成了负数那么等于自己的那负数部分消失
		// 如果是top填写为负数自身的一半那么自身的上面那一半不见了，buttom那就是下面那部分不见了
	}
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if (isTouchEvent == true && deltaY < 0 && mHeadView != null) { // 表示下拉了，
			// 改变headIv的高度
			// 发现该高度不能超过 图片的高度
			int tmpHeight = (int) (mHeadView.getLayoutParams().height + Math
					.abs(deltaY) / mDampFactor);
			if (tmpHeight <= mHeadHeight) {
				mHeadView.getLayoutParams().height = tmpHeight;
				mHeadView.requestLayout();
			}
		}else{
			maxOverScrollY=200;
		}

		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
//		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, 500, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
}
