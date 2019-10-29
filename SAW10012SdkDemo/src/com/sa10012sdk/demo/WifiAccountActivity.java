package com.sa10012sdk.demo;

import com.sleepace.sdk.core.nox.NoxSAWManager;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.DeviceManager;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.sa10012.NetUtils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WifiAccountActivity extends BaseActivity {

	public final static String EXTRA_SSID = "extra_ssid";
	public final static String EXTRA_PSW = "extra_psw";
	private TextView mTvCurWifi;
	private EditText mEtWifiPwd;
	private Button mBtnSubmit;
	private BleDevice mBleDevice;
	private NoxSAWManager mManager;
	int mResultCode = RESULT_CANCELED;
	private boolean mShowConnectedStatus;// 是否需要弹连接失败等状态，主动操作才需要

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_wifi_account);
		findView();
		initData();
		initListener();
		initUI();
	}

	public void initUI() {

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWifiInfo();
		mHelper.addConnectionStateCallback(stateCallback);
	}

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager,
				final CONNECTION_STATE state) {
			// TODO Auto-generated method stub

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (state == CONNECTION_STATE.CONNECTED) {
						Log.e(TAG, "设备连接成功");
						hideLoading();
						finish();
					}else{
						hideLoading();
						//Toast.makeText(WifiAccountActivity.this, getString(R.string.device_connect_fail), Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	};

	private void getWifiInfo() {
		if (!NetUtils.isWifiConnected(this)) {
			mTvCurWifi.setText(R.string.wifi_unfound);
			mTvCurWifi.setEnabled(false);
		} else {
			WifiManager wifiMgr = (WifiManager) getApplicationContext()
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			String ssid = wifiInfo != null ? wifiInfo.getSSID() : null;
			// LogUtil.log(TAG + " initUI ssid:" + ssid + ",wifiInfo:" +
			// wifiInfo);
			if (!TextUtils.isEmpty(ssid)) {
				ssid = ssid.replace("\"", "");
			}
			mTvCurWifi.setText(ssid);
			mTvCurWifi.setEnabled(true);
		}
	}

	public void initData() {
		mBleDevice = (BleDevice) getIntent().getSerializableExtra(
				SAWConfigurationActivity.EXTRA_DEVICE);
		Log.e(TAG, "当前蓝牙设备:" + mBleDevice);
		mManager = NoxSAWManager.getInstance(this);
		// 断开蓝牙连接
//		new Thread() {
//			@Override
//			public void run() {
//				super.run();
//				mManager.disconnect();
//			}
//		}.start();

	}

	public void findView() {
		mTvCurWifi = (TextView) findViewById(R.id.tv_cur_wifi);
		mEtWifiPwd = (EditText) findViewById(R.id.et_wifi_pwd);
		mBtnSubmit = (Button) findViewById(R.id.btn_next);
	}

	public void initListener() {
		mBtnSubmit.setOnClickListener(this);
		mEtWifiPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				mBtnSubmit.setEnabled(s.length() > 7);
			}

			public void afterTextChanged1(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

		String curWifi = mTvCurWifi.getText().toString().trim();
		String psw = mEtWifiPwd.getText().toString().trim();

		if (!NetUtils.isWifiConnected(WifiAccountActivity.this)) {
			Toast.makeText(WifiAccountActivity.this,
					R.string.wifi_not_connected, 3000);
			return;
		}
		if (TextUtils.isEmpty(psw) || TextUtils.isEmpty(curWifi)) {
			return;
		}
		mResultCode = RESULT_OK;

		Intent intent = new Intent();
		intent.putExtra(EXTRA_SSID, mTvCurWifi.getText().toString().trim());
		intent.putExtra(EXTRA_PSW, psw);
		// LogUtil.logE(TAG + "   设置SSID:" + mTvCurWifi.getText() + "  PSW:" +
		// psw);
		setResult(mResultCode, intent);
		super.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next:
			if (mManager.isBluetoothOpen()) {
				if (!mManager.isConnected()) {
					Log.e(TAG,
							"mBleDevice.getAddress():"
									+ mBleDevice.getAddress()
									+ "===mBleDevice.getDeviceType()===:"
									+ mBleDevice.getDeviceType());
					mManager.connectDevice(DeviceManager.ConnectType.BLE,
							mBleDevice.getAddress(),
							mBleDevice.getDeviceType(), 16000);
					mShowConnectedStatus = true;
					showLoading();
				} else {

					finish();
				}
			} else {
				Intent enabler = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
			}
			break;

		}
	}

	public void initUi() {

	}
}
