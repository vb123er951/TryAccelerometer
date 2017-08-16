import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
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
import java.io.Writer;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    boolean accelerometerPresent;
    Sensor accelerometerSensor;
    TextView textX, textY, textZ;
    Button button;
    String label = "";

    FileOutputStream fileOutputStream;
    File file;
    int tmp = 0;
    boolean writeHead = false;
    boolean writeBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textX = (TextView) findViewById(R.id.textx);
        textY = (TextView) findViewById(R.id.texty);
        textZ = (TextView) findViewById(R.id.textz);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensorList.size() > 0 ){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        } else {
            accelerometerPresent = false;
        }

        button = (Button) findViewById(R.id.write);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Register accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);
            Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    private SensorEventListener accelerometerListener = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1){

        }
        @Override
        public void onSensorChanged(SensorEvent event){
            textX.setText("X: " + String.valueOf(event.values[0]));
            textY.setText("Y: " + String.valueOf(event.values[1]));
            textZ.setText("Z: " + String.valueOf(event.values[2]));

            // write into file
            // file name: output.txt
            // down the directory: /storage/emulated/0/test/
            try {
                File sdcard =  Environment.getExternalStorageDirectory();
                TextView sdpath = (TextView) findViewById(R.id.path);
                sdpath.setText(sdcard.toString()+ File.separator + "test");
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
                    // write header in file, for drawing line chart
                    if (!writeHead) {
                        fileOutputStream.write("time,x,y,z\n".getBytes());
                        writeHead = true;
                    }
                    if (tmp == 15) { // do not write every time when changed, prefer once a second
                        Long tsLong = System.currentTimeMillis() / 1000;
                        String ts = tsLong.toString();
                        fileOutputStream.write((ts + ",").getBytes());
                        fileOutputStream.write((String.valueOf(event.values[0]) + ",").getBytes());
                        fileOutputStream.write((String.valueOf(event.values[1]) + ",").getBytes());
                        fileOutputStream.write((String.valueOf(event.values[2]) + "\n").getBytes());
                        fileOutputStream.flush();
                        tmp = 0;
                    }
                    tmp++;
                    fileOutputStream.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    public void onClick(View view){
        if (!writeBtn) {
            // press button one time to start recording
            writeBtn = true;
            Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show();
        } else {
            // press button another time to stop recording
            // and ask user to give a label
            labelDialog();
            writeBtn = false;
        }
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
                        folder.mkdirs();
                        file = new File(folder, "output.txt");

                        fileOutputStream = new FileOutputStream(file, true);
                        fileOutputStream.write((label + "\n").getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        writeHead = false;
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        builder.show();
    }
}
