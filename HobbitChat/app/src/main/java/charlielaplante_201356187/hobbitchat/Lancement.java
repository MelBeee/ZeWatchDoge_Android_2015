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
import android.widget.Toast;


public class Lancement extends AppCompatActivity {

    // Constantes
    final int MAXUSER = 8;
    final int MINUSER = 2;
    final int MAXLENGTH = 5;
    final int MINLENGTH = 4;
    final int MAXPORT = 65535;
    final int MINPORT = 1024;
    final String ComteIp = "230.0.0.1";
    final String MordorIp ="230.0.0.2";
    final String IsengardIp ="230.0.0.3";

    // Composantes de l'interface
    Spinner AdresseIP;
    EditText Username;
    EditText UnPort;
    TextView UneAdresse;
    TextView Port;
    TextView UnUsername;

    //------------AU DEBUT DU LANCEMENT DE L'APPLICATION------------\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancement);

        // Affectation des variables de composantes
        UneAdresse = (TextView) findViewById(R.id.Adresse);
        Port = (TextView) findViewById(R.id.Port);
        UnUsername = (TextView) findViewById(R.id.Username);
        AdresseIP = (Spinner)findViewById(R.id.SP_AddressIP);
        Username = (EditText)findViewById(R.id.ET_Username);
        UnPort = (EditText)findViewById(R.id.ET_Port);

        // Ici on genere le spinner qui affiche les choix de serveur
        CreationDuSpinner();

        // Initialise le texte des "label"
        UneAdresse.setText(R.string.AdresseIP);
        Port.setText(R.string.Port);
        UnUsername.setText(R.string.Username);
    }

    // Ici on crée le spinner
    private void CreationDuSpinner()
    {
        // affectation des variables de la composante
        AdresseIP = (Spinner)findViewById(R.id.SP_AddressIP);
        // on get les items de l'array
        String[] items = getResources().getStringArray(R.array.IP_Array);
        // on attribue les items au spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items);
        AdresseIP.setAdapter(adapter);
    }

    //------------SAUVEGARDE POUR CHANGEMENT D'ORIENTATION------------\\

    // Juste avant que l'écran change d'orientation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // On enregistre juste si le textbox est rempli
        if(!Username.getText().toString().equals(""))
        {
            // on prend le nom d'usager deja rentré dans le textbox
            outState.putString("NomUsager", Username.getText().toString());
        }
        if(!UnPort.getText().toString().equals(""))
        {
            // on prend le port deja rentré dans le textbox
            outState.putString("Port", UnPort.getText().toString());
        }
        outState.putInt("Adresse", AdresseIP.getSelectedItemPosition());
    }

    // Juste après que l'écran change d'orientation mais avant le réaffichage
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // on remet le nom usager
        String tempo = savedInstanceState.getString("NomUsager");
        Username.setText(tempo);
        // on remet le port
        String tempo1 = savedInstanceState.getString("Port");
        UnPort.setText(tempo1);
        // on remet l'adresse dans le spinner
        int tempo2 = savedInstanceState.getInt("Adresse");
        AdresseIP.setSelection(tempo2);
    }

    //------------EVENEMENT CLICK DU BOUTON------------\\

    public void Connecter(View v)
    {
        String ConnectionIp;
        // On verifie si nos textbox sont rempli ou non
        if(Username.getText().toString() != "" && UnPort.getText().toString() != "")
        {
            // On verifie quel item est selectionné dans le spinner
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

            // On verifie si le nom d'utilisateur respecte les bornes (2-8 caracteres)
            if(Username.getText().toString().length() >= MINUSER && Username.getText().toString().length() <= MAXUSER)
            {
                // On verifie si le port respecte les bornes (4-5 caracteres)
                if(UnPort.getText().toString().length() >= MINLENGTH && UnPort.getText().toString().length() <= MAXLENGTH)
                {
                    // On verifie si le port respecte les bornes (1024-65535)
                    if(Integer.parseInt(UnPort.getText().toString()) >= MINPORT && Integer.parseInt(UnPort.getText().toString()) <= MAXPORT)
                    {
                        Intent communication = new Intent(this, Communication.class);

                        // affectation des variables de composante
                        String LeUsername = Username.getText().toString();
                        String LePort = UnPort.getText().toString();

                        // on insert les informations qu'on veut pour la 2e activité
                        communication.putExtra("Ip",ConnectionIp);
                        communication.putExtra("Username", LeUsername);
                        communication.putExtra("Port",LePort);

                        // on démarre l'activité 2
                        startActivity(communication);
                    }
                    else
                    {
                        // si condition non respecté on affiche un toast pour que l'utilisateur soit satisfait
                        Toast.makeText(Lancement.this, R.string.BornePort, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    // si condition non respecté on affiche un toast pour que l'utilisateur soit satisfait
                    Toast.makeText(Lancement.this, R.string.BorneMaxPort, Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                // si condition non respecté on affiche un toast pour que l'utilisateur soit satisfait
                Toast.makeText(Lancement.this, R.string.BorneUsager, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //------------DES CHOSES AUTO-CRÉÉ------------\\

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lancement, menu);
        return true;
    }

}
