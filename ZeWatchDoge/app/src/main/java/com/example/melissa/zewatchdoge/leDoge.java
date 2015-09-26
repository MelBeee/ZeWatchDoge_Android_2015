package com.example.melissa.zewatchdoge;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class leDoge extends AppCompatActivity {

    // Constantes
    public final int LETIMEOUT = 1000;
    public final int ENPAUSE = 1000;

    // Variables contenant diverses informations
    public String S_Adresse;
    public String S_Debut;
    public String S_Fin;
    public String S_Port;
    public int I_NbrPort;
    public boolean EnPause;

    // Les views
    EditText ET_AdresseSousReseau;
    EditText ET_DebutPlage;
    EditText ET_FinPlage;
    EditText ET_NumeroPort;
    ProgressBar PB_LoadBar;
    TextView TV_Liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_doge);

        // Chercher les View de l'interface pour les utiliser dans le code Java
        ET_AdresseSousReseau = (EditText) findViewById(R.id.editText1);
        ET_DebutPlage = (EditText) findViewById(R.id.editText2);
        ET_FinPlage = (EditText) findViewById(R.id.editText3);
        ET_NumeroPort = (EditText) findViewById(R.id.editText4);
        PB_LoadBar = (ProgressBar) findViewById(R.id.progressBar);
        TV_Liste = (TextView) findViewById(R.id.textView5);
    }


    // fonction appelé lors du clic sur le bouton demarrer
    public void Demarrer(View v) {
        // verifie si l'utilsateur a bien rempli les champs
        if(!VerifET())
        {
            Toast message = Toast.makeText(leDoge.this,
                    "Il manque des informations pour commencer", Toast.LENGTH_SHORT);
            message.show();
        }
        else
        {
            // on va chercher les informations qu'il a écrit
            S_Adresse = ET_AdresseSousReseau.getText().toString();
            S_Debut = ET_DebutPlage.getText().toString();
            S_Fin = ET_FinPlage.getText().toString();
            S_Port = ET_NumeroPort.getText().toString();
            I_NbrPort = Integer.parseInt(S_Fin) - Integer.parseInt(S_Debut);


            if (VerifierValeur())
            {
                // on commence la recherche
                EnPause = false;
                LaRecherche uneRecherche = new LaRecherche();
                uneRecherche.execute();
            }
        }
    }

    public boolean VerifierValeur() {
        boolean bonneValeur = true;
        String message = "";

        if (Integer.parseInt(S_Fin) <= Integer.parseInt(S_Debut)) {
            bonneValeur = false;
            message = "La borne supérieure doit être plus grande que la borne inférieure";
        }
        else if (Integer.parseInt(S_Fin) < 2 || Integer.parseInt(S_Debut) < 2 )
        {
            bonneValeur = false;
            message = "Seule une valeur plus haute ou égale a 2 peut être entrée";
        }
        else if (Integer.parseInt(S_Fin) > 254 || Integer.parseInt(S_Debut) > 254)
        {
            bonneValeur = false;
            message = "Seule une valeur plus basse ou égale a 254 peut être entrée";
        }

        if(!bonneValeur)
        {
            Toast Unmessage = Toast.makeText(leDoge.this, message, Toast.LENGTH_LONG);
            Unmessage.show();
        }

        return bonneValeur;
    }

    public void Suspendre(View v) {
        EnPause = true;
    }


    private class LaRecherche extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {
            // ici on stock l'ip qui se connecte pour l'afficher plus tard
            String IPTrouve;
            // boucle pour parcourir chaque ip adresse dans la plage donné par l'utilisateur
            for (int i = Integer.parseInt(S_Debut); i <= Integer.parseInt(S_Fin); i++) {
                IPTrouve = "";
                // on verifie chaque adresse ip
                if (lePortEstOuvert(S_Adresse + "." + i, Integer.parseInt(S_Port), LETIMEOUT)) {
                    // on la stock dans notre string pour l'afficher
                    IPTrouve = S_Adresse + "." + i;
                }

                // Si jamais l'utilisateur decide de suspendre la recherche
                // On fait qque chose d'archaique (un while true) jusqu'a sque l'utilisateur reappuie sur le bouton
                while (EnPause) {
                    // on met le thread en pause
                    try {
                        Thread.sleep(ENPAUSE);
                    } catch (Exception exc) {

                    }
                }

                //On envoit l'adresse IP qui a été trouvé + la derniere adresse testé
                publishProgress(i + "", IPTrouve);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... valeurs) {
            super.onProgressUpdate(valeurs);
            // Dans Valeurs[0] se trouve l'endroit ou on est rendu
            // Dans Valeurs[1] se trouve l'adresse IP complete pour l'affichage

            // Pour trouver le progres qu'on est rendu il faut faire un produit croisé
            // Progres = ( Valeurs[0] - IPDebut ) * 100 / Nbre IP a chercher
            int Progres = (Integer.parseInt(valeurs[0]) - Integer.parseInt(S_Debut)) * 100 / I_NbrPort;
            PB_LoadBar.setProgress(Progres);

            // Ici on ecrit dans le text view si on recoit une reponse a l'adresse envoyer
            if (!valeurs[1].equals("")) {
                TV_Liste.append(valeurs[1] + "\n");
            }
        }

        @Override
        protected void onPostExecute(Void resultat) {
            Toast message = Toast.makeText(leDoge.this,
                    "Recherche terminé !", Toast.LENGTH_SHORT);
            message.show();
        }
    }


    // Fonction qui sert a verifier si le port a une certaine adresse ip est ouvert
    public boolean lePortEstOuvert(String unIP, int unPort, int unTimeout) {
        // variable qui contient notre reponse !
        boolean ouvert = true;
        try {
            // Creer un socket pour verifier si le port est ouvert
            Socket unSocket = new Socket();
            InetSocketAddress unISAddress = new InetSocketAddress(unIP, unPort);
            // On lui met un timeout pour ne pas qu'il cherche pendant trop longtemps
            unSocket.connect(unISAddress, unTimeout);
            unSocket.close();
        } catch (Exception exc) {
            // si on a une exception c'est que le connect n'a pas reussi, donc le port n'est pas ouvert
            ouvert = false;
        }
        // on retourne notre reponse !
        return ouvert;
    }

    // fonction qui sert a verifier si l'utilisateur a rempli les 4 champs
    public boolean VerifET() {
        // variable qui contient notre reponse !
        boolean complet = true;

        if (ET_AdresseSousReseau.getText().toString().equals("") ||
                ET_DebutPlage.getText().toString().equals("") ||
                ET_FinPlage.getText().toString().equals("") ||
                ET_NumeroPort.getText().toString().equals("")) {
            complet = false;
        }

        // on retourne notre reponse !
        return complet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_le_doge, menu);
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
