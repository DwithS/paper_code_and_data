package test.andapplication;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {

    TextView textGyroX, textGyroY, textGyroZ;
    TextView textAcclX, textAcclY, textAcclZ;
    TextView textMagX, textMagY, textMagZ;

    EditText labelEText;
    TextView recCText;

    Button recButton, stopButton;
    Button addButton, saveButton;

    int accelValueX, accelValueY, accelValueZ;
    int gyroX, gyroY, gyroZ;
    int magX=0, magY=0, magZ=0;

    String outputString = "";

    final int countFactor = 50;

    int addCounter=0;

    SensorManager mSensorManager;
    Sensor gyroscope;
    Sensor accSensor;
    Sensor magSensor;
    Boolean gyroEnable = true;
    Boolean accEnable = true;
    Boolean magEnable = false;
    int sensorFreq;

    int recCounter;
    boolean tempRec = false;

    ArrayList<smallsensordata> dataRecorder;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    boolean checkG, checkM, checkA, checkB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSensor();



        checkA = checkB = checkG = false;
        checkM = !magEnable;

        recCText = (TextView) findViewById(R.id.RecCountText);

        recCounter = 0;
        dataRecorder = new ArrayList<smallsensordata>();

        recButton = (Button) findViewById(R.id.RecBtn);
        stopButton = (Button) findViewById(R.id.StopBtn);
        addButton = (Button) findViewById(R.id.AddBtn);
        saveButton = (Button) findViewById(R.id.SaveBtn);
        stopButton.setVisibility(View.GONE);


        labelEText = (EditText) findViewById(R.id.LabelNumEText);

        smallsensordata.accEnable = accEnable;
        smallsensordata.gyroEnable = gyroEnable;
        smallsensordata.magEnable = magEnable;
        outputString = initOutputString(outputString);
        initButton();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        //http://mommoo.tistory.com/49
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            //외부저장소 쓰기 허가를 받은 상태일때
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALENDAR)){
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else{
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }

    public void initButton() {
        recButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recCounter = 0;
                dataRecorder.clear();
                //Toast.makeText(getApplicationContext(),"Record Start", Toast.LENGTH_SHORT).show();
                tempRec = true;
                stopButton.setVisibility(View.VISIBLE);
                recButton.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addCounter++;
                Toast.makeText(getApplicationContext(),addCounter+"개 add됨",Toast.LENGTH_SHORT).show();
                addButton.setVisibility(View.GONE);
                outputString = addDatatoString(outputString, dataRecorder);
                dataRecorder.clear();

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tempRec = false;
                addButton.setVisibility(View.VISIBLE);

                //saveButton.setVisibility(View.VISIBLE);
                recButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(),"Save 버튼 눌림",Toast.LENGTH_SHORT).show();


                if(addCounter!=0){
                    save(outputString);
                    addCounter=0;
                }else{
                    Toast.makeText(getApplicationContext(),"데이터가 없습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        textGyroX = (TextView) findViewById(R.id.GyroXText);
        textGyroY = (TextView) findViewById(R.id.GyroYText);
        textGyroZ = (TextView) findViewById(R.id.GyroZText);

        textAcclX = (TextView) findViewById(R.id.AccelXText);
        textAcclY = (TextView) findViewById(R.id.AccelYText);
        textAcclZ = (TextView) findViewById(R.id.AccelZText);

        textMagX = (TextView) findViewById(R.id.MagXText);
        textMagY = (TextView) findViewById(R.id.MagYText);
        textMagZ = (TextView) findViewById(R.id.MagZText);


        sensorFreq = SensorManager.SENSOR_DELAY_FASTEST;
        //sensorFreq = SensorManager.SENSOR_DELAY_NORMAL;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String initOutputString(String output) {

        output = "";

        for (int i = 0; i < countFactor; i++) {
            if (gyroEnable) {
                output = output + "gyroX_" + i + "\t" + "gyroY_" + i + "\t" + "gyroZ_" + i + "\t";
            }
            if (accEnable) {
                output = output + "accX_" + i + "\t" + "accY_" + i + "\t" + "accZ_" + i + "\t";
            }
            if (magEnable) {
                output = output + "magX_" + i + "\t" + "magY_" + i + "\t" + "magZ_" + i + "\t";
            }
        }
        output = output +"Label";
        return output;
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = Math.round(event.values[0] * 1000);
            gyroY = Math.round(event.values[1] * 1000);
            gyroZ = Math.round(event.values[2] * 1000);
            textGyroX.setText("Gyro X = " + gyroX);
            textGyroY.setText("Gyro Y = " + gyroY);
            textGyroZ.setText("Gyro Z = " + gyroZ);
            checkG = true;
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            accelValueX = Math.round(event.values[0] * 1000);
            accelValueY = Math.round(event.values[1] * 1000);
            accelValueZ = Math.round(event.values[2] * 1000);
            textAcclX.setText("Accel X = " + accelValueX);
            textAcclY.setText("Accel Y = " + accelValueY);
            textAcclZ.setText("Accel Z = " + accelValueZ);
            checkA = true;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magX = Math.round(event.values[0] * 1000);
            magY = Math.round(event.values[1] * 1000);
            magZ = Math.round(event.values[2] * 1000);
            textMagX.setText("Mag X = " + magX);
            textMagY.setText("Mag Y = " + magY);
            textMagZ.setText("Mag Z = " + magZ);
            checkM = true;
        }
        if (tempRec && checkA && checkM &&checkG) {
                recCounter++;
            recCText.setText("Record Counts " + Integer.toString(recCounter));
            dataRecorder.add(new smallsensordata(gyroX, gyroY, gyroZ, accelValueX, accelValueY, accelValueZ, magX, magY, magZ));
            if(accEnable){
                checkA = false;
            }
            if(magEnable){
                checkM = false;
            }
            if(gyroEnable){
                checkG = false;
            }
        }
    }

    //파일 세이브



    public String addDatatoString(String output, ArrayList<smallsensordata> alpha) {
        float jumpNumber = (float)alpha.size() / (float)countFactor;
        output = output + System.lineSeparator();
        for (int i = 0; i < countFactor; i++) {
            int temp = (int)(i * jumpNumber);
            output = output + alpha.get(temp).getDataByLine() + "\t";
        }
        output = output + labelEText.getText().toString();
        return output;
    }

    public void save(String output) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);

        if(isExternalStorageWritable()){
            Toast.makeText(getApplicationContext(),"SD카드 있음",Toast.LENGTH_SHORT).show();
            final String strSpatrh = Environment.getExternalStorageDirectory().getAbsolutePath();
            strDate = strSpatrh + "/" + strDate;
        }else{
            Toast.makeText(getApplicationContext(),"SD카드 장착여부를 확인해주세요",Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileOutputStream outFs = new FileOutputStream(new File(strDate + ".txt"));
            //FileOutputStream outFs = openFileOutput(strDate+".txt",Context.MODE_WORLD_WRITEABLE);
            //String output = "count";

            /*String output = "count  gyroX   gyroY   gyroZ   AcclX   AcclY   AcclZ   MagX    MagY    MagZ";
            for(int i = 0; i<alpha.size();i++){
                output = output + System.lineSeparator() +  Integer.toString(i) + "\t" + alpha.get(i).getDataByLine();
            }*/
            Log.v("출력", output);
            outFs.write(output.getBytes());
            outFs.close();
            Toast.makeText(getApplicationContext(), strDate + ".txt로 파일 세이브", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "세이브 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 주기 설명
    // SENSOR_DELAY_UI 갱신에 필요한 정도 주기
    // SENSOR_DELAY_NORMAL 화면 방향 전환 등의 일상적인  주기
    // SENSOR_DELAY_GAME 게임에 적합한 주기
    // SENSOR_DELAY_FASTEST 최대한의 빠른 주기


    //리스너 등록
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, gyroscope, sensorFreq);
        mSensorManager.registerListener(this, accSensor, sensorFreq);
        if(magEnable) {
            mSensorManager.registerListener(this, magSensor, sensorFreq);
        }
    }

    //리스너 해제
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if(requestCode == 0){
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if(grantResult[0] == 0){
                //해당 권한이 승낙된 경우.
            }else{
                //해당 권한이 거절된 경우.
            }
        }
    }
}
