package hackday.com.bloodpressure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    EditText userid;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.contains("userid")){
            Intent sender =new Intent(MainActivity.this, SenderAcitivity.class);
            startActivity(sender);
        }else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("Blood Pressure At Home");
            Button ok=(Button)findViewById(R.id.ok_button);
            userid=(EditText)findViewById(R.id.patientid);
            name=(EditText)findViewById(R.id.patienname);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("userid", userid.getText().toString());
                    editor.putString("username", name.getText().toString());
                    editor.commit();
                    //Log.d("userid", userid.getText().toString());
                    Intent sender =new Intent(MainActivity.this, SenderAcitivity.class);
                    startActivity(sender);
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
