package charlielaplante_201356187.hobbitchat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class Communication extends AppCompatActivity {

    final int HIGHLIMIT = 60;
    final int LOWLIMIT = 0;

    String username;
    String port;
    String Ip;

    EditText MessageEnvoie;
    TextView MessageRecus;
    ScrollView leScroll;
    TextView AdresseIPTextView;
    CheckBox CheckBoxIp;

    byte tamponRecevoir[];
    InetAddress adresse;
    DatagramPacket paquetRecevoir;
    MulticastSocket socketRecevoir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);


        Bundle extra = getIntent().getExtras();

        if(extra != null)
        {
            username = extra.getString("Username");
            port = extra.getString("Port");
            Ip = extra.getString("Ip");
        }

        leScroll = (ScrollView)findViewById(R.id.UnScrollView);
        MessageEnvoie = (EditText)findViewById(R.id.ET_Message);
        MessageRecus = (TextView)findViewById(R.id.TV_Message);
        AdresseIPTextView = (TextView)findViewById(R.id.TV_AdresseIP);
        CheckBoxIp = (CheckBox)findViewById(R.id.CB_ShowAddressIP);

        MessageRecus.setMovementMethod(new ScrollingMovementMethod());
        RecevoirMSG();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(!MessageEnvoie.getText().toString().equals(""))
        {
            outState.putString("MsgAEnvoyer", MessageEnvoie.getText().toString());
        }
        if(!MessageEnvoie.getText().toString().equals(""))
        {
            outState.putString("TextView", MessageRecus.getText().toString());
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String temp1 = savedInstanceState.getString("MsgAEnvoyer");
        MessageEnvoie.setText(temp1);

        String temp2 = savedInstanceState.getString("TextView");
        MessageRecus.setText(temp2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_communication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void RecevoirMSG()
    {
        try
        {
            adresse = InetAddress.getByName(Ip);

            tamponRecevoir = new byte[1024];

            paquetRecevoir = new DatagramPacket(tamponRecevoir, 0,tamponRecevoir.length,adresse,Integer.parseInt(port));

            socketRecevoir = new MulticastSocket(Integer.parseInt(port));

            socketRecevoir.joinGroup(adresse);

            new ThreadRecevoir().execute();

        }catch(Exception e)
        {
            System.err.println("Criss calice");
            e.printStackTrace();
            System.exit(1);
        }


    }


    private class ThreadRecevoir extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {
                while(socketRecevoir!=null)
                {
                    socketRecevoir.receive(paquetRecevoir);

                    String LeMessageRecus  = new String(paquetRecevoir.getData(),paquetRecevoir.getOffset(),paquetRecevoir.getLength());

                    publishProgress(LeMessageRecus);
                }
            }
            catch(Exception ex)
            {

            }
            return "Executed";
        }

        @Override
        protected void onProgressUpdate(String... values) {

            MessageRecus.append(values[0].toString() + "\n");
            leScroll.fullScroll(ScrollView.FOCUS_DOWN);
           // MessageEnvoie.setText("");
        }
    }

    public void StartEnvoyerThread(View v)
    {
        if(!MessageEnvoie.getText().toString().matches(""))
        {
             if(MessageEnvoie.getText().length() > LOWLIMIT || MessageEnvoie.getText().length() < HIGHLIMIT)
             {
                 String LeMessage = MessageEnvoie.getText().toString();
                 new Thread(new ThreadEnvoyer(LeMessage, adresse, port, username)).start();
                 MessageEnvoie.setText("");
             }
        }
    }

    public void CBChecked(View v)
    {
        if(CheckBoxIp.isChecked())
        {
            AdresseIPTextView.setText("IP : "+ Ip);
        }
        else
        {
            AdresseIPTextView.setText("");
        }

    }
}
