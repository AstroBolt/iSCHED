package mobcom.iacademy.thesis.event.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.event.model.DayBean;
import mobcom.iacademy.thesis.event.model.EventBean;
import mobcom.iacademy.thesis.group.model.GroupMember;
import mobcom.iacademy.thesis.group.utilities.GroupAdapter;

public class TestActivity extends AppCompatActivity {

    ArrayList<GroupMember> groupList = new ArrayList<GroupMember>();
    GroupAdapter dataAdapter = null;
    Intent intent;
    EventBean event;
    GroupMember groupMember;
    DayBean day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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
                    intent.getIntExtra("dayYear", 0),
                    intent.getIntExtra("dayMonth", 0),
                    intent.getIntExtra("dayNow", 0));

            day = new DayBean(intent.getStringExtra("eventId"),
                    intent.getStringExtra("eventTitle"),
                    intent.getIntExtra("dayYear", 0),
                    intent.getIntExtra("dayMonth", 0),
                    intent.getIntExtra("dayNow", 0));
        }
        displayListView();
        onGroupClicked();


    }


    private void displayListView() {
        //Array list of events
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("isDeleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (ParseObject post : list) {
                    groupMember = new GroupMember(post.getObjectId(), post.getString("usergroup"), post.getString("groupId"), false);
                    groupList.add(groupMember);
                }
                dataAdapter.notifyDataSetChanged();

            }
        });


        //create an ArrayAdaptar from the String Array
        dataAdapter = new GroupAdapter(this, groupList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    public void onGroupClicked() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int x = 0; x < groupList.size(); x++) {
                    groupMember = groupList.get(x);
                    if (groupMember.isChecked()) {
                        ParseObject groupEvent = new ParseObject("GroupEvent");
                        groupEvent.put("eventId", event.getId());
                        groupEvent.put("groupId", groupMember.getGroupId());
                        groupEvent.put("groupName", groupMember.getGroupName());
                        groupEvent.put("userId", ParseUser.getCurrentUser().getObjectId());
                        groupEvent.put("username", ParseUser.getCurrentUser().getUsername());
                        groupEvent.put("groupEvent", event.getEvent());
                        groupEvent.put("description", event.getDescription());
                        groupEvent.put("location", event.getLocation());
                        groupEvent.put("year", event.getYear());
                        groupEvent.put("month", event.getMonth());
                        groupEvent.put("day", event.getDay());
                        groupEvent.put("isCompleted", false);
                        groupEvent.put("isSharable", false);
                        groupEvent.put("timeStart", event.getTimeStart());
                        groupEvent.put("timeEnd", event.getTimeEnd());
                        groupEvent.put("dateStart", event.getDateStart());
                        groupEvent.put("dateEnd", event.getDateEnd());
                        groupEvent.put("isAllDay", event.isAllDay());
                        groupEvent.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                }
            }
        });
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

}
