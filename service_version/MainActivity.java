package com.example.nol.tryaccelerometer;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MyService s;

    TextView textX, textY, textZ, textStatus, textTime;
    String label = "";

    public Long startTime;
    boolean timeFlag = false;

    FileOutputStream fileOutputStream;
    File file;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));

        textX = (TextView) findViewById(R.id.textx);
        textY = (TextView) findViewById(R.id.texty);
        textZ = (TextView) findViewById(R.id.textz);
        textStatus = (TextView) findViewById(R.id.status);
        textTime = (TextView) findViewById(R.id.timer);


    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        MyService.MyBinder b = (MyService.MyBinder) binder;
        s = b.getService();
        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();

        textX.setText("X: " + s.getX());
        textY.setText("Y: " + s.getY());
        textZ.setText("Z: " + s.getZ());
        textStatus.setText(R.string.stop_record);
        textTime.setText("start recording for  0  seconds.");
    }

    @Override
    public void onServiceDisconnected(ComponentName name){
        s = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    // click WRITE button to start recording
    public void clickWrite(View view){
        //writeBtn = true;
        s.setWriteBtn(true);
        Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show();
        textStatus.setText(R.string.start_record);
        startTime = System.currentTimeMillis();
        timeFlag = true;
        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);
    }

    // count the time of recording period
    private Runnable updateTimer = new Runnable(){
        public void run(){
            Long seconds = 0L;
            if (timeFlag) {
                Long currentTime = System.currentTimeMillis();
                Long spentTime = currentTime - startTime;
                seconds = (spentTime / 1000) % 60;
            }
            textTime.setText("start recording for  " + seconds + "  seconds.");
            handler.postDelayed(this, 1000);
        }
    };

    // click STOP button to stop recording
    // and ask user to give a label
    public void clickStop(View view){
        s.setWriteBtn(false);
        textStatus.setText(R.string.stop_record);
        labelDialog();
        timeFlag = false;
    }

    private void labelDialog(){
        final String[] list = {"walk", "stand", "sit", "upstairs", "downstairs", "run"};
        ArrayAdapter<String> listAdapter;
        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        final ListView listView = (ListView) item.findViewById(R.id.label);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.label_title);
        builder.setView(item);
        final AlertDialog alert = builder.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                label = list[position];
                try {
                    File sdcard =  Environment.getExternalStorageDirectory();
                    File folder = new File(sdcard + File.separator + "test");
                    //folder.mkdirs();
                    file = new File(folder, "output.txt");

                    fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.write((label + "\n").getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    s.setWriteHead(false);
                    Toast.makeText(MainActivity.this, label, Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    e.printStackTrace();
                }
                alert.dismiss();
            }
        });
    }
}
