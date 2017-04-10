package kompasim;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.suke.widget.SwitchButton;

/**
 * Created by Alvido_bahor on 2017/4/8.
 */

public class MyApplication extends Application {
    public static SwitchButton money_ = null;
    public static SwitchButton answer_ = null;

    @Override
    public void onCreate(){
        super.onCreate();
        if (!WindowService.flag) {

            ServiceConnection con = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            Intent windowService = null;
            windowService = new Intent(MyApplication.this, WindowService.class);
            bindService(windowService, con, BIND_AUTO_CREATE);
            startService(windowService);
        }
    }
}
