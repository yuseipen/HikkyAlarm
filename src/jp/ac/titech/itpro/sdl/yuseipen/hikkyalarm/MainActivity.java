package jp.ac.titech.itpro.sdl.yuseipen.hikkyalarm;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TimePicker alarm_tp;
	private int snooze_interval;
	private MyAlarmManager mam;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //画面の設定
        alarm_tp = (TimePicker)findViewById(R.id.alarm_picker);
        final EditText snooze_edit_text = (EditText)findViewById(R.id.snooze_edit_text);
        final Button set_button = (Button)findViewById(R.id.set_btn);
        final Button cancel_button = (Button)findViewById(R.id.cancel_btn);
        mam = new MyAlarmManager(MainActivity.this);
        
        //アラームセットボタンの設定
        set_button.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View arg0){
          		if(!TextUtils.isEmpty(snooze_edit_text.getText())){
          			snooze_interval = Integer.parseInt(snooze_edit_text.getText().toString().trim());
          			mam.addAlarm(alarm_tp.getCurrentHour(), alarm_tp.getCurrentMinute(), snooze_interval);
          			set_button.setEnabled(false);
              		cancel_button.setEnabled(true);
          		}else{
          			Toast.makeText(MainActivity.this, "スヌーズの時間を設定してください", Toast.LENGTH_LONG).show();
          		}
        	}
        });
        
        //アラームキャンセルボタンの設定
        cancel_button.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View arg0){
        		set_button.setEnabled(true);
          		cancel_button.setEnabled(false);
          		mam.stopAlarm();
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
