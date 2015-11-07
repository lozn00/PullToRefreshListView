# PullToRefreshListView
PullToRefreshListview 简单的listview Demo等待大神去完善里面的问题- - 
[我的博客](http://blog.51bwn.com "欢迎观赏")

![](https://github.com/51bwn/PullToRefreshListView/blob/master/assets/a.png)
#
![](https://github.com/51bwn/PullToRefreshListView/blob/master/assets/b.png)

##
实现的方法和xListview差不多,不过我这个是没多少bug的，可以多次设置stopRefuresh()不会出现奇葩问题哦!!
###支持设置时间

	public void setOnStateListener(OnStateListener listener) {
		this.mListener = listener;
	}


	public void stopRefresh() {
	
	}

	/**
	 * 设置下拉加载的时间 本来是手动调用了,为了让大家自定义因此提供公开的方法咯!
	 */
	public void setRefreshTime(String str) {
	}

	public void stopLoadMore() {

	}
	
		interface OnStateListener {
		void onRefresh();

		void onLoadMore();
	}
  
