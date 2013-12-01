package com.prashant.adesara.socket.client;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.prashant.custom.adapter.MyCustomAdapter;

/**
 * @author Prashant Adesara
 * Display Activity with sending messages to server 
 * */

@SuppressLint("NewApi")
public class MyActivity extends Activity
{
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        arrayList = new ArrayList<String>();
 
        final EditText editText = (EditText) findViewById(R.id.editText);
        Button send = (Button)findViewById(R.id.send_button);
 
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);
 
        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                //add the text in the arrayList
                arrayList.add("Android Client: " + message);
                //sends the message to the server
                if (mTcpClient != null) 
                {
                    mTcpClient.sendMessage("Android Client: "+message);
                }
                //refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(new SensorEventListener() {
        	float[] history = {0f,0f};
			@Override
			public void onSensorChanged(SensorEvent event) {
				
                if (mTcpClient != null) 
                {	
                		mTcpClient.sendMessage("x"+event.values[0]+"y"+event.values[1]);
                }
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		}, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    
    /**
     * @author Prashant Adesara
     * receive the message from server with asyncTask  
     * */
    public class connectTask extends AsyncTask<String,String,TCPClient> {
        @Override
        protected TCPClient doInBackground(String... message) 
        {
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() 
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) 
                {
                	try
					{
                		//this method calls the onProgressUpdate
                		publishProgress(message);
                		if(message!=null)
                		{
                			System.out.println("Return Message from Socket::::: >>>>> "+message);
                		}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
                }
            });
            mTcpClient.run();
            if(mTcpClient!=null)
            {
            	mTcpClient.sendMessage("Initial Message when connected with Socket Server");
            }
            return null;
        }
 
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
 
            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy()
    {
    	try
		{
    		System.out.println("onDestroy.");
			//mTcpClient.sendMessage("bye");
			mTcpClient.stopClient();
			conctTask.cancel(true);
			conctTask = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    	super.onDestroy();
    }
  
    
}