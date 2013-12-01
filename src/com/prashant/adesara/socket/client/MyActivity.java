package com.prashant.adesara.socket.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity
{
    private TCPClient tcpClient = null;
    private connectTask conctTask = null;
    private SensorManager sensorManager;
    private Sensor sensor;

    private Button leftButton;
    private Button rightButton;
    private Button connectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        initListeners();
        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeConnection();
    }

    private void initView(){
        leftButton = (Button) findViewById(R.id.main_left_button);
        rightButton = (Button) findViewById(R.id.main_right_button);
        connectButton = (Button) findViewById(R.id.main_connect_button);
    }

    private void initListeners() {
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        connectButton.setOnClickListener(new ConnectButtonListener());
    }

    @SuppressLint("NewApi")
    private void initConnection() {
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void closeConnection() {
        try
        {
            tcpClient.sendMessage("bye");
            tcpClient.stopClient();
            conctTask.cancel(true);
            conctTask = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDisconnected();
    }

    private void setDisconnected() {
        connectButton.setText(R.string.main_disconnect);
        connectButton.setOnClickListener(new DisconnectButtonListener());

    }

    private void setConnected() {
        connectButton.setText(R.string.main_reconnect);
        connectButton.setOnClickListener(new ConnectButtonListener());
    }


    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (tcpClient != null) {
                    tcpClient.sendMessage("move_"+"x" + event.values[0] + "y" + event.values[1]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) 
        {
            tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {

                @Override
                public void messageReceived(String message)
                {
                	try
					{
                		publishProgress(message);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
                }
            });
            tcpClient.run();
            if (tcpClient != null) {
                publishProgress("connected");
                tcpClient.sendMessage("Hello!");
            } else {
                publishProgress("disconnected");

            }
            return null;
        }
 
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(((String)values[0]).equals("connected")){
                setConnected();
            }else if(((String)values[0]).equals("disconnected")){
                setDisconnected();
            }
        }
    }

    private class DisconnectButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            closeConnection();
        }

    }

    private class ConnectButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            initConnection();
        }

    }
}