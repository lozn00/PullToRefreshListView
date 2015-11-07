package bwn.bwn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListResourceBundle;

import bwn.bwn.BWNListView.OnStateListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 本人能力有限搞了 半天搞不出腾讯qq那种流畅的回弹效果，不过还好啦,没有什么崩溃的bug
 * @author luozheng
 *
 */
public class MainActivity extends Activity{
	protected static final String TAG="MainActivity";
	protected static final int MSG_RESULT=0;
	private BWNListView mListView;
	private ArrayList<String> list;
	Handler mHandler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MSG_RESULT:
				adapter.notifyDataSetChanged();
				mListView.stopRefresh();
				
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView=(BWNListView) findViewById(R.id.lv_refresh);
		String[] arrs={"原始数据a,","原始数据bb","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c","原始c"};
		list=new ArrayList<String>();
			for(int i=0;i< arrs.length;i++){
				list.add(arrs[i]);
			}
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		mListView.setAdapter(adapter);
		mListView.setOnStateListener(new OnStateListener(){
			@Override
			public void onRefresh(){
				new Thread( new Runnable(){
					@Override
					public void run(){
						SystemClock.sleep(2000);
						Log.i(TAG,"刷新中");
						list.clear();
						//如果重新创建了一个集合赋值，那么notify是显然无效的
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x111");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						list.add("下拉加载的数据x");
						mHandler.sendEmptyMessage(MSG_RESULT);
			
					}
				}).start();
			}

			@Override
			public void onLoadMore(){
				
				MyTask task=new MyTask();
				task.execute("测试");
			}
		});
	}


	private ArrayAdapter<String> adapter;
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	class MyTask extends AsyncTask<String,Void,String>{
		/**
		 * 主线程  main,5main
		 */
			@Override
			protected void onPreExecute(){
				Log.i(TAG,"ononPreExecute"+Thread.currentThread());
				super.onPreExecute();
			}
			/**
			 * 子线程  AsyncTask #1 , 5main
			 */
			@Override
			protected String doInBackground(String...params){
				Log.i(TAG,"doInBackground"+Thread.currentThread());
				SystemClock.sleep(2000);
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("上啦刷新数据，世33界");
				list.add("你好，世333界");

				return "fffff";//这里返回的fff讲在onPostExecute的参数里面
			}
			/**
			 * 主线程
			 */
			@Override
			protected void onProgressUpdate(Void...values){
				Log.i(TAG,"onProgressUpdate"+Thread.currentThread());
				super.onProgressUpdate(values);
			}
			/**
			 * 主线程  处理结果  main
			 */
			@Override
			protected void onPostExecute(String result){
				super.onPostExecute(result);	
				Log.i(TAG,result+"onPostExecute"+Thread.currentThread().getName());
				adapter.notifyDataSetChanged();
				mListView.stopLoadMore();
				/**
				 * onPreExecute
				 * doInBackground
				 * onPostExecute  由于中间那个参数是空所以没有执行了
				 * 
				 */
			}
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return list==null?0:list.size();
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				TextView textView=new TextView(MainActivity.this);
				textView.setText("严重:"+list.get(position));
				;
			}
	
			return convertView;
		}
		
	}
}
