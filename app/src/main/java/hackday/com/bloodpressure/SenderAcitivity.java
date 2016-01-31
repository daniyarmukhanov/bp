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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
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
    private class CreateOrder extends AsyncTask {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        ProgressDialog progressDialog;
        String email, password,api_password,is_user,text,type,price,name,city,address,phone;



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
            Boolean authorized=sharedPreferences.getBoolean("authorized",false);
            email=sharedPreferences.getString("email","");
            password=authorized?sharedPreferences.getString("password",""):"";
            api_password="artlinesAa!";
            is_user=authorized?"yes":"no";
            text=sharedPreferences.getString("text","");
            type=sharedPreferences.getString("type","");
            price=sharedPreferences.getString("price","");
            name=authorized?"":sharedPreferences.getString("name","");
            city=authorized?"":sharedPreferences.getString("city","");
            address=authorized?"":sharedPreferences.getString("address","");
            phone=authorized?"":sharedPreferences.getString("phone","");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("api_password", api_password));
            params.add(new BasicNameValuePair("is_user", is_user));
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("price", price));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("phone", phone));
            Log.e("FOR CREATE ORDER",email+"\n"+password+"\n"+api_password+"\n"+is_user+"\n"+text+"\n"+type+"\n"+price+"\n"+name+"\n"+city+"\n"+address+"\n"+phone);

            jsonObject = jsonParser.makeHttpRequest(""+"order/APICreate", "POST", params);
            Log.d("Order", jsonObject.toString());

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
            try {
                if(jsonObject.getString("result").equals("success")) {
                    //startActivity(new Intent(Payment.this, Success.class));

                }else {
                    Toast.makeText(getApplicationContext(), "Network problem, try again", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
