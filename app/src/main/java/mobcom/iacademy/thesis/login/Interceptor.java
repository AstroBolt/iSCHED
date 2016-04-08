package mobcom.iacademy.thesis.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import mobcom.iacademy.thesis.controller.MainActivity;
import mobcom.iacademy.thesis.R;


public class Interceptor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interceptor);
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(Interceptor.this, MainActivity.class);
            populateLocalDataStore();
            intent.putExtra("Activity", "New Routine");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Interceptor.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void populateLocalDataStore() {
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("RoutineGroup");
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.pinAllInBackground(list);
            }
        });

        ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Routine");
        query3.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.pinAllInBackground(list);
            }
        });

        ParseQuery<ParseObject> query4 = ParseQuery.getQuery("GroupMembers");
        query4.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.pinAllInBackground(list);
            }
        });

        ParseQuery<ParseObject> query5 = ParseQuery.getQuery("Event");
        query5.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.pinAllInBackground(list);
            }
        });
    }
}
