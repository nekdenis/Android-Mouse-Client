package com.nek.airmouse.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.nek.airmouse.R;
import com.nek.airmouse.socket.client.TCPClient;

public class ControlActivity extends Activity {

    private static final String EXTRA_IP = "EXTRA_IP";

    private com.nek.airmouse.socket.client.TCPClient tcpClient = null;
    private connectTask conctTask = null;
    private SensorManager sensorManager;
    private Sensor sensor;

    private String ipAddress;
    private View mDecorView;
    private Button leftButton;
    private Button rightButton;
    private Button connectButton;
    private Button scroll;
    private GestureDetector scrollListener;


    public static void startActivity(Context context, String ipAddress) {
        Intent i = new Intent(context, ControlActivity.class);
        i.putExtra(EXTRA_IP, ipAddress);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ipAddress = getIntent().getStringExtra(EXTRA_IP);
        mDecorView = getWindow().getDecorView();
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
        leftButton = (Button) findViewById(R.id.mouse_left_button);
        rightButton = (Button) findViewById(R.id.mouse_right_button);
        connectButton = (Button) findViewById(R.id.mouse_connect_button);
        scroll = (Button) findViewById(R.id.mouse_scroll);
        scrollListener = new GestureDetector(this, new ScrollListener());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideSystemUI();
        }
    }

    private void initListeners() {
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tcpClient != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        tcpClient.sendMessage("clck_d_" + "lc");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        tcpClient.sendMessage("clck_u_" + "lc");
                    }
                }
                return false;
            }
        });
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tcpClient != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        tcpClient.sendMessage("clck_d_" + "rc");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        tcpClient.sendMessage("clck_u_" + "rc");
                    }
                }
                return false;
            }
        });
        connectButton.setOnClickListener(new ConnectButtonListener());
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return scrollListener.onTouchEvent(motionEvent);
            }
        });
    }

    @SuppressLint("NewApi")
    private void initConnection() {
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void closeConnection() {
        try
        {
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

    public class connectTask extends AsyncTask<String,String, com.nek.airmouse.socket.client.TCPClient> {
        @Override
        protected com.nek.airmouse.socket.client.TCPClient doInBackground(String... message)
        {
            tcpClient = new com.nek.airmouse.socket.client.TCPClient(new TCPClient.OnMessageReceived() {

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
            }, ipAddress);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

    private class ScrollListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (tcpClient != null) {
                tcpClient.sendMessage("scrl_"+(int) distanceY);
            }
            return true;
        }
    }
}