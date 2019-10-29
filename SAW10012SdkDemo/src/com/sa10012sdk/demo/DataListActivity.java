package com.sa10012sdk.demo;

import java.util.ArrayList;
import java.util.List;

import com.sa10012sdk.demo.bean.MusicInfo;
import com.sa10012sdk.demo.util.Utils;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DataListActivity extends BaseActivity {
	private ListView listView;
	private LayoutInflater inflater;
	private DataAdapter adapter;
	
	public static final int DATA_TYPE_ALARM_MUSIC = 1;
	public static final int DATA_TYPE_SLEEPAID_MUSIC = 2;
	public static final int DATA_TYPE_REPEAT = 3;
	
	private List<Object> list = new ArrayList<Object>();
	private int dataType = 0;
	private int musicId = 0;
	private boolean playing;
	
	private int selPos = 0;
	
	private int[] repeatVal;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		listView = (ListView) findViewById(R.id.list);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		listView.setOnItemClickListener(onItemClickListener);
	}

	public void initUI() {
		dataType = getIntent().getIntExtra("dataType", 0);
		musicId = getIntent().getIntExtra("musicId", 0);
		
		if(dataType == DATA_TYPE_REPEAT) {
			tvTitle.setText(R.string.reply);
			byte repeat = getIntent().getByteExtra("repeat", (byte)0);
			repeatVal = Utils.getWeekRepeat(repeat);
			String[] weeks = getResources().getStringArray(R.array.arr_week);
			for(int i=0;i<weeks.length;i++) {
				list.add(weeks[i]);
			}
		}else {
			tvTitle.setText(R.string.music_list);
			if(dataType == DATA_TYPE_ALARM_MUSIC) {
				for(int i=0;i<DemoApp.ALARM_MUSIC.length;i++) {
					MusicInfo mInfo = new MusicInfo();
					mInfo.setMusicID(DemoApp.ALARM_MUSIC[i][0]);
					mInfo.setMusicName(getString(DemoApp.ALARM_MUSIC[i][1]));
					list.add(mInfo);
					
					if(mInfo.getMusicID() == musicId) {
						selPos = i;
					}
				}
			}else {
				for(int i=0;i<DemoApp.SLEEPAID_MUSIC.length;i++) {
					MusicInfo mInfo = new MusicInfo();
					mInfo.setMusicID(DemoApp.SLEEPAID_MUSIC[i][0]);
					mInfo.setMusicName(getString(DemoApp.SLEEPAID_MUSIC[i][1]));
					list.add(mInfo);
					
					if(mInfo.getMusicID() == musicId) {
						selPos = i;
					}
				}
			}
		}
		
		inflater = getLayoutInflater();
		tvRight.setText(R.string.save);
		adapter = new DataAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BleHelper.REQCODE_OPEN_BT) {

		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(dataType == DATA_TYPE_REPEAT) {
				repeatVal[position] = 1 - repeatVal[position];
			}else {
				DataListActivity.this.selPos = position;
			}
			adapter.notifyDataSetChanged();
			
			if(dataType == DATA_TYPE_ALARM_MUSIC) {
				MusicInfo mInfo = (MusicInfo) adapter.getItem(position);
				mHelper.turnOnMusic(mInfo.getMusicID(), (byte)12, (byte)2, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						if(cd.isSuccess()) {
							playing = true;
						}
					}
				});
			}
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v == tvRight) {
			if(dataType == DATA_TYPE_ALARM_MUSIC) {
				MusicInfo mInfo = (MusicInfo) adapter.getItem(selPos);
				Intent data = new Intent();
				data.putExtra("musicId", mInfo.getMusicID());
				setResult(RESULT_OK, data);
				finish();
			}else if(dataType == DATA_TYPE_SLEEPAID_MUSIC) {
				MusicInfo mInfo = (MusicInfo) adapter.getItem(selPos);
				Intent data = new Intent();
				data.putExtra("musicId", mInfo.getMusicID());
				setResult(RESULT_OK, data);
				finish();
			}else if(dataType == DATA_TYPE_REPEAT) {
				byte repeat = Utils.getWeekRepeat(repeatVal);
				Intent data = new Intent();
				data.putExtra("repeat", repeat);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if(dataType == DATA_TYPE_ALARM_MUSIC) {
			if(playing) {
				mHelper.turnOffMusic(3000, null);
			}
		}
	}

	
	class DataAdapter extends BaseAdapter {
		
		DataAdapter(){ }

		class ViewHolder {
			TextView tvName;
			ImageView ivCheck;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_music_item, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_check);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if(dataType == DATA_TYPE_REPEAT) {
				String week = (String) getItem(position);
				holder.tvName.setText(week);
				if(repeatVal[position] == 1) {
					holder.ivCheck.setVisibility(View.VISIBLE);
				}else {
					holder.ivCheck.setVisibility(View.GONE);
				}
			}else {
				MusicInfo item = (MusicInfo) getItem(position);
				holder.tvName.setText(item.getMusicName());
				if(selPos == position) {
					holder.ivCheck.setVisibility(View.VISIBLE);
				}else {
					holder.ivCheck.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

	}

}
