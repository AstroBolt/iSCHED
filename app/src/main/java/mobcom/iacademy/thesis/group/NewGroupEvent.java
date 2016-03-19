package mobcom.iacademy.thesis.group;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import java.util.ArrayList;
import java.util.Calendar;

import mobcom.iacademy.thesis.MainActivity;
import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.EventBean;
import mobcom.iacademy.thesis.model.GroupBean;

public class NewGroupEvent extends AppCompatActivity implements
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private Button btnTimeStart, btnTimeEnd, btnDateStart, btnDateEnd;
    private TextView timeStartView, timeEndView, dateStartView, dateEndView;
    private EditText eventTitle, eventLocation, eventContent;
    private CheckBox checkBox;
    private boolean allDay = false;
    private Intent intent;
    int dayNow, monthNow, yearNow, dayStart, dayEnd;
    String dateStart, dateEnd, format, timeStart, timeSet, timeEnd, title, location, content;
    DecimalFormat df = new DecimalFormat("00");
    ArrayList<Integer> dayOfMonth = new ArrayList<>();
    GroupBean groupBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_event);
        intent = this.getIntent();
        if (intent != null) {
            groupBean = new GroupBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"), intent.getStringExtra("id"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.new_group_event);
        setSupportActionBar(toolbar);

        initUI();
        buttonActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                attemptSave();
                break;

            case R.id.action_cancel:
                intent = new Intent(NewGroupEvent.this, GroupInterfaceActivity.class);
                intent.putExtra("groupId", groupBean.getId());
                intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                intent.putExtra("groupName", groupBean.getGroupName());
                intent.putExtra("id", groupBean.getGroupId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

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

        dateStartView.setText(getCurrentDate());
        dateEndView.setText(getCurrentDate());
        timeStartView.setText(getCurrentTime());
        timeEndView.setText(getCurrentTime());
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
        DatePickerDialog dpd = DatePickerDialog.newInstance(NewGroupEvent.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.setTitle(getString(R.string.event_date_start));
        dpd.setThemeDark(true);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                dayStart = dayOfMonth;
                monthNow = monthOfYear;
                yearNow = year;
                dateStart = (getMonth(monthNow) + " " + dayStart + ", " + yearNow);
                dateStartView.setText(dateStart);
                //insert next function here
                getDateEnd();
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getDateEnd() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(NewGroupEvent.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.setTitle(getString(R.string.event_date_end));
        dpd.setThemeDark(true);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                dayEnd = dayOfMonth;
                monthNow = monthOfYear;
                yearNow = year;
                dateEnd = (getMonth(monthNow) + " " + dayEnd + ", " + yearNow);
                dateEndView.setText(dateEnd);
                //insert next function here
                getTimeStart();
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getTimeStart() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(NewGroupEvent.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
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
        TimePickerDialog tpd = TimePickerDialog.newInstance(NewGroupEvent.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
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
            for (int i = dayStart; i < dayEnd + 1; i++) {
                dayOfMonth.add(i);
            }
            saveEvent(title, content, location, timeStart, timeEnd, dateStart, dateEnd);

        }
    }

    private void saveEvent(final String title, final String content, final String location, final String timeStart, final String timeEnd, final String dateStart, final String dateEnd) {
        final ProgressDialog progressDialog = new ProgressDialog(NewGroupEvent.this);
        progressDialog.setMessage(getString(R.string.new_event_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupEvent");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("location", location);
            query.whereEqualTo("dateStart", dateStart);
            query.whereEqualTo("dateEnd", dateEnd);
            query.whereEqualTo("timeStart", timeStart);
            query.whereEqualTo("timeEnd", timeEnd);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject userevent, ParseException e) {
                    if (e == null) {
                        EventBean eventUser = new EventBean(userevent.getObjectId(),
                                userevent.getString("event"), userevent.getString("description"),
                                userevent.getString("timeStart"), userevent.getString("timeEnd"),
                                userevent.getString("location"), userevent.getString("dateStart"),
                                userevent.getString("dateEnd"), userevent.getString("username"));

                        progressDialog.dismiss();
                        showSameEvent(eventUser.getEvent(), eventUser.getLocation(), eventUser.getDateStart(), eventUser.getTimeStart(), eventUser.getTimeEnd());
                    } else {
                        //if object does not exist
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            ParseObject event = new ParseObject("GroupEvent");
                            event.put("groupId", groupBean.getGroupId());
                            event.put("groupName", groupBean.getGroupName());
                            event.put("userId", ParseUser.getCurrentUser().getObjectId());
                            event.put("username", ParseUser.getCurrentUser().getUsername());
                            event.put("groupEvent", title);
                            event.put("description", content);
                            event.put("location", location);
                            event.put("year", yearNow);
                            event.put("month", monthNow);
                            event.put("day", dayStart);
                            event.put("isCompleted", false);
                            event.put("isSharable", false);
                            if (allDay == false) {
                                event.put("isAllDay", false);
                                event.put("timeStart", timeStart);
                                event.put("timeEnd", timeEnd);
                                event.put("dateStart", dateStart);
                                event.put("dateEnd", dateEnd);
                            } else if (allDay == true) {
                                event.put("isAllDay", true);
                                event.put("timeStart", "11:59PM");
                                event.put("timeEnd", "12:01AM");
                                event.put("dateStart", dateStart);
                                event.put("dateEnd", dateEnd);
                            }
                            event.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        progressDialog.dismiss();
                                        intent = new Intent(NewGroupEvent.this, GroupInterfaceActivity.class);
                                        intent.putExtra("groupId", groupBean.getId());
                                        intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                                        intent.putExtra("groupName", groupBean.getGroupName());
                                        intent.putExtra("id", groupBean.getGroupId());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        Toast.makeText(NewGroupEvent.this, "Event Successfully Created", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }
                }
            });


        } else {
            progressDialog.cancel();
            Toast.makeText(NewGroupEvent.this, "Your device appears to be offline. Unable to save event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSameEvent(String event, String location, String dateStart, String timeStart, String timeEnd) {
        String eventLine = "Event: " + event;
        String locationLine = "Location: " + location;
        String dateLine = "Date: " + dateStart;
        String time = "Time: " + timeStart + " - " + timeEnd;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.duplicate_event);
        alertDialogBuilder.setMessage(eventLine + "\n" + locationLine + "\n" + dateLine + "\n" + time);
        alertDialogBuilder.setPositiveButton(R.string.dialogOk, null).show();
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

    private String getCurrentDate() {
        String currentDate;
        Calendar now = Calendar.getInstance();
        yearNow = now.get(Calendar.YEAR);
        monthNow = now.get(Calendar.MONTH);
        dayNow = now.get(Calendar.DAY_OF_MONTH);
        currentDate = (getMonth(monthNow) + " " + dayNow + ", " + yearNow);
        return currentDate;
    }

    private String getCurrentTime() {
        String currentTime;
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

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

        currentTime = String.valueOf(df.format(hour) + ":" + String.valueOf(df.format(minute)) + " " + format);
        return currentTime;
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
