package hackday.com.bloodpressure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SenderAcitivity extends AppCompatActivity {

    static TextView time;
    static TextView date;
    EditText sys;
    EditText dia;
    String userid, sys_str, dia_str, date_str, time_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sys=(EditText)findViewById(R.id.sys);
        dia=(EditText)findViewById(R.id.dia);
        String userid=sharedPreferences.getString("userid", "error");
        String username=sharedPreferences.getString("username", userid);
        if(username.length()<1)
            username=userid;
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);
        time=(TextView)findViewById(R.id.timetext);
        date=(TextView)findViewById(R.id.datetext);
        Calendar C=Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String times=sdf.format(C.getTime());
        time.setText(times);
        sdf=new SimpleDateFormat("dd/MM/yyyy");
        String dates=sdf.format(C.getTime());
        date.setText(dates);
        Button setDate=(Button)findViewById(R.id.setdate);
        Button setTime=(Button)findViewById(R.id.settime);
        Button submit=(Button)findViewById(R.id.submit);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                DialogFragment newFragment =new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sys_str=sys.getText().toString();
                dia_str=dia.getText().toString();
                date_str=date.getText().toString();
                time_str=time.getText().toString();
                date_str.replace('/','-');
                new Submit().execute();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour=hourOfDay<10?"0"+hourOfDay:""+hourOfDay;
            String minut=minute<10?"0"+minute:""+minute;
            time.setText(hour+":"+minut);

        }
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;
            String days=day<10?"0"+day:""+day;
            String months=month<10?"0"+month:""+month;
            date.setText(days+"/"+months+"/"+year);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Log.d("test","menu works");
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove("userid");
            editor.remove("username");
            editor.commit();
            startActivity(new Intent(SenderAcitivity.this, MainActivity.class));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private class Submit extends AsyncTask {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        ProgressDialog progressDialog;




        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog = new ProgressDialog(SenderAcitivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
           // Boolean authorized=sharedPreferences.getBoolean("authorized",false);
            userid=sharedPreferences.getString("userid", "error");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("patient_id", userid));
            params.add(new BasicNameValuePair("sys", sys_str));
            params.add(new BasicNameValuePair("dia", dia_str));
            params.add(new BasicNameValuePair("date", date_str));
            params.add(new BasicNameValuePair("time", time_str));

            jsonObject = jsonParser.makeHttpRequest("http://bpathome.starswan.com/api/v1/readings", "POST", params);
            //jsonObject = jsonParser.makeHttpRequest("http://128.199.128.192:80/aqparat/allnews.php", "POST", params);


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            Log.d("String json", jsonObject.toString());
//            try {
//                if(jsonObject.getString("hello").length()>4) {
//                    Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();;
//
//                }else {
//                    Toast.makeText(getApplicationContext(), "Network problem, try again", Toast.LENGTH_LONG).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }
    }

}
