package com.pmeter.dmitrij999.photometer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by dmitrij999 on 15.04.16.
 */
public class client {
    private String serverMessage;
    public static String SERVERIP = "192.168.1.100"; //your computer IP address
    public static int SERVERPORT = 80;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    public static boolean errors = false;


    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public client(OnMessageReceived listener) {
        mMessageListener = listener;
    }



    public void Set(String ip) {
        SERVERIP = ip;
        //Log.d("IP", SERVERIP);
        //SERVERPORT = port;
    }
    public static boolean getError() {
        return errors;
    }
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){

        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;

    }



    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");
            //mMessageListener.messageReceived("Connecting...");
            //
            errors = false;
            //create a socket to make the connection with the server
            try {
                Socket socket = new Socket(serverAddr, SERVERPORT);
                try {

                    //send the message to the server
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    Log.e("TCP Client", "C: Sent.");
                    //mMessageListener.messageReceived("Sent ");

                    Log.e("TCP Client", "C: Done.");
                    errors = false;

                    //receive the message which the server sends back
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //in this while the client listens for the messages sent by the server
                    while (mRun) {
                        //Thread.sleep(300);

                            serverMessage = in.readLine();

                            if (serverMessage != null && mMessageListener != null) {
                                //replyed = false;
                                //call the method messageReceived from MyActivity class
                                mMessageListener.messageReceived(serverMessage);

                                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                                //Screen.putRes(serverMessage);
                                //replyed = true;
                            }

                            serverMessage = null;
                    }



                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);
                    mMessageListener.messageReceived("Error " + e);
                    errors = true;


                }

                finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    socket.close();
                }
            } catch (ConnectException e) {
                Log.e("TCP", "C: Error", e);
                errors = true;
                Log.e("TCP", "Словили...");
            }


        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);
            errors = true;
            mMessageListener.messageReceived("Error " + e);

        }

    }




    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);

    }
}
