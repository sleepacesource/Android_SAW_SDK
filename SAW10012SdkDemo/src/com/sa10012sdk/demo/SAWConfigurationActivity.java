package com.sa10012sdk.demo;

import java.util.Map;

import com.sleepace.sdk.core.nox.NoxSAWManager;
import com.sleepace.sdk.core.nox.interfs.INoxManager.NetType;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.domain.Device;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceManager;
import com.sleepace.sdk.manager.DeviceType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SAWConfigurationActivity extends BaseActivity {
	private BleDevice mBleDevice;
	public final static String EXTRA_DEVICE = "extra_device";
	private static final int REQUEST_CODE_GET_WIFI_ACCOUNT = 1001;
	private NoxSAWManager mManager;
	private String mWifiSsid;
	private String mWifiPsw;
	protected final static int WAIT_DEVICE_TIMEOUT = 12000;

	boolean mNox2WifiNeedConfigRun;

	private TextView mTvProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saw_configuration);
		findView();
		mBleDevice = (BleDevice) getIntent().getSerializableExtra(EXTRA_DEVICE);
		mManager = NoxSAWManager.getInstance(this);
		mManager.setDeviceName(mBleDevice.getDeviceName());
		mManager.mConnectType = DeviceManager.ConnectType.BLE;
		Intent intent = new Intent(this, WifiAccountActivity.class);
		intent.putExtra(EXTRA_DEVICE, mBleDevice);
		startActivityForResult(intent, REQUEST_CODE_GET_WIFI_ACCOUNT);
	}

	public void findView() {
		mTvProgress = (TextView) findViewById(R.id.tv_progress);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

					Log.e(TAG, "设备状态回调:" + state);
					if (state == CONNECTION_STATE.DISCONNECT) {

					} else if (state == CONNECTION_STATE.CONNECTED) {

					}
				}
			});
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE_GET_WIFI_ACCOUNT) {
			// nox2wifi获取到wifi密码后
			if (resultCode == RESULT_OK) {
				mWifiSsid = data.getStringExtra(WifiAccountActivity.EXTRA_SSID);
				mWifiPsw = data.getStringExtra(WifiAccountActivity.EXTRA_PSW);
				// LogUtil.logE(TAG + "  获取到的SSID:" + mWifiSsid + "   PSW:" +
				// mWifiPsw);
				if (!TextUtils.isEmpty(mWifiSsid)) {
					startBind();
				}
			} else {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void startBind() {
		showLoading();
		mHelper.login(mBleDevice.getDeviceName(), mBleDevice.getAddress(),
				mWifiSsid, mWifiPsw, new IResultCallback() {

					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub

						Log.e(TAG, "登录设备回调结果:" + cd);

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								hideLoading();
								if (cd.isSuccess()) {
									Intent intent = new Intent(
											SAWConfigurationActivity.this,
											MainActivity.class);
									startActivity(intent);
								} else {
									showErrTips(cd);
									finish();
								}

							}
						});

					}
				});

	}

}
