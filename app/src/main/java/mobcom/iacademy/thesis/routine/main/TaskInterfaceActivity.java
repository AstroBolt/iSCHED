package mobcom.iacademy.thesis.routine.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import mobcom.iacademy.thesis.MainActivity;
import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.RoutineBean;
import mobcom.iacademy.thesis.routine.adapter.PagerAdapter;

public class TaskInterfaceActivity extends AppCompatActivity {

    private RoutineBean routineBean;
    Intent intent;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    ViewPager viewPager;
    PagerAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_interface);
        initUI();

    }

    private void initUI() {
        intent = this.getIntent();
        if (intent != null) {
            routineBean = new RoutineBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"));
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(routineBean.getRoutineName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("SUN"));
        tabLayout.addTab(tabLayout.newTab().setText("MON"));
        tabLayout.addTab(tabLayout.newTab().setText("TUE"));
        tabLayout.addTab(tabLayout.newTab().setText("WED"));
        tabLayout.addTab(tabLayout.newTab().setText("THU"));
        tabLayout.addTab(tabLayout.newTab().setText("FRI"));
        tabLayout.addTab(tabLayout.newTab().setText("SAT"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskInterfaceActivity.this, NewTaskActivity.class);
                intent.putExtra("groupId", routineBean.getId());
                intent.putExtra("groupName", routineBean.getRoutineName());
                intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            intent = new Intent(TaskInterfaceActivity.this, MainActivity.class);
            intent.putExtra("Activity", "New Routine");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_edit_routine) {
            inputDialog();
            return true;
        }

        if (id == R.id.action_delete_routine) {
            deleteRoutine();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inputDialog() {

        //get prompts.xml
        LayoutInflater li = LayoutInflater.from(TaskInterfaceActivity.this);
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(TaskInterfaceActivity.this);

        //set dialog message
        alBuilder.setMessage(R.string.rename_routine);

        //set prompts.xml to alert dialog builder
        alBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(routineBean.getRoutineName());

        alBuilder.setCancelable(false).setPositiveButton(R.string.dialogOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String userPrompt = userInput.getText().toString();
                        if (!TextUtils.isEmpty(userPrompt)) {
                            //toolbar.setTitle(userPrompt);
                            progressDialog = new ProgressDialog(TaskInterfaceActivity.this);
                            progressDialog.setMessage(getString(R.string.updateRoutine));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo ni = cm.getActiveNetworkInfo();
                            if ((ni != null) && (ni.isConnected())) {
                                //if internet is connected
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutineGroup");
                                query.whereEqualTo("isDeleted", false);
                                query.whereEqualTo("username", routineBean.getRoutineAdmin());
                                query.getInBackground(routineBean.getId(), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        parseObject.put("routineGroup", userPrompt);
                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                progressDialog.cancel();
                                                routineBean.setRoutineName(userPrompt);
                                                toolbar.setTitle(routineBean.getRoutineName());
                                            }
                                        });
                                    }
                                });
                            } else {
                                //if there is no internet connection
                                Toast.makeText(
                                        TaskInterfaceActivity.this,
                                        "Your device appears to be offline. Unable to update routine.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(TaskInterfaceActivity.this, "Missing Fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        ).setNegativeButton(R.string.dialogCancel, null);

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void deleteRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInterfaceActivity.this);
        builder.setTitle("Leave Group");
        builder.setMessage("Are you sure you want to delete this routine?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(TaskInterfaceActivity.this);
                progressDialog.setMessage(getString(R.string.deleteRoutine));
                progressDialog.setCancelable(false);
                progressDialog.show();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutineGroup");
                query.whereEqualTo("routineGroup", routineBean.getRoutineName());
                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("isDeleted", false);
                query.getInBackground(routineBean.getId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            parseObject.put("isDeleted", true);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    progressDialog.cancel();
                                    intent = new Intent(TaskInterfaceActivity.this, MainActivity.class);
                                    intent.putExtra("Activity", "New Routine");
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }
}
