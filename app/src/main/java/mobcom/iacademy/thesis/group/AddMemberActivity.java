package mobcom.iacademy.thesis.group;

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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.GroupBean;
import mobcom.iacademy.thesis.model.GroupMember;
import mobcom.iacademy.thesis.utilities.GroupAdapter;
import mobcom.iacademy.thesis.utilities.GroupHolder;

public class AddMemberActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    GroupBean groupBean;
    GroupMember groupMember;
    private GroupMember[] groupMembers;
    private ArrayAdapter<GroupMember> listAdapter;
    ListView mainListView;
    final ArrayList<GroupMember> groupMembersList = new ArrayList<>();
    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
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
            public void onClick(View view) {
                progressDialog = new ProgressDialog(AddMemberActivity.this);
                progressDialog.setMessage(getString(R.string.group_add_member_loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if ((ni != null) && (ni.isConnected())) {
                    //if internet is true
                    for(int user = 0; user < groupMembersList.size(); user++){
                        groupMember = groupMembersList.get(user);
                        if(groupMember.isChecked()){
                            ParseObject groups = new ParseObject("GroupMembers");
                            groups.put("username", groupMember.getGroupName());
                            groups.put("userId", groupMember.getId());
                            groups.put("groupId", groupBean.getGroupId());
                            groups.put("usergroup", groupBean.getGroupName());
                            groups.put("isAdmin", false);
                            groups.put("isDeleted", false);
                            groups.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(AddMemberActivity.this, "Successfully Added New Members", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    intent = new Intent(AddMemberActivity.this, RemoveMemberActivity.class);
                                    intent.putExtra("intent", "groupname");
                                    intent.putExtra("groupId", groupBean.getId());
                                    intent.putExtra("groupAdmin", groupBean.getGroupAdmin());
                                    intent.putExtra("groupName", groupBean.getGroupName());
                                    intent.putExtra("id", groupBean.getGroupId());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(
                            AddMemberActivity.this,
                            "Your device appears to be offline. Unable to add member/s.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.group_add_member);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(AddMemberActivity.this, RemoveMemberActivity.class);
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

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    for (ParseObject event : list) {
                        GroupMember groupMembers = new GroupMember(event.getObjectId(), event.getString("username"), false);
                        groupMembersList.add(groupMembers);
                    }

                    listAdapter.notifyDataSetChanged();
                } else if (!isNetworkConnected()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
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
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", searchedUser);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    for (ParseObject event : list) {
                        GroupMember groupMembers = new GroupMember(event.getObjectId(), event.getString("username"), false);
                        groupMembersList.add(groupMembers);
                    }

                    listAdapter.notifyDataSetChanged();
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
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereStartsWith("username", searchedUser);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    for (ParseObject event : list) {
                        GroupMember groupMembers = new GroupMember(event.getObjectId(), event.getString("username"), false);
                        groupMembersList.add(groupMembers);
                    }

                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new GroupAdapter(this, groupMembersList);
        mainListView.setAdapter(listAdapter);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return groupMembers;
    }
}
