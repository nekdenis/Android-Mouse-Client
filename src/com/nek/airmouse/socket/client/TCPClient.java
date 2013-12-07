package com.nek.airmouse.socket.client;

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class TCPClient {

    public static final String MESSAGE_START = "Server_hi!";
    public static final String MESSAGE_END = "Server_bye!";

    private String serverMessage;
    private String serverIP = "";
    /**
	 * Specify the Server Ip Address here. Whereas our Socket Server is started.
	 * */
    public static final int SERVERPORT = 5657;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
 
    private PrintWriter out = null;
    private BufferedReader in = null;
    
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener, String serverIP)
    {
        mMessageListener = listener;
        this.serverIP = serverIP;
    }
 
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
        	System.out.println("message: "+ message);
            out.println(message);
            out.flush();
        }
    }
 
    public void stopClient(){
        sendMessage(MESSAGE_END);
        mRun = false;
    }
    
    public void run() {
 
        mRun = true;
 
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);
 
            Log.e("TCP SI Client", "SI: Connecting...");
 
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            try {
          
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP SI Client", "SI: Sent.");
                sendMessage(MESSAGE_START);
                Log.e("TCP SI Client", "SI: Done.");
                
                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                	serverMessage = in.readLine();
 
                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                        Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                    }
                    serverMessage = null;
                }
            }
            catch (Exception e) 
            {
                Log.e("TCP SI Error", "SI: Error", e);
                e.printStackTrace();
            }
            finally 
            {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }
 
        } catch (Exception e) {
 
            Log.e("TCP SI Error", "SI: Error", e);
 
        }
 
    }
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}