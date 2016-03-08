package mobcom.iacademy.thesis.utilities;

import android.app.Application;

import com.parse.Parse;

public class BackendConnection extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,Defaults.APPLICATION_ID, Defaults.SECRET_KEY);
    }
}
