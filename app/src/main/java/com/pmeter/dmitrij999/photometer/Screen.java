package com.pmeter.dmitrij999.photometer;


//import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.util.concurrent.TimeUnit;


public class Screen extends Activity {

    RadioGroup scrView;
    Graph_Frag graph_frag;
    Console_Frag console_frag;
    public client tcp_net;
    FragmentTransaction fTrans;
    SharedPreferences ip_backup;
    public static String reply;
    boolean measured = false;
    boolean connect = false;
    public static String measures[];
    final String SAVED_IP = "192.168.1.100";
    String ip;

    final String LOG_TAG = "myLogs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_screen);
        graph_frag = new Graph_Frag();
        console_frag = new Console_Frag();
        scrView = (RadioGroup) findViewById(R.id.scr);
        scrView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                fTrans = getFragmentManager().beginTransaction();
                //FrameLayout frame = (FrameLayout) findViewById(R.id.screen_frag);
                switch (checkedId) {
                    case -1:
                        //Toast.makeText(getApplicationContext(), "No choice", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.graphic:

                        //fTrans.remove(console_frag);
                        if (measured) fTrans.replace(R.id.screen_frag, graph_frag);
                        else Toast.makeText(getApplicationContext(), "Невозможно показать график без измерений!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "Radio0", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cons:
                        //Toast.makeText(getApplicationContext(), "Radio1", Toast.LENGTH_SHORT).show();

                        fTrans.replace(R.id.screen_frag, console_frag);

                        break;
                    default:
                        break;
                }
                fTrans.commit();
            }
        });
        if (savedInstanceState == null) {
            connect = false;
        }
        else {
            connect = savedInstanceState.getBoolean("connect");
        }
        Button con = (Button) findViewById(R.id.connect);
        final EditText ipaddress = (EditText) findViewById(R.id.ip);
        Button measure = (Button) findViewById(R.id.measure);
        Button KnowIP = (Button) findViewById(R.id.know_ip);
        load_ip();
        KnowIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connect) {
                    tcp_net.sendMessage("ip?\n\r\n");
                }
                else Toast.makeText(getApplicationContext(), R.string.conclosed, Toast.LENGTH_SHORT).show();
            }
        });



        //
        //
        //connection();

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tcp_net == null || !connect || client.getError())  Toast.makeText(getApplicationContext(), R.string.conclosed, Toast.LENGTH_SHORT).show();
                else {
                    Button meas = (Button) findViewById(R.id.measure);
                    meas.setEnabled(false);
                    Thread ms = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            measured = false;

                            int i;
                            measures = new String[16];
                            for (i = 0;i<16;i++) measures[i] = "0";
                            reply = "0";
                            try {
                                for (i = 0;i<16;i++) {
                                    tcp_net.sendMessage("dat" + i + "\n\r\n");
                                    Log.d("Send", "dat" + i + "\n\r");
                                    TimeUnit.MILLISECONDS.sleep(50);
                                    if (reply != null) {
                                        measures[i] = reply;
                                        Log.d("Meas ", " " + i + " " + reply);
                                    }
                                    TimeUnit.MILLISECONDS.sleep(50);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finally {
                                measured = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Button meas = (Button) findViewById(R.id.measure);
                                        meas.setEnabled(true);
                                    }
                                });

                            }
                        }
                    });
                    ms.start();

                }
            }
        });
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = ipaddress.getText().toString();
                connect = !connect;
                connection();
            }
        });
        Log.d(LOG_TAG, "onCreate");
    }
/*

*/


    protected void load_ip() {
        final EditText ipaddress = (EditText) findViewById(R.id.ip);
        ip_backup = getPreferences(MODE_PRIVATE);
        String savedIP = ip_backup.getString(SAVED_IP, "");
        ipaddress.setText(savedIP);
    }
    protected void save_ip() {
        final EditText ipaddress = (EditText) findViewById(R.id.ip);
        ip_backup = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = ip_backup.edit();
        ed.putString(SAVED_IP, ipaddress.getText().toString());
        ed.commit();
    }
    public static String getMeasure(int j) {
        return measures[j];
    }
    protected void connection() {
        Button con = (Button) findViewById(R.id.connect);
        connectTask cn = new connectTask();
        if (!connect) {
            con.setText(R.string.connect);
            cn.cancel(true);

        }
        else {
            cn.execute("");
            con.setText(R.string.disconnect);

            if (tcp_net != null) {
                tcp_net.sendMessage("ip?\n\r");
            }

        }
        if (client.getError()) {
            connect = false;
            con.setText(R.string.connect);
            cn.cancel(true);
            Toast.makeText(getApplicationContext(), R.string.con_error, Toast.LENGTH_SHORT).show();
        }
        else if (connect) {
            save_ip();
            Toast.makeText(getApplicationContext(), R.string.con_success, Toast.LENGTH_SHORT).show();
        }

    }
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume ");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connect", connect);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    /*public static void putRes(String msg) {
        reply = msg;
    }*/

    public class connectTask extends AsyncTask<String,String,client> {

        @Override
        protected client doInBackground(String... message) {

            //we create a TCPClient object and
            tcp_net = new client(new client.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            tcp_net.Set(ip);
            tcp_net.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            reply = values[0];
            //Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_SHORT).show();


            //in the arrayList we add the messaged received from server
            //arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //tvInfo.setText("Cancel");

            //tcp_net.stopClient();
            Toast.makeText(getApplicationContext(), R.string.conclosed, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Cancel");

        }

    }
}




