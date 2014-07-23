package jp.ac.titech.itpro.sdl.yuseipen.hikkyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent){
		// アラームを受け取って起動するActivityを指定、起動
		Intent notification = new Intent(context, AlarmNotificationActivity.class);
		notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Bundle extras = intent.getExtras();
		notification.putExtra("snooze_interval", extras.getInt("snooze_interval"));
		
		//2回目以降のスヌーズで呼び出されたか判定
		if(intent.hasExtra("latitude") && intent.hasExtra("longitude")){
		    notification.putExtra("latitude", extras.getDouble("latitude"));
		    notification.putExtra("longitude", extras.getDouble("longitude"));
		}
		context.startActivity(notification);
	}
}
