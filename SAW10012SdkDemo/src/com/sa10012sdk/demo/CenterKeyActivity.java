package com.sa10012sdk.demo;

import com.sa10012sdk.demo.util.ActivityUtil;
import com.sa10012sdk.demo.util.LogUtil;
import com.sleepace.sdk.core.nox.domain.CenterKey;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CenterKeyActivity extends BaseActivity {
	
	private TextView vMusic, vLight, vAroma;
	private CenterKey centerKey = new CenterKey(true, true, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center_key);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		vMusic = (TextView) findViewById(R.id.tv_music);
		vLight = (TextView) findViewById(R.id.tv_light);
		vAroma = (TextView) findViewById(R.id.tv_aroma);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		vMusic.setOnClickListener(this);
		vLight.setOnClickListener(this);
		vAroma.setOnClickListener(this);
	}

	public void initUI() {
		tvTitle.setText(R.string.sa_center_set);
		tvRight.setText(R.string.save);
		initCenterKeyView();
	}
	
	private void initCenterKeyView() {
		initViewCheckStatus(vMusic, centerKey.isAudioEnable());
		initViewCheckStatus(vLight, centerKey.isLightEnable());
		initViewCheckStatus(vAroma, centerKey.isAromaEnable());
	}

	@Override
	protected void onResume() {
		super.onResume();
		showLoading();
		mHelper.getCenterKey(3000, new IResultCallback<CenterKey>() {
			@Override
			public void onResultCallback(final CallbackData<CenterKey> cd) {
				// TODO Auto-generated method stub
				if(!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();
						if (cd.isSuccess()) {
							centerKey = cd.getResult();
							initCenterKeyView();
						} else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v == tvRight) {
			showLoading();
			mHelper.setCenterKey(centerKey.isLightEnable(), centerKey.isAudioEnable(), centerKey.isAromaEnable(), 3000, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					LogUtil.log(TAG+" setCenterKey cd:" + cd);
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if(cd.isSuccess()) {
								finish();
							}else {
								showErrTips(cd);
							}
						}
					});
				}
			});
		}else if(v == vMusic) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isAudioEnable())) {
				centerKey.setAudioEnable(!centerKey.isAudioEnable());
				initViewCheckStatus(vMusic, centerKey.isAudioEnable());
			}
		}else if(v == vLight) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isLightEnable())) {
				centerKey.setLightEnable(!centerKey.isLightEnable());
				initViewCheckStatus(vLight, centerKey.isLightEnable());
			}
		}else if(v == vAroma) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isAromaEnable())) {
				centerKey.setAromaEnable(!centerKey.isAromaEnable());
				initViewCheckStatus(vAroma, centerKey.isAromaEnable());
			}
		}
	}
	
	private int getCheckCount() {
		int count = 0;
		if(centerKey.isAudioEnable()) {
			count++;
		}
		
		if(centerKey.isLightEnable()) {
			count++;
		}
		
		if(centerKey.isAromaEnable()) {
			count++;
		}
		return count;
	}
	
	private void initViewCheckStatus(TextView v, boolean check) {
		if(check) {
			v.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
		}else {
			v.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
	}

}











