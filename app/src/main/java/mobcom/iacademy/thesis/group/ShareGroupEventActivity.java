package mobcom.iacademy.thesis.group;

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

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.event.main.EditEventActivity;
import mobcom.iacademy.thesis.menu.Group;
import mobcom.iacademy.thesis.model.DayBean;
import mobcom.iacademy.thesis.model.EventBean;
import mobcom.iacademy.thesis.model.GroupBean;
import mobcom.iacademy.thesis.model.GroupMember;
import mobcom.iacademy.thesis.utilities.DayAdapter;
import mobcom.iacademy.thesis.utilities.GroupHolder;

public class ShareGroupEventActivity extends AppCompatActivity {

    private TextView emptyView;
    Toolbar toolbar;
    ListView mainListView;
    ProgressDialog progressDialog;
    Intent intent;
    GroupBean groupBean;
    ProgressBar progressBar;
    DayBean day;
    private GroupMember[] groupMembers;
    private DayBean[] eventBeans;
    private ArrayAdapter<EventBean> listAdapter;
    ArrayList<EventBean> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event2);
        emptyView = (TextView) findViewById(R.id.empty);
        emptyView.setVisibility(View.GONE);
        mainListView = (ListView) findViewById(R.id.mainListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        intent = this.getIntent();
        if (intent != null) {
            groupBean = new GroupBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"), intent.getStringExtra("id"));
        }

        populateListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < eventList.size(); i++) {
                    final EventBean eventBean = eventList.get(i);
                    if (eventBean.isChecked()) {
                        final ProgressDialog progressDialog = new ProgressDialog(ShareGroupEventActivity.this);
                        progressDialog.setMessage(getString(R.string.event_share_loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        if ((ni != null) && (ni.isConnected())) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupEvent");
                            query.whereEqualTo("groupId", groupBean.getGroupId());
                            query.whereEqualTo("dateStart", eventBean.getDateStart());
                            query.whereEqualTo("dateEnd", eventBean.getDateEnd());
                            query.whereEqualTo("timeStart", eventBean.getTimeStart());
                            query.whereEqualTo("timeEnd", eventBean.getTimeEnd());
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
                                        groupEvent.put("eventId", eventBean.getId());
                                        groupEvent.put("groupId", groupBean.getGroupId());
                                        groupEvent.put("groupName", groupBean.getGroupName());
                                        groupEvent.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        groupEvent.put("username", ParseUser.getCurrentUser().getUsername());
                                        groupEvent.put("groupEvent", eventBean.getEvent());
                                        groupEvent.put("description", eventBean.getDescription());
                                        groupEvent.put("location", eventBean.getLocation());
                                        groupEvent.put("year", day.getYear());
                                        groupEvent.put("month", day.getMonth());
                                        groupEvent.put("day", day.getDayNow());
                                        groupEvent.put("isCompleted", false);
                                        groupEvent.put("isSharable", false);
                                        groupEvent.put("timeStart", eventBean.getTimeStart());
                                        groupEvent.put("timeEnd", eventBean.getTimeEnd());
                                        groupEvent.put("dateStart", eventBean.getDateStart());
                                        groupEvent.put("dateEnd", eventBean.getDateEnd());
                                        groupEvent.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                progressDialog.cancel();
                                                intent = new Intent(ShareGroupEventActivity.this, GroupInterfaceActivity.class);
                                                intent.putExtra("groupId", groupBean.getId());
                                                intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                                                intent.putExtra("groupName", groupBean.getGroupName());
                                                intent.putExtra("id", groupBean.getGroupId());
                                                startActivity(intent);
                                                Toast.makeText(ShareGroupEventActivity.this, "Event Successfully Shared", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }else{
                            //no internet
                            Toast.makeText(ShareGroupEventActivity.this, "Your device appears to be offline. Unable to share event.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.share_your_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void populateListView() {
        progressBar.setVisibility(View.VISIBLE);
        eventList.clear();

        // When item is tapped, toggle checked properties of CheckBox and GroupMember.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                EventBean userEvent = listAdapter.getItem(position);
                userEvent.toggleChecked();
                GroupHolder viewHolder = (GroupHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(userEvent.isChecked());
            }
        });

        // Create and populate Event List.
        eventBeans = (DayBean[]) getLastCustomNonConfigurationInstance();
        listAdapter = new DayAdapter(this, eventList);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("isCompleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    for (ParseObject userevent : list) {
                        EventBean eventsUser = new EventBean(userevent.getObjectId(), userevent.getString("event"), userevent.getString("description"), userevent.getString("timeStart"), userevent.getString("timeEnd"), userevent.getString("location"), userevent.getString("dateStart"), userevent.getString("dateEnd"), userevent.getString("username"));
                        day = new DayBean(userevent.getObjectId(), userevent.getString("event"), userevent.getInt("year"), userevent.getInt("month"), userevent.getInt("day"));
                        eventList.add(eventsUser);

                    }
                    listAdapter.notifyDataSetChanged();
                    if(list.size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else{
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }
        });

        listAdapter = new DayAdapter(this, eventList);
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
                //populateSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //populateTextChanged(newText);
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(ShareGroupEventActivity.this, GroupInterfaceActivity.class);
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


    public Object onRetainCustomNonConfigurationInstance() {
        return eventBeans;
    }

}
