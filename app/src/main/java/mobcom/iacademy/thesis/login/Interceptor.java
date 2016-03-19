package mobcom.iacademy.thesis.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;

import mobcom.iacademy.thesis.controller.MainActivity;
import mobcom.iacademy.thesis.R;


public class Interceptor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interceptor);
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(Interceptor.this, MainActivity.class);
            intent.putExtra("Activity", "New Routine");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Interceptor.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
