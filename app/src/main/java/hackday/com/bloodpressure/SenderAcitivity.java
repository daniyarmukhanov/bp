package hackday.com.bloodpressure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment =new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
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

}
