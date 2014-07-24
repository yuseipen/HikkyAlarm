package jp.ac.titech.itpro.sdl.yuseipen.hikkyalarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmManager {
    Context c;
    AlarmManager am;
    private PendingIntent mAlarmSender;

    private static final String TAG = MyAlarmManager.class.getSimpleName();

    public MyAlarmManager(Context c){
        this.c = c;
        am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * アラームをセットする
     * @param alarmHour アラームを鳴らす時間
     * @param alarmMinute アラームを鳴らす分
     * @param snooze_interval アラームを再度鳴らす間隔
     * */
    public void addAlarm(int alarmHour, int alarmMinute, int snooze_interval){
        //AlarmManagerにセットするPendingIntentを設定
        mAlarmSender = this.getPendingIntent(snooze_interval);

        // アラーム時間設定
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 過去だったら明日にする
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
        	cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Toast.makeText(c, String.format("%02d時%02d分に起こします", alarmHour, alarmMinute), Toast.LENGTH_LONG).show(); 
        
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
    }

    /**
     * アラームをキャンセルする
     * */
    public void stopAlarm() {
        if(am != null){
        	am.cancel(mAlarmSender);
        }
    }
    
    /**
     * @param snooze_interval PendingIntentにputExtraする時間(アラームを再度鳴らす間隔(分単位))
     * @param latitude PendingIntentにputExtraする緯度(元の地点の緯度)
     * @param longitude PendingIntentにputExtraする経度(元の地点の経度)
     * */
    public void addSnooze(int snooze_interval, double latitude, double longitude){
    	mAlarmSender = this.getPendingIntent(latitude, longitude, snooze_interval);
    	Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // 現在時刻から指定されただけ後の時刻をカレンダーに設定
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, snooze_interval);
        // 過去だったら明日にする
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
        	cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Toast.makeText(c, String.format("%02d分後に起こします", snooze_interval), Toast.LENGTH_LONG).show();
        
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
    }

    /**
     * 初回のアラームのためのPendingIntentを作成する
     * intentにはスヌーズ間隔のみputExtraする
     * @param snooze_interval アラームを再度鳴らす間隔(分単位)
     * */
    private PendingIntent getPendingIntent(int snooze_interval) {
        // アラームを指定した時刻にBroadcastを投げる 
        Intent intent = new Intent("MyAlarmAction");
        intent.putExtra("snooze_interval", snooze_interval);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    
    /**
     * 2回目以降のアラームのためのPendingIntentを作成する
     * intentにはスヌーズ間隔に加えて緯度と経度をputExtraする
     * @param latitude 元の地点の緯度
     * @param longitude 元の地点の経度
     * @param snooze_interval アラームを再度鳴らす間隔(分単位)
     * */
    private PendingIntent getPendingIntent(double latitude, double longitude, int snooze_interval) {
    	Intent intent = new Intent("MyAlarmAction");
    	intent.putExtra("latitude", latitude);
    	intent.putExtra("longitude", longitude);
    	intent.putExtra("snooze_interval", snooze_interval);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;    	
    }
}