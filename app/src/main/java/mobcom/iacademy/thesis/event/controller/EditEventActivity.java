package mobcom.iacademy.thesis.event.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.util.Calendar;

import mobcom.iacademy.thesis.controller.MainActivity;
import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.event.model.DayBean;
import mobcom.iacademy.thesis.event.model.EventBean;

public class EditEventActivity extends AppCompatActivity implements
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private Button btnTimeStart, btnTimeEnd, btnDateStart, btnDateEnd;
    private TextView timeStartView, timeEndView, dateStartView, dateEndView;
    private EditText eventTitle, eventLocation, eventContent;
    private CheckBox checkBox;
    private boolean allDay = false;
    private Intent intent;
    private int dayNow, monthNow, yearNow;
    private String dateStart, dateEnd, format, timeStart, timeSet, timeEnd, title, location, content;
    DecimalFormat df = new DecimalFormat("00");
    private EventBean event;
    private DayBean day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        intent = this.getIntent();
        if (intent != null) {
            event = new EventBean(intent.getStringExtra("eventId"),
                    intent.getStringExtra("eventTitle"),
                    intent.getStringExtra("eventContent"),
                    intent.getStringExtra("eventTimeStart"),
                    intent.getStringExtra("eventTimeEnd"),
                    intent.getStringExtra("eventLocation"),
                    intent.getStringExtra("eventDateStart"),
                    intent.getStringExtra("eventDateEnd"),
                    intent.getStringExtra("eventOwner"),
                    intent.getBooleanExtra("isAllDay", false),
                    intent.getIntExtra("dayYear", 0), intent.getIntExtra("dayMonth", 0), intent.getIntExtra("dayNow", 0));

            day = new DayBean(intent.getStringExtra("eventId"), intent.getStringExtra("eventTitle"), intent.getIntExtra("dayYear", 0), intent.getIntExtra("dayMonth", 0), intent.getIntExtra("dayNow", 0));
        }

        initUI();
        buttonActions();
        toolbar.setTitle(R.string.event_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            intent = new Intent(EditEventActivity.this, MainActivity.class);
            intent.putExtra("Activity", "NewEvent");
            Toast.makeText(EditEventActivity.this, "Event not Saved.", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_save) {
            attemptSave();
        }
        if (id == R.id.action_delete) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.event_delete);
            alertDialogBuilder.setMessage(R.string.event_delete_message);
            alertDialogBuilder.setPositiveButton(getString(R.string.delete_task_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEvent();
                }
            }).setNegativeButton(R.string.delete_task_cancel, null).show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        eventTitle = (EditText) findViewById(R.id.eventTitle);
        eventContent = (EditText) findViewById(R.id.eventContent);
        eventLocation = (EditText) findViewById(R.id.eventLocation);
        checkBox = (CheckBox) findViewById(R.id.cbAllDay);
        btnTimeStart = (Button) findViewById(R.id.btnTimeStart);
        btnDateStart = (Button) findViewById(R.id.btnDateStart);
        btnTimeEnd = (Button) findViewById(R.id.btnTimeEnd);
        btnDateEnd = (Button) findViewById(R.id.btnDateEnd);
        dateStartView = (TextView) findViewById(R.id.dateStartView);
        timeStartView = (TextView) findViewById(R.id.timeStartView);
        dateEndView = (TextView) findViewById(R.id.dateEndView);
        timeEndView = (TextView) findViewById(R.id.timeEndView);


        checkBox.setChecked(event.isAllDay());
        eventTitle.setText(event.getEvent());
        eventContent.setText(event.getDescription());
        eventLocation.setText(event.getLocation());
        dateStartView.setText(event.getDateStart());
        dateEndView.setText(event.getDateEnd());
        timeStartView.setText(event.getTimeStart());
        timeEndView.setText(event.getTimeEnd());
    }

    private void buttonActions() {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    allDay = true;
                    btnTimeStart.setEnabled(false);
                    btnTimeStart.setBackgroundColor(Color.GRAY);
                    btnTimeEnd.setEnabled(false);
                    btnTimeEnd.setBackgroundColor(Color.GRAY);
                    timeStartView.setText(R.string.all_day_start);
                    timeEndView.setText(R.string.all_day_end);
                } else {
                    allDay = false;
                    btnTimeStart.setEnabled(true);
                    btnTimeStart.setBackgroundColor(Color.parseColor("#3F51B5"));
                    btnTimeEnd.setEnabled(true);
                    btnTimeEnd.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
            }
        });

        btnDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateStart();
            }
        });

        btnDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateEnd();
            }
        });

        btnTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeStart();
            }
        });

        btnTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeEnd();
            }
        });
    }

    private void getDateStart() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(EditEventActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.setTitle(getString(R.string.event_date_start));
        dpd.setThemeDark(true);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                dayNow = dayOfMonth;
                monthNow = monthOfYear;
                yearNow = year;
                dateStart = (getMonth(monthNow) + " " + dayNow + ", " + yearNow);
                dateStartView.setText(dateStart);
                event.setDay(dayOfMonth);
                event.setMonth(monthOfYear);
                event.setYear(year);
                //insert next function here
                getDateEnd();
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getDateEnd() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(EditEventActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.setTitle(getString(R.string.event_date_end));
        dpd.setThemeDark(true);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                dayNow = dayOfMonth;
                monthNow = monthOfYear;
                yearNow = year;
                dateEnd = (getMonth(monthNow) + " " + dayNow + ", " + yearNow);
                dateEndView.setText(dateEnd);
                //insert next function here
                getTimeStart();
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getTimeStart() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(EditEventActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.vibrate(true);
        tpd.enableSeconds(false);
        tpd.setTitle(getString(R.string.event_time_start));
        tpd.setThemeDark(true);
        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                int hour = hourOfDay;

                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                timeSet = String.valueOf(df.format(hour) + ":" + String.valueOf(df.format(minute)));
                timeStart = timeSet + " " + format;
                timeStartView.setText(timeStart);
                //insert next function here
                getTimeEnd();

            }
        });
        tpd.setOnCancelListener(null);
        tpd.show(getFragmentManager(), "Timepickerdialog1");
    }

    private void getTimeEnd() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(EditEventActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.vibrate(true);
        tpd.enableSeconds(false);
        tpd.setTitle(getString(R.string.event_time_end));
        tpd.setThemeDark(true);
        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                int hour = hourOfDay;

                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                timeSet = String.valueOf(df.format(hour) + ":" + String.valueOf(df.format(minute)));
                timeEnd = timeSet + " " + format;
                timeEndView.setText(timeEnd);

                //insert next function here

            }
        });
        tpd.setOnCancelListener(null);
        tpd.show(getFragmentManager(), "Timepickerdialog1");
    }


    private void attemptSave() {
        // Reset errors.
        eventTitle.setError(null);
        eventContent.setError(null);
        eventLocation.setError(null);

        // Store values at the time of the save task attempt.
        title = eventTitle.getText().toString();
        content = eventContent.getText().toString();
        location = eventLocation.getText().toString();
        dateStart = dateStartView.getText().toString();
        dateEnd = dateEndView.getText().toString();
        timeStart = timeStartView.getText().toString();
        timeEnd = timeEndView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid content, if the user entered one.
        if (TextUtils.isEmpty(content)) {
            eventContent.setError(getString(R.string.error_field_required));
            focusView = eventContent;
            cancel = true;
        }

        // Check for a valid title
        if (TextUtils.isEmpty(title)) {
            eventTitle.setError(getString(R.string.error_field_required));
            focusView = eventTitle;
            cancel = true;
        }

        //check for a valid location
        if (TextUtils.isEmpty(location)) {
            eventLocation.setError(getString(R.string.error_field_required));
            focusView = eventLocation;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt save and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress dialog, and kick off a background task to
            // perform the save task attempt.
            updateEvent(title, content, location, timeStart, timeEnd, dateStart, dateEnd);

        }
    }

    private void updateEvent(final String title, final String content, final String location, final String timeStart, final String timeEnd, final String dateStart, final String dateEnd){
        final ProgressDialog progressDialog = new ProgressDialog(EditEventActivity.this);
        progressDialog.setMessage(getString(R.string.event_update_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            query.getInBackground(event.getId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.put("username", ParseUser.getCurrentUser().getObjectId());
                    parseObject.put("event", title);
                    parseObject.put("description", content);
                    parseObject.put("location", location);
                    parseObject.put("year", event.getYear());
                    parseObject.put("month", event.getMonth());
                    parseObject.put("day", event.getDay());
                    parseObject.put("isCompleted", false);
                    parseObject.put("isSharable", false);
                    if (allDay == false) {
                        parseObject.put("isAllDay", false);
                        parseObject.put("timeStart", timeStart);
                        parseObject.put("timeEnd", timeEnd);
                        parseObject.put("dateStart", dateStart);
                        parseObject.put("dateEnd", dateEnd);
                    } else if (allDay == true) {
                        parseObject.put("isAllDay", true);
                        parseObject.put("timeStart", "11:59PM");
                        parseObject.put("timeEnd", "12:01AM");
                        parseObject.put("dateStart", dateStart);
                        parseObject.put("dateEnd", dateEnd);
                    }

                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.cancel();
                            intent = new Intent(EditEventActivity.this, MainActivity.class);
                            intent.putExtra("Activity", "NewEvent");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(EditEventActivity.this, "Event Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            progressDialog.cancel();
            Toast.makeText(EditEventActivity.this, "Your device appears to be offline. Unable to update event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEvent(){
        final ProgressDialog progressDialog = new ProgressDialog(EditEventActivity.this);
        progressDialog.setMessage(getString(R.string.event_delete_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            query.getInBackground(event.getId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.put("isCompleted", true);
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.cancel();
                            intent = new Intent(EditEventActivity.this, MainActivity.class);
                            intent.putExtra("Activity", "New Event");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(EditEventActivity.this, "Event Successfully Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            progressDialog.cancel();
            Toast.makeText(EditEventActivity.this, "Your device appears to be offline. Unable to delete event.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMonth(int month) {
        String currentMonth = "";
        switch (month) {
            case 0:
                currentMonth = "Jan";
                break;
            case 1:
                currentMonth = "Feb";
                break;
            case 2:
                currentMonth = "Mar";
                break;
            case 3:
                currentMonth = "Apr";
                break;
            case 4:
                currentMonth = "May";
                break;
            case 5:
                currentMonth = "Jun";
                break;
            case 6:
                currentMonth = "Jul";
                break;
            case 7:
                currentMonth = "Aug";
                break;
            case 8:
                currentMonth = "Sep";
                break;
            case 9:
                currentMonth = "Oct";
                break;
            case 10:
                currentMonth = "Nov";
                break;
            case 11:
                currentMonth = "Dec";
                break;
        }
        return currentMonth;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        //null
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //null
    }
}
