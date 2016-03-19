package mobcom.iacademy.thesis.group;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import mobcom.iacademy.thesis.MainActivity;
import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.event.adapter.EventAdapter;
import mobcom.iacademy.thesis.event.adapter.EventDecorator;
import mobcom.iacademy.thesis.event.adapter.HighlightWeekendsDecorator;
import mobcom.iacademy.thesis.event.adapter.MySelectorDecorator;
import mobcom.iacademy.thesis.event.adapter.OneDayDecorator;
import mobcom.iacademy.thesis.event.main.EditEventActivity;
import mobcom.iacademy.thesis.event.main.NewEventActivity;
import mobcom.iacademy.thesis.event.main.ShareEventActivity;
import mobcom.iacademy.thesis.model.DayBean;
import mobcom.iacademy.thesis.model.EventBean;
import mobcom.iacademy.thesis.model.GroupBean;

public class GroupInterfaceActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Context context;
    Intent intent;
    private CalendarDay calendarDay;
    GroupBean groupBean;
    private EventAdapter eventAdapter;
    Toolbar toolbar;
    private RecyclerView rv;
    private MaterialCalendarView calendarView;
    private ProgressBar progressBar;
    private List<EventBean> list;
    private DayBean day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_interface);
        intent = this.getIntent();
        if (intent != null) {
            groupBean = new GroupBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"), intent.getStringExtra("id"));
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        init();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(groupBean.getGroupName() + " Events");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInterfaceActivity.this, NewGroupEvent.class);
                intent.putExtra("groupId", groupBean.getId());
                intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                intent.putExtra("groupName", groupBean.getGroupName());
                intent.putExtra("id", groupBean.getGroupId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            intent = new Intent(GroupInterfaceActivity.this, MainActivity.class);
            intent.putExtra("Activity", "New Group");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (id == R.id.action_group_settings) {
            userProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        calendarConfig();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            progressBar.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            new EventSync().executeOnExecutor(Executors.newSingleThreadExecutor());
        } else {
            rv.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            calendarView.setVisibility(View.GONE);
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    GroupInterfaceActivity.this,
                    "Your device appears to be offline. Unable to fetch events.",
                    Toast.LENGTH_LONG).show();
        }

        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        String currentDate = (getMonth(monthNow) + " " + dayNow + ", " + yearNow);
        getSelectedUserEvent(currentDate);

        eventAdapter.SetOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EventBean event = list.get(position);
                Intent intent = new Intent(GroupInterfaceActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", event.getId());
                intent.putExtra("eventTitle", event.getEvent());
                intent.putExtra("eventLocation", event.getLocation());
                intent.putExtra("eventDateStart", event.getDateStart());
                intent.putExtra("eventDateEnd", event.getDateEnd());
                intent.putExtra("eventTimeStart", event.getTimeStart());
                intent.putExtra("eventTimeEnd", event.getTimeEnd());
                intent.putExtra("eventContent", event.getDescription());
                intent.putExtra("eventOwner", event.getUsername());
                //startActivity(intent);
                viewEvent(event.getId(),
                        event.getEvent(),
                        event.getLocation(),
                        event.getDateStart(),
                        event.getDateEnd(),
                        event.getTimeStart(),
                        event.getTimeEnd(),
                        event.getDescription(),
                        event.getUsername(),
                        event.getDay(),
                        event.getMonth(),
                        event.getYear(),
                        event.isAllDay());


            }
        });
    }

    private void viewEvent(final String eventId, final String eventTitle, final String eventLocation, final String dateStart, final String dateEnd, final String timeStart, final String timeEnd, final String description, final String owner, final int day, final int month, final int year, final boolean allDay) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(eventTitle);
        alertDialog.setMessage("Description: " + description + "\n"
                + "Date: " + dateStart + " - " + dateEnd + "\n"
                + "Time: " + timeStart + " - " + timeEnd + "\n"
                + "Location: " + eventLocation + "\n"
                + "Owner: " + owner);

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GroupInterfaceActivity.this, "Edit", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", null);

        alertDialog.show();
    }

    private void calendarConfig() {
        calendarView.setOnDateChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_NONE);
        calendarView.setCalendarDisplayMode(CalendarMode.MONTHS);
        Calendar calendar = Calendar.getInstance();
        calendarView.setSelectedDate(calendar.getTime());

        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        calendarView.setMinimumDate(calendar.getTime());
        calendarView.addDecorators(
                new MySelectorDecorator(GroupInterfaceActivity.this),
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );
    }

    private class EventSync extends AsyncTask<Void, Void, List<CalendarDay>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            goToday();

        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final ArrayList<CalendarDay> dates = new ArrayList<>();
            //fetch events in background


            ParseQuery<ParseObject> userGroup = ParseQuery.getQuery("GroupEvent");
            userGroup.whereEqualTo("groupId", groupBean.getGroupId());
            userGroup.whereEqualTo("isCompleted", false);
            userGroup.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    calendarView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    dates.clear();
                    for (ParseObject days : objects) {
                        day = new DayBean(days.getObjectId(), days.getString("groupEvent"), days.getInt("year"), days.getInt("month"), days.getInt("day"));
                        calendarDay = CalendarDay.from(day.getYear(), day.getMonth(), day.getDayNow());
                        dates.add(calendarDay);
                        calendarView.addDecorator(new EventDecorator(Color.RED, dates));
                    }
                }
            });


            return dates;
        }
    }

    private String getSelectedDatesString(CalendarDay selectedDay) {
        selectedDay = calendarView.getSelectedDate();
        if (day == null) {
            return "No Selection";
        }
        return FORMATTER.format(selectedDay.getDate());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        String selectedDate = getSelectedDatesString(date);
        getSelectedUserEvent(selectedDate);
    }

    private void getSelectedUserEvent(String event) {

        list = new ArrayList<>();
        eventAdapter = new EventAdapter(list);
        rv.setAdapter(eventAdapter);
        ParseQuery<ParseObject> userGroup = ParseQuery.getQuery("GroupEvent");
        userGroup.whereEqualTo("dateStart", event);
        userGroup.whereEqualTo("groupId", groupBean.getGroupId());
        userGroup.whereEqualTo("isCompleted", false);
        userGroup.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                list.clear();
                if (e == null) {
                    for (ParseObject userevent : objects) {
                        EventBean eventsUser = new EventBean(userevent.getObjectId(), userevent.getString("groupEvent"), userevent.getString("description"), userevent.getString("timeStart"), userevent.getString("timeEnd"), userevent.getString("location"), userevent.getString("dateStart"), userevent.getString("dateEnd"), userevent.getString("username"));
                        list.add(eventsUser);
                    }
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Parse Error: ", e.getMessage());
                }
            }
        });
    }

    private void userProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInterfaceActivity.this);
        builder.setTitle("Group Settings");
        String[] types = {"Share Your Events", "View Members", "Leave Group"};
        builder.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        Intent intent = new Intent(GroupInterfaceActivity.this, ShareGroupEventActivity.class);
                        intent.putExtra("groupId", groupBean.getId());
                        intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                        intent.putExtra("groupName", groupBean.getGroupName());
                        intent.putExtra("id", groupBean.getGroupId());
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(GroupInterfaceActivity.this, RemoveMemberActivity.class);
                        intent.putExtra("groupId", groupBean.getId());
                        intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                        intent.putExtra("groupName", groupBean.getGroupName());
                        intent.putExtra("id", groupBean.getGroupId());
                        startActivity(intent);
                        break;
                    case 2:
                        leaveGroup();
                        break;
                }
            }
        });
        builder.show();
    }

    public void goToday() {
        Calendar calendar = Calendar.getInstance();
        calendarView.setCurrentDate(calendar);
        calendarView.setSelectedDate(calendar);
    }


    private void leaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.group_leave));
        builder.setPositiveButton(R.string.delete_task_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = new ProgressDialog(GroupInterfaceActivity.this);
                progressDialog.setMessage(getString(R.string.group_leave_loading));
                progressDialog.show();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
                query.whereEqualTo("username", groupBean.getGroupAdmin());
                query.whereEqualTo("usergroup", groupBean.getGroupName());
                query.getInBackground(groupBean.getId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            parseObject.put("isDeleted", true);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    progressDialog.cancel();
                                    intent = new Intent(GroupInterfaceActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("Activity", "New Group");
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        }).setNegativeButton(R.string.delete_task_cancel, null).show();
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

}
