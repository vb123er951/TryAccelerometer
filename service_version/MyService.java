package com.example.nol.tryaccelerometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MyService extends Service implements SensorEventListener {
    private final IBinder mBinder = new MyBinder();

    private SensorManager sensorManager;
    private boolean accelerometerPresent;
    private Sensor accelerometerSensor;
    private String x, y, z;
    private FileOutputStream fileOutputStream;
    private File file;
    private boolean writeHead = false;
    private boolean writeBtn = false;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        startAccelerometer();
        return mBinder;
    }

    /*@Override
    public int onStartCommand(Intent intent, int flag, int startId){
        startAccelerometer();
        return Service.START_NOT_STICKY;
    }*/

    @Override
    public void onCreate(){
        startAccelerometer();
    }

    public class MyBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

    public String getX(){
        return x;
    }

    public String getY(){
        return y;
    }

    public String getZ(){
        return z;
    }

    public void setWriteHead(boolean b){
        writeHead = b;
    }

    public void setWriteBtn(boolean b){
        writeBtn = b;
    }

    private void startAccelerometer(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensorList.size() > 0 ){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            accelerometerPresent = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = String.valueOf(event.values[0]);
        y = String.valueOf(event.values[1]);
        z = String.valueOf(event.values[2]);
        writeValues();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    // write into file
    // file name: output.txt
    // down the directory: /storage/emulated/0/test/
    private void writeValues(){
        try {
            File sdcard =  Environment.getExternalStorageDirectory();
            //TextView sdpath = (TextView) findViewById(R.id.path);
            //sdpath.setText(sdcard.toString()+ File.separator + "test");
            //Log.d("SAVEPATH",sdcard.toString()+ File.separator + "test");
            File folder = new File(sdcard + File.separator + "test");
            folder.mkdirs();
            file = new File(folder, "output.txt");

            if (writeBtn) {
                fileOutputStream = new FileOutputStream(file, true);
                if (!file.exists()) {
                    file.createNewFile();
                    Log.d("FILE", "create new file");
                }
                // write header in file
                if (!writeHead) {
                    fileOutputStream.write("time,x,y,z\n".getBytes());
                    writeHead = true;
                }
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                fileOutputStream.write((ts + ",").getBytes());
                fileOutputStream.write((x + ",").getBytes());
                fileOutputStream.write((y + ",").getBytes());
                fileOutputStream.write((z + "\n").getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
