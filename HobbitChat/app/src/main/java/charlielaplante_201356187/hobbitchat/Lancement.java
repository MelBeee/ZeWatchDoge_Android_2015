package charlielaplante_201356187.hobbitchat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.TextView;


public class Lancement extends AppCompatActivity {
    final String ComteIp = "230.0.0.1";
    final String MordorIp ="230.0.0.2";
    final String IsengardIp ="230.0.0.3";
    Spinner AdresseIP;
    EditText Username;
    EditText UnPort;
    TextView UneAdresse;
    TextView Port;
    TextView UnUsername;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancement);

        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("HobbitChat");
            lock.acquire();
        }

        UneAdresse = (TextView) findViewById(R.id.Adresse);
        Port = (TextView) findViewById(R.id.Port);
        UnUsername = (TextView) findViewById(R.id.Username);
        AdresseIP = (Spinner)findViewById(R.id.SP_AddressIP);
        Username = (EditText)findViewById(R.id.ET_Username);
        UnPort = (EditText)findViewById(R.id.ET_Port);
        CreationDuSpinner();
        UneAdresse.setText(R.string.AdresseIP);
        Port.setText(R.string.Port);
        UnUsername.setText(R.string.Username);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(!Username.getText().toString().equals(""))
        {
            outState.putString("NomUsager", Username.getText().toString());
        }
        if(!UnPort.getText().toString().equals(""))
        {
            outState.putString("Port", UnPort.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String tempo = savedInstanceState.getString("NomUsager");
        Username.setText(tempo);
        String tempo1 = savedInstanceState.getString("Port");
        UnPort.setText(tempo1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lancement, menu);
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

    private void CreationDuSpinner()
    {
        AdresseIP = (Spinner)findViewById(R.id.SP_AddressIP);
        String[] items = getResources().getStringArray(R.array.IP_Array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items);
        AdresseIP.setAdapter(adapter);
    }

    public void Connecter(View v)
    {
        String ConnectionIp;
        if(Username.getText().toString() != "" && UnPort.getText().toString() != "")
        {
            if(AdresseIP.getSelectedItemId() == 0)
            {
                ConnectionIp = ComteIp;
            }
            else if(AdresseIP.getSelectedItemId() == 1)
            {
                ConnectionIp = MordorIp;
            }
            else
            {
                ConnectionIp = IsengardIp;
            }

            Intent intent = new Intent(this, Communication.class);

            String LeUsername = Username.getText().toString();
            String LePort = UnPort.getText().toString();
            intent.putExtra("Ip",ConnectionIp);
            intent.putExtra("Username", LeUsername);
            intent.putExtra("Port",LePort);
            startActivity(intent);
        }
    }
}
