package jp.ac.titech.itpro.sdl.yuseipen.hikkyalarm;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmNotificationActivity extends Activity implements LocationListener{
		private static MediaPlayer mp;
		private MyAlarmManager mam;
	    private static final float HIKIKOMORI_SCOPE = 50;
	    private boolean isFirstAlarm = true;
	    private LocationManager mLocationManager;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState){
	    	super.onCreate(savedInstanceState);
	    	
	        setContentView(R.layout.alarm_notification);
	        setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        
	        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	        am.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);

	        // スクリーンロックを解除する
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
	                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
	                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
	                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        
	      //初回アラームか2回目以降のアラームかを判定
	        if(getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")){
	        	isFirstAlarm = false;
	        	TextView notification_textview = (TextView)findViewById(R.id.notification_textview);
		        notification_textview.setText("早く家を出てください！！！");
	        }
	                
	        // LocationManagerを取得
	        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	 
	        // Criteriaオブジェクトを生成
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	        criteria.setPowerRequirement(Criteria.POWER_LOW);
	        criteria.setSpeedRequired(false);
	        criteria.setBearingRequired(false);
	        criteria.setAltitudeRequired(false);
	        criteria.setCostAllowed(false);
	        
	        //locationManagerにListenerを登録
	        String provider = mLocationManager.getBestProvider(criteria, true);
	        mLocationManager.requestLocationUpdates(provider, 0, 0, this);
	                                
	        //アラームを止めるボタンを設定
	        Button stopButton = (Button)findViewById(R.id.notification_button);
	        stopButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mp != null){
						mp.stop();
					}
				}
			});
	    }
	    	    
	    @Override
	    public void onDestroy(){
	    	super.onDestroy();
	    	if(mp != null){
				mp.stop();
				mp.release();
			}
	    }

		@Override
		public void onLocationChanged(Location location) {
			// アラームが鳴った時の緯度の表示
	        TextView tv_lat = (TextView) findViewById(R.id.notification_latitude);
	        tv_lat.setText("現在の緯度:"+location.getLatitude());
	        // アラームが鳴った時の経度の表示
	        TextView tv_lng = (TextView) findViewById(R.id.notification_longitude);
	        tv_lng.setText("現在の経度:"+location.getLongitude());
			
	        //次のアラームを鳴らすためのMyAlarmManager
	        mam = new MyAlarmManager(AlarmNotificationActivity.this);
	        
	        //Intent中のextra（＝初回アラーム時の緯度経度）
	        Bundle extras = getIntent().getExtras();
	        
	        //MediaPlayerを設定
        	mp = MediaPlayer.create(this, R.raw.kc236v1);
	        
	        //初回のアラーム起動時にはその時の緯度経度を渡してaddSnooze
	        //2回目以降のアラーム時には初回のアラーム起動時の緯度経度を表示して、
	        //初回の緯度経度と現在の緯度経度を計算してその距離が一定以下であれば鳴らす
	        //その後IntentのExtrasにある緯度経度（＝初回アラーム時の緯度経度）を渡してaddSnooze
	        if(!isFirstAlarm){
	        	TextView tv_lat_wu = (TextView) findViewById(R.id.notification_latitude_wakeup);
	            tv_lat_wu.setText("起きた時の緯度:"+ extras.getDouble("latitude"));
	            TextView tv_lng_wu = (TextView) findViewById(R.id.notification_longitude_wakeup);
	            tv_lng_wu.setText("起きた時の経度:"+ extras.getDouble("longitude"));
	            float[] between = new float[1];
	        	Location.distanceBetween(location.getLatitude(), location.getLongitude(), extras.getDouble("latitude"), extras.getDouble("longitude"), between);
	        	TextView tv_btw = (TextView) findViewById(R.id.notification_between);
	            tv_btw.setText("起きた時との距離:"+between[0]);
	        	if(between[0] < HIKIKOMORI_SCOPE){
	                mam.addSnooze(extras.getInt("snooze_interval"), extras.getDouble("latitude"), extras.getDouble("longitude"));
	            	mp.setLooping(true);
	            	mp.start();
	        	}else{
	        		finish();
	        	}
	        }else{
	        	mam.addSnooze(extras.getInt("snooze_interval"), location.getLatitude(), location.getLongitude());
	        	mp.setLooping(true);
	        	mp.start();
	        }
	        mLocationManager.removeUpdates(this);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
}
