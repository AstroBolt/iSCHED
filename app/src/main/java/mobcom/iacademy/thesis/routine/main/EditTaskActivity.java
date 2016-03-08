package mobcom.iacademy.thesis.routine.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.RoutineBean;
import mobcom.iacademy.thesis.model.TaskBean;

public class EditTaskActivity extends AppCompatActivity implements
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private ArrayList<Integer> selectedDayIndexList = new ArrayList<>();
    private RoutineBean routineBean;
    private Intent intent;
    private Button dueDate, priority, day, timeStart;
    private TextView dueDateTv, priorityTv, dayTv, timeStartTv;
    private EditText taskTitle, taskContent;
    private int dayNow, monthNow, yearNow;
    private String selectedPriority, dateNow, selectedDay, format, timeSet, timeStartFormat, currentDay;
    DecimalFormat df = new DecimalFormat("00");
    private TaskBean note;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        selectedDayIndexList.clear();

        initUI();
        btnOnClickListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.editTask);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                attemptSave();
                break;

            case R.id.action_delete:
                deleteTask();
                break;

            case android.R.id.home:
                intent = new Intent(EditTaskActivity.this, TaskInterfaceActivity.class);
                intent.putExtra("groupId", routineBean.getId());
                intent.putExtra("groupName", routineBean.getRoutineName());
                intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void initUI() {
        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskContent = (EditText) findViewById(R.id.content);
        dueDate = (Button) findViewById(R.id.btnDueDate);
        priority = (Button) findViewById(R.id.btnPriority);
        day = (Button) findViewById(R.id.btnDay);
        timeStart = (Button) findViewById(R.id.btnTimeStart);
        dueDateTv = (TextView) findViewById(R.id.dueDateView);
        priorityTv = (TextView) findViewById(R.id.priorityView);
        dayTv = (TextView) findViewById(R.id.dayView);
        timeStartTv = (TextView) findViewById(R.id.timeStartView);


        intent = this.getIntent();
        if (intent != null) {
            routineBean = new RoutineBean(intent.getStringExtra("groupId"),
                    intent.getStringExtra("groupName"),
                    intent.getStringExtra("groupAdmin"));

            note = new TaskBean(intent.getStringExtra("noteId"),
                    intent.getStringExtra("noteTitle"),
                    intent.getStringExtra("noteContent"),
                    intent.getStringExtra("noteDate"),
                    intent.getStringExtra("notePriority"),
                    intent.getStringExtra("noteUsername"),
                    intent.getStringExtra("noteGroupName"),
                    intent.getStringExtra("timeStart"));


            currentDay = intent.getStringExtra("selectedDay");

        }


        dateNow = note.getDueDate();
        selectedPriority = note.getPriority();
        timeStartFormat = note.getTimeStart();

        dayTv.setText(currentDay);
        taskTitle.setText(note.getTitle());
        taskContent.setText(note.getContent());
        dueDateTv.setText(dateNow);
        priorityTv.setText(selectedPriority);
        timeStartTv.setText(timeStartFormat);

    }

    private void btnOnClickListeners() {
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDueDate();
            }
        });

        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPriority();
            }
        });

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedDay();
            }
        });

        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeStart();
            }
        });
    }


    private void attemptSave() {
        // Reset errors.
        taskTitle.setError(null);
        taskContent.setError(null);

        // Store values at the time of the save task attempt.
        String title = taskTitle.getText().toString();
        String content = taskContent.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid content, if the user entered one.
        if (TextUtils.isEmpty(content)) {
            taskContent.setError(getString(R.string.error_field_required));
            focusView = taskContent;
            cancel = true;
        }

        // Check for a valid title
        if (TextUtils.isEmpty(title)) {
            taskTitle.setError(getString(R.string.error_field_required));
            focusView = taskTitle;
            cancel = true;
        }

        if (TextUtils.isEmpty(dateNow)) {
            dateNow = getString(R.string.noData);
        }

        if (TextUtils.isEmpty(selectedPriority)) {
            selectedPriority = getString(R.string.noData);
        }

        if (TextUtils.isEmpty(selectedDay)) {
            selectedDay = getString(R.string.noData);
        }

        if (TextUtils.isEmpty(timeStartFormat)) {
            timeStartFormat = getString(R.string.noData);
        }

        if (cancel) {
            // There was an error; don't attempt save and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress dialog, and kick off a background task to
            // perform the save task attempt.

            saveTask();
        }
    }


    private void deleteTask() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(EditTaskActivity.this);
        alBuilder.setMessage(R.string.delete_task_message)
                .setCancelable(false)
                .setPositiveButton(R.string.delete_task_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        //check if internet is connected
                        if ((ni != null) && (ni.isConnected())) {
                            progressDialog = new ProgressDialog(EditTaskActivity.this);
                            progressDialog.setMessage(getString(R.string.delete_task_loading));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Routine");
                            query.whereEqualTo("routineGroup", routineBean.getRoutineName());
                            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                            query.whereEqualTo("isCompleted", false);
                            query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    parseObject.put("isCompleted", true);
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            progressDialog.cancel();
                                            intent = new Intent(EditTaskActivity.this, TaskInterfaceActivity.class);
                                            intent.putExtra("groupId", routineBean.getId());
                                            intent.putExtra("groupName", routineBean.getRoutineName());
                                            intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        } else {
                            //if no internet connection
                            progressDialog.cancel();
                            Toast.makeText(EditTaskActivity.this, "Your device appears to be offline. Unable to delete task.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).setNegativeButton(R.string.delete_task_cancel, null).show();


    }


    private void saveTask() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.updateTask));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Routine");
            query.whereEqualTo("routineGroup", routineBean.getRoutineName());
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("isCompleted", false);
            query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                    parseObject.put("timeStart", timeStartFormat);
                    parseObject.put("routineGroup", routineBean.getId());
                    parseObject.put("Title", taskTitle.getText().toString());
                    parseObject.put("Content", taskContent.getText().toString());
                    parseObject.put("DueDate", dateNow);
                    parseObject.put("Priority", selectedPriority);
                    parseObject.put("isCompleted", false);
                    parseObject.addAllUnique("SelectedDay", selectedDayIndexList);
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                progressDialog.cancel();
                                intent = new Intent(EditTaskActivity.this, TaskInterfaceActivity.class);
                                intent.putExtra("groupId", routineBean.getId());
                                intent.putExtra("groupName", routineBean.getRoutineName());
                                intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                progressDialog.cancel();
                                Log.d("Parse Error", e.getMessage());
                            }
                        }
                    });
                }
            });

        } else {
            progressDialog.cancel();
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    this,
                    "Your device appears to be offline. Unable to save task.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getDueDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(EditTaskActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.setThemeDark(true);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                dayNow = dayOfMonth;
                monthNow = monthOfYear + 1;
                yearNow = year;
                dateNow = (getMonth(monthNow) + "/" + dayNow + "/" + yearNow);
                dueDateTv.setText(dateNow);
                //insert next function here
                getTimeStart();
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    private void getPriority() {
        final CharSequence myPriority[] = {"Low", "Medium", "High"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
        builder.setSingleChoiceItems(myPriority, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPriority = myPriority[which].toString();
            }
        }).setPositiveButton(R.string.dialogOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                priorityTv.setText(selectedPriority);
                //insert next function
                getDueDate();
            }
        }).setNegativeButton(R.string.dialogCancel, null).show();
    }

    private void getSelectedDay() {
        final CharSequence myDays[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
        builder.setTitle(R.string.selectDay).setSingleChoiceItems(myDays, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDay = myDays[which].toString();
                selectedDayIndexList.add(which);
            }
        }).setPositiveButton(R.string.dialogOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dayTv.setText(selectedDay);
                //insert next function
                getPriority();
            }
        }).setNegativeButton(R.string.dialogCancel, null).show();
    }

    private void getTimeStart() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(EditTaskActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.vibrate(true);
        tpd.enableSeconds(false);
        tpd.setTitle(getString(R.string.timeStart));
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
                timeStartFormat = timeSet + " " + format;
                timeStartTv.setText(timeStartFormat);
            }
        });
        tpd.setOnCancelListener(null);
        tpd.show(getFragmentManager(), "Timepickerdialog1");
    }

    private String getMonth(int month) {
        String currentMonth = "";
        switch (month) {
            case 1:
                currentMonth = "Jan";
                break;
            case 2:
                currentMonth = "Feb";
                break;
            case 3:
                currentMonth = "Mar";
                break;
            case 4:
                currentMonth = "Apr";
                break;
            case 5:
                currentMonth = "May";
                break;
            case 6:
                currentMonth = "Jun";
                break;
            case 7:
                currentMonth = "Jul";
                break;
            case 8:
                currentMonth = "Aug";
                break;
            case 9:
                currentMonth = "Sep";
                break;
            case 10:
                currentMonth = "Oct";
                break;
            case 11:
                currentMonth = "Nov";
                break;
            case 12:
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
