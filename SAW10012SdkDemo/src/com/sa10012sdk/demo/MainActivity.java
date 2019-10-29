package com.sa10012sdk.demo;

import com.sa10012sdk.demo.fragment.ControlFragment;
import com.sa10012sdk.demo.fragment.DeviceFragment;
import com.sa10012sdk.demo.fragment.SettingFragment;
import com.sa10012sdk.demo.util.LogUtil;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.manager.CONNECTION_STATE;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends BaseActivity {
	
	private RadioGroup rgTab;
	private FragmentManager fragmentMgr;
	private Fragment deviceFragment, controlFragment, settingFragment;
	private ProgressDialog upgradeDialog;
	
	//缓存数据
	public static String deviceName, deviceId, version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initListener();
		initUI();
	}
	
	
	
	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		super.findView();
		rgTab = (RadioGroup) findViewById(R.id.rg_tab);
	}


	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		mHelper.addConnectionStateCallback(stateCallback);
		rgTab.setOnCheckedChangeListener(checkedChangeListener);
	}


	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		super.initUI();
		fragmentMgr = getFragmentManager();
		deviceFragment = new DeviceFragment();
		controlFragment = new ControlFragment();
		settingFragment = new SettingFragment();
		
		rgTab.check(R.id.rb_device);
//		rgTab.check(R.id.rb_control);
//		rgTab.check(R.id.rb_data);
		ivBack.setVisibility(View.GONE);
		
		upgradeDialog = new ProgressDialog(this);
		upgradeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
		upgradeDialog.setMax(100);
		upgradeDialog.setIcon(android.R.drawable.ic_dialog_info);
		upgradeDialog.setTitle(R.string.fireware_update);
		upgradeDialog.setMessage(getString(R.string.upgrading));
		upgradeDialog.setCancelable(false);
		upgradeDialog.setCanceledOnTouchOutside(false);
	}
	
	
	public void setTitle(int res){
		tvTitle.setText(res);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == ivBack){
			exit();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void exit(){
//		mHelper.release();
		clearCache();
//		Intent intent = new Intent(this, SplashActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
		finish();
	}
	
	
	private void clearCache(){
		deviceName = null;
		deviceId = null;
		version = null;
	}
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			LogUtil.log(TAG+" onStateChanged state:" + state);
			if(state == CONNECTION_STATE.DISCONNECT){
				
			}
		}
	};
	
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			FragmentTransaction trans = fragmentMgr.beginTransaction();
			if(checkedId == R.id.rb_device){
				trans.replace(R.id.content, deviceFragment);
			}else if(checkedId == R.id.rb_control){
				trans.replace(R.id.content, controlFragment);
			}else if(checkedId == R.id.rb_data){
				trans.replace(R.id.content, settingFragment);
			}
			trans.commit();
		}
	};
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHelper.removeConnectionStateCallback(stateCallback);
	}
	
	public void showUpgradeDialog(){
		upgradeDialog.show();
	}
	
	public void setUpgradeProgress(int progress){
		upgradeDialog.setProgress(progress);
	}
	
	public void hideUpgradeDialog(){
		upgradeDialog.dismiss();
	}
	
}








































