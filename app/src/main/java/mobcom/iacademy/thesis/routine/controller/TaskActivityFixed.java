package mobcom.iacademy.thesis.routine.controller;

import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import mobcom.iacademy.thesis.controller.MainActivity;
import mobcom.iacademy.thesis.routine.model.RoutineBean;
import mobcom.iacademy.thesis.routine.model.TaskBean;
import mobcom.iacademy.thesis.routine.utilities.SimpleRecyclerAdapter;

public class TaskActivityFixed extends AppCompatActivity {

    Intent intent;
    ProgressDialog progressDialog;
    RoutineBean routineBean;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_activity_fixed);

        intent = this.getIntent();
        if (intent != null) {
            routineBean = new RoutineBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"));
        }

        toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        toolbar.setTitle(routineBean.getRoutineName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivityFixed.this, NewTaskActivity.class);
                intent.putExtra("groupId", routineBean.getId());
                intent.putExtra("groupName", routineBean.getRoutineName());
                intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());

                startActivity(intent);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 0), "SUN");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 1), "MON");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 2), "TUE");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 3), "WED");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 4), "THU");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 5), "FRI");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light), 6), "SAT");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
            intent = new Intent(TaskActivityFixed.this, MainActivity.class);
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
        LayoutInflater li = LayoutInflater.from(TaskActivityFixed.this);
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(TaskActivityFixed.this);

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
                            progressDialog = new ProgressDialog(TaskActivityFixed.this);
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
                                        parseObject.pinInBackground();
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
                                        TaskActivityFixed.this,
                                        "Your device appears to be offline. Unable to update routine.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(TaskActivityFixed.this, "Missing Fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        ).setNegativeButton(R.string.dialogCancel, null);

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void deleteRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivityFixed.this);
        builder.setTitle("Leave Group");
        builder.setMessage("Are you sure you want to delete this routine?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(TaskActivityFixed.this);
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
                            parseObject.unpinInBackground();
                            parseObject.put("isDeleted", true);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    progressDialog.cancel();
                                    intent = new Intent(TaskActivityFixed.this, MainActivity.class);
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


    public static class DummyFragment extends Fragment {
        int color;
        int day;
        Intent intent;
        private RoutineBean routine;
        SimpleRecyclerAdapter adapter;

        public DummyFragment() {
        }

        @SuppressLint("ValidFragment")
        public DummyFragment(int color, int day) {
            this.color = color;
            this.day = day;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dummy_fragment, container, false);
            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
            frameLayout.setBackgroundColor(color);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            intent = getActivity().getIntent();
            if (intent != null) {
                routine = new RoutineBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"));
            }

            //Sunday
            final List<TaskBean> posts = new ArrayList<>();
            adapter = new SimpleRecyclerAdapter(posts, getActivity());
            recyclerView.setAdapter(adapter);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Routine");
            query.fromLocalDatastore();
            query.orderByAscending("createdAt");
            query.whereEqualTo("routineGroup", routine.getId());
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("SelectedDay", day);
            query.whereEqualTo("isCompleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        for (ParseObject post : list) {
                            TaskBean task = new TaskBean(post.getObjectId(), post.getString("Title"), post.getString("Content"), post.getString("DueDate"), post.getString("Priority"), post.getString("username"), post.getString("routineGroup"), post.getString("routineName") ,post.getString("timeStart"));
                            posts.add(task);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                    }
                }
            });


            adapter = new SimpleRecyclerAdapter(posts, getActivity());
            recyclerView.setAdapter(adapter);

            return view;
        }

        private void viewTask(final String id, final String title, final String priority, final String dueDate, final String timeStart, final String content, final String owner, final String routineGroup, final String routineId, final String routineAdmin, final String routineName) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setCancelable(false);
            alertDialog.setTitle(title);
            alertDialog.setMessage("Description: " + content + "\n" + "Day: Sunday" + "\n"
                    + "Priority: " + priority + "\n" + "Time Start: " + timeStart + "\n" +
                    "Due Date: " + dueDate);
            alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    intent = new Intent(getActivity().getApplication(), EditTaskActivity.class);
                    intent.putExtra("selectedDay", "Sunday");
                    intent.putExtra("noteId", id);
                    intent.putExtra("noteTitle", title);
                    intent.putExtra("noteContent", content);
                    intent.putExtra("noteDate", dueDate);
                    intent.putExtra("notePriority", priority);
                    intent.putExtra("noteUsername", owner);
                    intent.putExtra("noteGroupName", routineGroup);
                    intent.putExtra("timeStart", timeStart);
                    intent.putExtra("groupId", routineId);
                    intent.putExtra("groupAdmin", routineAdmin);
                    intent.putExtra("groupName", routineName);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancel", null).show();

        }
    }
}
