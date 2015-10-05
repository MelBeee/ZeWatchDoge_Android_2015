package charlielaplante_201356187.hobbitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class Lancement extends AppCompatActivity {
    final String ComteIp = "230.0.0.1";
    final String MordorIp ="230.0.0.2";
    final String IsengardIp ="230.0.0.3";
    Spinner SpinnerDuMillieu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancement);

        CreationDuSpinner();

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
        SpinnerDuMillieu = (Spinner)findViewById(R.id.TerreDuMilieu_Spinner);
        String[] items = new String[]{"Le comté", "Mordor", "Isengard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        SpinnerDuMillieu.setAdapter(adapter);
    }

    private void Connecter(View v)
    {
        String ConnectionIp;

    }
}
