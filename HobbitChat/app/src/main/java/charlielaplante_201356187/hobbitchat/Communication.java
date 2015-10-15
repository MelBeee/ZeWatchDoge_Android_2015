package charlielaplante_201356187.hobbitchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;
import org.w3c.dom.Text;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class Communication extends AppCompatActivity {

    // Les constantes
    final int HIGHLIMIT = 60;
    final int LOWLIMIT = 1;

    // Les infos recu de Lancement.java
    String username;
    String port;
    String Ip;

    // Les composantes de l'interface
    EditText MessageEnvoie;
    TextView MessageRecus;
    ScrollView leScroll;
    CheckBox CheckBoxIp;

    // Les variables pour tchater !
    byte tamponRecevoir[];
    InetAddress adresse;
    DatagramPacket paquetRecevoir;
    MulticastSocket socketRecevoir;
    // flag pour
    boolean FlagAssync;
    // variable pour savoir si on affiche l'adresse IP
    boolean IpOn = false;

    //------------AU DEBUT DU LANCEMENT DE L'APPLICATION------------\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("HobbitChat");
            lock.acquire();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);

        // On remet le flag a true
        FlagAssync= true;

        Intent intent = getIntent();

        // On va chercher les informations qu'on a envoyé de l'autre activité
        username = intent.getStringExtra("Username");
        port =  intent.getStringExtra("Port");
        Ip =   intent.getStringExtra("Ip");

        // On affiche les messages qui ont été envoyer pendant le load de l'interface
        RecevoirMSG();

        // Affectation des variables de composantes
        leScroll = (ScrollView)findViewById(R.id.UnScrollView);
        MessageEnvoie = (EditText)findViewById(R.id.ET_Message);
        MessageRecus = (TextView)findViewById(R.id.TV_Message);
        CheckBoxIp = (CheckBox)findViewById(R.id.CB_ShowAddressIP);
        MessageRecus.setMovementMethod(new ScrollingMovementMethod());
    }

    // Lorsque l'application est en pause
    @Override
    public void onPause()
    {
        super.onPause();
        FlagAssync = false;
    }

    // Lorsque l'application est detruite
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FlagAssync = false;
    }

    //------------SAUVEGARDE POUR CHANGEMENT D'ORIENTATION------------\\

    // Juste avant que l'écran change d'orientation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // On verifie si le message a envoyer est vide
        if(!MessageEnvoie.getText().toString().matches(""))
        {
            // on l'enregistre s'il n'est pas vide
            outState.putString("MsgAEnvoyer", MessageEnvoie.getText().toString());
        }
        // On verifie si le textview est rempli de message
        if(!MessageRecus.getText().toString().matches(""))
        {
            // on l'enregistre s'il n'est pas vide
            outState.putString("TextView", MessageRecus.getText().toString());
        }
        // On verifie si le checkbox est selectionner
        if(CheckBoxIp.isChecked())
            // si oui, on enregistre true
            outState.putBoolean("IpOn" , true);
        else
            // si non, on enregistre non
            outState.putBoolean("IpOn" , false);
    }

    // Juste après que l'écran change d'orientation mais avant le réaffichage
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // on remet le message écrit par l'utilisateur
        String temp1 = savedInstanceState.getString("MsgAEnvoyer");
        MessageEnvoie.setText(temp1);
        // on remet le textview en place
        String temp2 = savedInstanceState.getString("TextView");
        MessageRecus.setText(temp2);
        // on remet la variable a true/false selon le cas
        IpOn = savedInstanceState.getBoolean("IpOn");
    }

    //------------EVENEMENT DU CLICK DU BOUTON ENVOYER------------\\

    public void StartEnvoyerThread(View v)
    {
        // On verifie si le message est vide ou non
        if(!MessageEnvoie.getText().toString().matches(""))
        {
            // on verifie si le message comporte entre 1 et 60 messages
            if(MessageEnvoie.getText().length() >= LOWLIMIT && MessageEnvoie.getText().length() <= HIGHLIMIT)
            {
                // get le message que l'usager a entrer
                String LeMessage = MessageEnvoie.getText().toString();

                // on crée un instance de thread pour envoyer le message et les info requise
                Thread t = new Thread(new ThreadEnvoyer(LeMessage, adresse, port, username));
                t.setDaemon(true);
                t.start();

                // on set le textbox a vide
                MessageEnvoie.setText("");
            }
            else // si non, on affiche un message d'alerte
            {
                Toast.makeText(Communication.this, R.string.BorneMSG, Toast.LENGTH_SHORT).show();
            }
        }
        else // si non, on affiche un message d'alerte
        {
            Toast.makeText(Communication.this, R.string.BorneVide, Toast.LENGTH_SHORT).show();
        }
    }

    //------------EVENEMENT DE SELETION DU CHECKBOX------------\\

    public void CBChecked(View v)
    {
        // si check, on met notre variable a true
        if(CheckBoxIp.isChecked())
        {
            IpOn =true;
        }
        else // si uncheck, on met notre variable a false
        {
            IpOn = false;
        }
    }

    //------------ICI ON RECOIT DES MESSAGES------------\\

	// 
    public void RecevoirMSG()
    {
        try
        {
			// transforme l'ip en inetaddress
            adresse = InetAddress.getByName(Ip);
			// initialise, nombre de bite maximum qu'on peut recevoir
            tamponRecevoir = new byte[1024];
			// Faire un paquet avec les tampons pour l'envoyer au serveur
            paquetRecevoir = new DatagramPacket(tamponRecevoir, 0,tamponRecevoir.length,adresse,Integer.parseInt(port));
			// Creer le socket en multicast mais on lui dit quel port écouter
            socketRecevoir = new MulticastSocket(Integer.parseInt(port));
			// Timeout pour que l'application fonctionne lorsqu'on change de tchat. 
			// Si l'application ne fait plus rien parce qu'on a changer et reste pris dans la boucle, 
			// il va changer pour terminer le thread
            socketRecevoir.setSoTimeout(1000);
			// Pour join le groupe multicast a tel adresse au port mentionné plus haut
            socketRecevoir.joinGroup(adresse);
			
			// Debute l'Async 
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
                while(FlagAssync)
                {
                    try
                    {
						// Recoit un paquet de tampon qui est en fait un message
                        socketRecevoir.receive(paquetRecevoir);
						// Decompose le paquet recu pour avoir un message clair soit "Nom : Message"
                        String LeMessageRecus  = new String(paquetRecevoir.getData(),paquetRecevoir.getOffset(),paquetRecevoir.getLength());
                        // Trouve l'adresse IP d'origine du paquet
						String LeIp = paquetRecevoir.getAddress().toString();
                        LeIp = LeIp.substring(1);
						// On va afficher le message
                        publishProgress(LeMessageRecus,LeIp);

                    }catch(SocketTimeoutException ex) { }
                }
            }
            catch(Exception ex) { }

            return "Executed";
        }

        @Override
        protected void onProgressUpdate(String... values) {

            // On verifie si l'utilisateur a cocher le checkbox pendant que l'utilisation
            if(IpOn)
            {
                // si oui, on affiche l'ip + le nom + le message
                MessageRecus.append("("+ values[1].toString() +")" + values[0].toString() + "\n" );
            }
            else
            {
                // si non, on affiche seulement le nom et le message
                MessageRecus.append(values[0].toString() + "\n" );
            }
            // on scroll jusqu'en bas a chaque message recu
            leScroll.fullScroll(ScrollView.FOCUS_DOWN);
        }


    }

    //------------DES CHOSES AUTO-CRÉÉ------------\\

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

}
