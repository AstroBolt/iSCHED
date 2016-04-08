package mobcom.iacademy.thesis.controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.login.Interceptor;
import mobcom.iacademy.thesis.utilities.NavItem;
import mobcom.iacademy.thesis.utilities.NavListAdapter;


public class MainActivity extends AppCompatActivity {

    RelativeLayout profileBox;
    DrawerLayout drawerLayout;
    RelativeLayout drawerPanel;
    ListView lvNav;
    TextView username, email;
    List<NavItem> listNavItems;
    List<Fragment> listFragments;
    Intent intent;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragTran;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profileBox = (RelativeLayout) findViewById(R.id.profile_box);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerPanel = (RelativeLayout) findViewById(R.id.drawer_panel);
        lvNav = (ListView) findViewById(R.id.nav_list);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        //get user credentials
        username = (TextView) findViewById(R.id.profileUsername);
        email = (TextView) findViewById(R.id.profileEmail);
        username.setText(ParseUser.getCurrentUser().getUsername());
        email.setText(ParseUser.getCurrentUser().getEmail());

        listNavItems = new ArrayList<>();
        listNavItems.add(new NavItem("Routines", R.drawable.ic_action_paste));
        listNavItems.add(new NavItem("Events", R.drawable.ic_action_calendar_day));
        listNavItems.add(new NavItem("Groups", R.drawable.ic_action_users));


        NavListAdapter navListAdapter = new NavListAdapter(getApplicationContext(), R.layout.items_nav_list, listNavItems);
        lvNav.setAdapter(navListAdapter);
        listFragments = new ArrayList<>();
        listFragments.add(new RoutineList());
        listFragments.add(new Event());
        listFragments.add(new Group());

        //initial commit
        fragTran = fragmentManager.beginTransaction();
        fragTran.replace(R.id.containerView, new RoutineList());
        fragTran.addToBackStack(null);
        toolbar.setTitle("Routine");
        fragTran.commit();
        lvNav.setItemChecked(0, true);
        drawerLayout.closeDrawer(drawerPanel);

        intent = this.getIntent();
        if (intent.getExtras() != null) {
            String activity = intent.getStringExtra("Activity");
            switch (activity) {
                case "NewEvent":

                    getSupportActionBar().setTitle("Events");
                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(1)).commit();
                    lvNav.setItemChecked(1, true);
                    break;
                case "New Routine":

                    getSupportActionBar().setTitle("Routines");
                    lvNav.setItemChecked(0, true);
                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(0)).commit();
                    break;

                case "New Group":

                    getSupportActionBar().setTitle("Groups");
                    lvNav.setItemChecked(2, true);
                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(2)).commit();
                    break;

            }
        }

        //set listener for navigation items;
        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //replace the fragment with the selection correspondingly
                fragmentManager = getSupportFragmentManager();
                actionBarDrawerToggle.syncState();

                if (position == 0) {
                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(0)).commit();

                    toolbar.setTitle(listNavItems.get(position).getTitle());
                    lvNav.setItemChecked(position, true);
                    drawerLayout.closeDrawer(drawerPanel);
                }
                if (position == 1) {

                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(1)).commit();

                    toolbar.setTitle(listNavItems.get(position).getTitle());
                    lvNav.setItemChecked(position, true);
                    drawerLayout.closeDrawer(drawerPanel);
                }
                if (position == 2) {
                    fragmentManager.beginTransaction().replace(R.id.containerView, listFragments.get(2)).commit();

                    toolbar.setTitle(listNavItems.get(position).getTitle());
                    lvNav.setItemChecked(position, true);
                    drawerLayout.closeDrawer(drawerPanel);
                }


            }
        });


        //create listener for drawer layout
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                actionBarDrawerToggle.syncState();
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                actionBarDrawerToggle.syncState();
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);

            }
        };

        profileBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfile();
            }
        });


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        actionBarDrawerToggle.syncState();

    }

    private void userProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Help and Settings");
        String[] types = {"Change Username", "Reset Password", "Delete Account", "Logout"};
        builder.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        inputDialog();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Reset", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Delete Account");
                        builder.setMessage("Are you sure you want to delete your account?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser user = ParseUser.getCurrentUser();
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage(getString(R.string.action_delete_user));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                user.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        progressDialog.cancel();
                                        ParseUser.logOut();
                                        Toast.makeText(getApplicationContext(), "Thank You for using iSCHED. Have a nice day.",
                                                Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(MainActivity.this, Interceptor.class));
                                    }
                                });
                            }
                        });

                        builder.setNegativeButton("NO", null);
                        builder.show();
                        break;
                    case 3:
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage(getString(R.string.action_logout));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.cancel();
                                startActivity(new Intent(MainActivity.this, Interceptor.class));
                            }
                        });
                        break;
                }
            }

        });
        builder.show();
    }

    private void inputDialog() {

        //get prompts.xml
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);

        //set dialog message
        alBuilder.setMessage("Change Username");

        //set prompts.xml to alert dialog builder
        alBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(ParseUser.getCurrentUser().getUsername());

        alBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                final String userPrompt = userInput.getText().toString();

                if (!TextUtils.isEmpty(userPrompt)) {

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                           if(e == null){
                               parseUser.setUsername(userPrompt);
                               parseUser.saveEventually();
                               Toast.makeText(MainActivity.this, "Username Successfully Updated", Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(MainActivity.this, "Username Not Updated", Toast.LENGTH_SHORT).show();
                           }
                        }
                    });


                } else {
                    Toast.makeText(MainActivity.this, "Missing Fields", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //ACTIONBAR BUTTON ACTIONS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item) || actionBarDrawerToggle.onOptionsItemSelected(item);
    }


    //when back button is pressed we close the app
    @TargetApi(16)
    @Override
    public void onBackPressed() {
        Log.d("Back Button", "Pressed");
        finishAffinity();
    }
}
