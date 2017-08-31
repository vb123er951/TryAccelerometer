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
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MyService s;

    TextView textX, textY, textZ;
    String label = "";

    FileOutputStream fileOutputStream;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));

        textX = (TextView) findViewById(R.id.textx);
        textY = (TextView) findViewById(R.id.texty);
        textZ = (TextView) findViewById(R.id.textz);
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
    }

    // click STOP button to stop recording
    // and ask user to give a label
    public void clickStop(View view){
        s.setWriteBtn(false);
        labelDialog();
    }

    private void labelDialog(){
        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        final EditText editText = (EditText) item.findViewById(R.id.label);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.label_title);
        builder.setView(item);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    label = editText.getText().toString();

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
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        builder.show();
    }
}