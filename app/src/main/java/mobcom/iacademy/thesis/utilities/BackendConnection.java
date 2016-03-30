package mobcom.iacademy.thesis.utilities;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class BackendConnection extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(Defaults.APPLICATION_ID)
                .clientKey(Defaults.CLIENT_KEY)
                .server(Defaults.SERVER_URL)
                .enableLocalDataStore()
                .build());
    }
}
