package com.example.testlockscreen;

import com.example.lock.GestureLockView;
import com.example.lock.GestureLockView.OnGestureFinishListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GestureLockAty extends Activity implements OnClickListener {
	// 底部
	private RelativeLayout rel_botton;
	// 提示信息
	private TextView tv_message;
	// 绘制手势的控件
	private GestureLockView gv;
	// 绘制错误时候的动画
	private Animation animation;

	private String drawKey;// 绘制图形的key

	private boolean isSetting;

	private SharedPreferences preferences = null;
	private Editor editor;
	private Button btn_onDraw, btn_finishDraw;
	//忘记密码
	private TextView tv_forget_password;
	
	private int index = 0;// 标记当前绘制了几次
	
	private Context context;
	//当前已经绘制的手势 提示
	private GridView gv_lock;
	
	private LockAdapter lockAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_gesturelock);
		context = GestureLockAty.this;
		setTitle("设置图形锁");
		//true表示需要重新设置图形锁  false表示解锁
		isSetting = getIntent().getBooleanExtra("isSetting", false);
		
		preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
		
		drawKey = preferences.getString("drawKey", "");
		editor = preferences.edit();
		InitView();
		SetOnClickListener();
	}

	private void InitView() {
		rel_botton = (RelativeLayout) findViewById(R.id.rel_botton);
		rel_botton.setVisibility(View.GONE);
		tv_message = (TextView) findViewById(R.id.tv_message);
		gv = (GestureLockView) findViewById(R.id.gv);
		btn_finishDraw = (Button) findViewById(R.id.btn_finishDraw);
		btn_onDraw = (Button) findViewById(R.id.btn_onDraw);
		
		gv_lock = (GridView) findViewById(R.id.gv_lock);
		lockAdapter = new LockAdapter();
		gv_lock.setAdapter(lockAdapter);
		tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
		if (drawKey == null && "".equals(drawKey)) {
			gv.setResult(true);
		}
		//如果是从设置揭界面进来的隐藏掉忘记密码
		if (isSetting) {
			gv.setKey("");
			tv_forget_password.setVisibility(View.GONE);
			gv_lock.setVisibility(View.VISIBLE);
		} else {
			gv.setKey(drawKey);
			tv_forget_password.setVisibility(View.VISIBLE);
			gv_lock.setVisibility(View.GONE);
		}
		animation = new TranslateAnimation(-10, 10, 0, 0);
		animation.setDuration(50);
		animation.setRepeatCount(10);
		animation.setRepeatMode(Animation.REVERSE);
	}

	private void SetOnClickListener() {
		btn_onDraw.setOnClickListener(this);
		btn_finishDraw.setOnClickListener(this);
		tv_forget_password.setOnClickListener(this);
		//绘制手势的监听
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			//success绘制状态 key绘制的密码
			@Override
			public void OnGestureFinish(boolean success, String key) {
				Log.i("Log", success+"    "+key);
				if (!success) {
					gv.setDrawSucceed(false);
					if (isSetting) {
						if (index == 1) {
							tv_message.setText("与上次绘制的不一样！");
							index =1;
						} else if (index == 1){
							tv_message.setText("请您重新绘制！");
							gv.setKey("");
							index = 0;
						}
					} else {
						tv_message.setText("密码错误！");
					}
					tv_message.startAnimation(animation);
					gv.setKey(drawKey);
				} else {
					if (isSetting) {
						if (index == 0) {
							tv_message.setText("请再次绘制！");
							index =1;
							lockAdapter.setKey(key);
						} else if(index == 1){
							tv_message.setText("绘制成功！");
							rel_botton.setVisibility(View.VISIBLE);
							gv.setDrawSucceed(true);
						}
					} else {
						tv_message.setText("密码正确");
						finish();
					}
					drawKey = key;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//重绘时把所有数据还原
		case R.id.btn_onDraw:
			rel_botton.setVisibility(View.GONE);
			drawKey = "";
			gv.setKey(drawKey);
			tv_message.setText("请重新绘制图形锁！");
			index = 0;
			lockAdapter.setKey(drawKey);
			gv_lock.setVisibility(View.VISIBLE);
			gv.setResult(false);
			gv.setDrawSucceed(false);
			gv.clearDraw();
			break;

		case R.id.btn_finishDraw:
			editor.putString("drawKey", drawKey);
			editor.commit();
			finish();
			break;
		case R.id.tv_forget_password:
			gv.setKey("");
			isSetting = true;
			tv_forget_password.setVisibility(View.GONE);
			gv_lock.setVisibility(View.VISIBLE);
			tv_message.setText("请绘制图形锁！");
			break;
		}
	}
	
	class LockAdapter extends BaseAdapter{
		
		private char keys[];
		public void setKey(String key){
			if (key != null) {
				this.keys = key.toCharArray();
				this.notifyDataSetChanged();
			}
		}
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 9;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(R.drawable.eqe);
			if (keys != null) {
				for (int i = 0; i < keys.length; i++) {
					if ((keys[i]-48)==position) {
						imageView.setImageResource(R.drawable.eqd);
						continue;
					}
				}
				
			}
			return imageView;
		}
		
	}
	
}
