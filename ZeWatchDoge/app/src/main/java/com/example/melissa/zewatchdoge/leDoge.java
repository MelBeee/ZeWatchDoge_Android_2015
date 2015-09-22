package com.example.melissa.zewatchdoge;

import android.os.AsyncTask;
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

public class leDoge extends AppCompatActivity {

    public String S_Adresse;
    public String S_Debut;
    public String S_Fin;
    public String S_Port;

    LaRecherche uneRecherche = new LaRecherche();

    EditText ET_AdresseSousReseau;
    EditText ET_DebutPlage;
    EditText ET_FinPlage;
    EditText ET_NumeroPort;
    ProgressBar PB_LoadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_doge);

        // Chercher les View de l'interface pour les utiliser dans le code Java
        ET_AdresseSousReseau = (EditText)findViewById(R.id.editText1);
        ET_DebutPlage =        (EditText)findViewById(R.id.editText2);
        ET_FinPlage =          (EditText)findViewById(R.id.editText3);
        ET_NumeroPort =        (EditText)findViewById(R.id.editText4);

        PB_LoadBar =        (ProgressBar)findViewById(R.id.progressBar);
        Button BTN_Demarrer =           (Button)findViewById(R.id.BTN_Start);
        Button BTN_Stop =               (Button)findViewById(R.id.BTN_Stop);

        TextView TV_Liste =             (TextView)findViewById(R.id.textView5);
    }

    public void Demarrer(View v)
    {
        if(VerifET())
        {
            S_Adresse = ET_AdresseSousReseau.getText().toString();
            S_Debut = ET_DebutPlage.getText().toString();
            S_Fin = ET_FinPlage.getText().toString();
            S_Port = ET_NumeroPort.getText().toString();

            CommencerLaRecherche();
        }
        else
        {
            Toast message = Toast.makeText( leDoge.this,
                    "Il manque des informations pour commencer", Toast.LENGTH_SHORT);
            message.show();
        }
    }

    public void CommencerLaRecherche()
    {
        uneRecherche.execute();
    }

    private class LaRecherche extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {

           // publishProgress(progres);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... valeurs) {
            super.onProgressUpdate(valeurs);
            // mise à jour de la barre de progression
            PB_LoadBar.setProgress(valeurs[0]);
        }

        @Override
        protected void onPostExecute(Void resultat) {
        }
    }

    public boolean VerifET()
    {
        boolean complet = true;

        if( ET_AdresseSousReseau.getText().toString().equals("")    ||
            ET_DebutPlage.getText().toString().equals("")           ||
            ET_FinPlage.getText().toString().equals("")             ||
            ET_NumeroPort.getText().toString().equals(""))
        {
            complet = false;
        }

        return complet;
    }

    public void Suspendre(View v)
    {
        uneRecherche.cancel(true);
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
