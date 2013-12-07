package com.nek.airmouse.socket.client;

import android.util.Log;

import java.net.*;
import java.util.Enumeration;


public class ServerExplorer {

    private static final String TAG = ServerExplorer.class.getSimpleName();

    private String serverMessage;
    /**
     * Specify the Server Ip Address here. Whereas our Socket Server is started.
     */
    public static final int SERVERPORT = 1900;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public ServerExplorer(final OnMessageReceived listener) {
        mMessageListener = listener;
    }


    public void stopClient() {
        mRun = false;
    }

    public void run(long endTime) {
        mRun = true;
        try {

            MulticastSocket socket = new MulticastSocket(SERVERPORT);
            InetAddress group = InetAddress.getByName("239.255.255.250");
            socket.joinGroup(group);
            socket.setSoTimeout(1000*10);
            while (mRun) {
                Log.d(TAG,"trying to read multicast message");
                DatagramPacket packet;

                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData());
                Log.d(TAG,"received: "+ received);
                if (mMessageListener != null) {
                    received = received.substring(0, received.indexOf("$$$"));
                    mMessageListener.messageReceived(received);
                }
                if(endTime<System.currentTimeMillis()){
                    Log.d(TAG,"reading time ended");
                    break;
                }
            }


            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {

            Log.e("TCP SI Error", "SI: Error", e);

        }
        mRun = false;

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    return interfaceAddress.getBroadcast().toString().substring(1);
                }
            }
        }
        return null;
    }
}