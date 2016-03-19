package mobcom.iacademy.thesis.event.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.MainActivity;
import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.DayBean;
import mobcom.iacademy.thesis.model.EventBean;
import mobcom.iacademy.thesis.model.GroupBean;
import mobcom.iacademy.thesis.model.GroupMember;
import mobcom.iacademy.thesis.utilities.GroupAdapter;
import mobcom.iacademy.thesis.utilities.GroupHolder;

public class ShareEventActivity extends AppCompatActivity {

    ListView mainListView;
    ProgressBar progressBar;
    private ArrayAdapter<GroupMember> listAdapter;
    Intent intent;
    GroupMember member;
    EventBean event;
    private ArrayList<GroupBean> group = new ArrayList<>();
    final ArrayList<GroupMember> groupMembersList = new ArrayList<>();
    private GroupMember[] groupMembers;
    DayBean day;
    Boolean flag = false;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event);
        emptyView = (TextView) findViewById(R.id.empty);
        mainListView = (ListView) findViewById(R.id.mainListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        emptyView.setVisibility(View.GONE);
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

        populateListView();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ShareEventActivity.this);
                progressDialog.setMessage(getString(R.string.event_share_loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if ((ni != null) && (ni.isConnected())) {
                    for (int x = 0; x < groupMembersList.size(); x++) {
                        member = groupMembersList.get(x);
                        if (member.isChecked()) {
                            //AddNewGroupEvent
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupEvent");
                            query.whereEqualTo("groupId", member.getGroupId());
                            query.whereEqualTo("dateStart", event.getDateStart());
                            query.whereEqualTo("dateEnd", event.getDateEnd());
                            query.whereEqualTo("timeStart", event.getTimeStart());
                            query.whereEqualTo("timeEnd", event.getTimeEnd());
                            query.whereEqualTo("isCompleted", false);
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject userevent, ParseException e) {
                                    if (e == null) {
                                        progressDialog.cancel();
                                        EventBean eventUser = new EventBean(userevent.getObjectId(),
                                                userevent.getString("groupEvent"), userevent.getString("description"),
                                                userevent.getString("timeStart"), userevent.getString("timeEnd"),
                                                userevent.getString("location"), userevent.getString("dateStart"),
                                                userevent.getString("dateEnd"), userevent.getString("username"));

                                        progressDialog.dismiss();
                                        showSameEvent(eventUser.getEvent(), eventUser.getLocation(), eventUser.getDateStart(), eventUser.getTimeStart(), eventUser.getTimeEnd());
                                    } else {
                                        progressDialog.cancel();
                                        ParseObject groupEvent = new ParseObject("GroupEvent");
                                        groupEvent.put("eventId", event.getId());
                                        groupEvent.put("groupId", member.getGroupId());
                                        groupEvent.put("groupName", member.getGroupName());
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
                                                progressDialog.cancel();
                                                intent = new Intent(ShareEventActivity.this, MainActivity.class);
                                                intent.putExtra("Activity", "NewEvent");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(ShareEventActivity.this, "Event Successfully Shared", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }

                } else {
                    //no internet
                    Toast.makeText(ShareEventActivity.this, "Your device appears to be offline. Unable to share event.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        toolbar.setTitle(R.string.event_share_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void populateListView() {
        progressBar.setVisibility(View.VISIBLE);
        groupMembersList.clear();
        // When item is tapped, toggle checked properties of CheckBox and GroupMember.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                GroupMember groupMembers = listAdapter.getItem(position);
                groupMembers.toggleChecked();
                GroupHolder viewHolder = (GroupHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(groupMembers.isChecked());
            }
        });

        // Create and populate GroupMembers.
        groupMembers = (GroupMember[]) getLastCustomNonConfigurationInstance();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("isDeleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                progressBar.setVisibility(View.GONE);
                groupMembersList.clear();
                for (ParseObject post : list) {
                    GroupMember member = new GroupMember(post.getObjectId(), post.getString("usergroup"), post.getString("groupId"), false);
                    groupMembersList.add(member);
                }
                listAdapter.notifyDataSetChanged();

                if(list.size() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
            }
        });

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new GroupAdapter(this, groupMembersList);
        mainListView.setAdapter(listAdapter);

    }

    private void populateTextChanged(String searchedUser) {
        progressBar.setVisibility(View.VISIBLE);
        groupMembersList.clear();
        // When item is tapped, toggle checked properties of CheckBox and GroupMember.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                GroupMember groupMembers = listAdapter.getItem(position);
                groupMembers.toggleChecked();
                GroupHolder viewHolder = (GroupHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(groupMembers.isChecked());
            }
        });

        groupMembers = (GroupMember[]) getLastCustomNonConfigurationInstance();

        // Create and populate GroupMembers.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("isDeleted", false);
        query.whereStartsWith("usergroup", searchedUser.toUpperCase());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    groupMembersList.clear();
                    for (ParseObject post : list) {
                        GroupMember member = new GroupMember(post.getObjectId(), post.getString("usergroup"), post.getString("groupId"), false);
                        groupMembersList.add(member);
                    }
                    listAdapter.notifyDataSetChanged();
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new GroupAdapter(this, groupMembersList);
        mainListView.setAdapter(listAdapter);
    }

    private void populateSearch(String searchedUser) {
        progressBar.setVisibility(View.VISIBLE);
        groupMembersList.clear();
        // When item is tapped, toggle checked properties of CheckBox and GroupMember.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                GroupMember groupMembers = listAdapter.getItem(position);
                groupMembers.toggleChecked();
                GroupHolder viewHolder = (GroupHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(groupMembers.isChecked());
            }
        });

        groupMembers = (GroupMember[]) getLastCustomNonConfigurationInstance();

        // Create and populate GroupMembers.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("isDeleted", false);
        query.whereEqualTo("usergroup", searchedUser.toUpperCase());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    groupMembersList.clear();
                    for (ParseObject post : list) {
                        GroupMember member = new GroupMember(post.getObjectId(), post.getString("usergroup"), post.getString("groupId"), false);
                        groupMembersList.add(member);
                    }
                    listAdapter.notifyDataSetChanged();
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new GroupAdapter(this, groupMembersList);
        mainListView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                populateSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateTextChanged(newText);
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(ShareEventActivity.this, MainActivity.class);
                intent.putExtra("Activity", "NewEvent");
                Toast.makeText(ShareEventActivity.this, "Event not shared.", Toast.LENGTH_SHORT).show();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

        }
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return groupMembers;
    }
}
