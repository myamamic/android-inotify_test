
package yama.tp.os.mounttest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MainActivity extends Activity {
    static final String TAG = "MountTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                int ret;
                while (true) {
                    Log.e(TAG, "CALL watch()");
                    ret = watch();
                    Log.e(TAG, "RETURN watch() ret=" + ret);
                    if (ret != 0) {
                        break;
                    }
                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //unregisterReceiver(mReceiver);
        super.onStop();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        static final String EXTERNAL_STORAGE_PATH = "/mnt/ext_sdcard";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                Log.e(TAG, "Receive ACTION_MEDIA_EJECT intent.getData().getPath()=" + intent.getData().getPath());
                if (EXTERNAL_STORAGE_PATH.equals(intent.getData().getPath())) {
                    // SDカードがアンマウントされた or 取り外された

                    // ★★★TODO★★★
                    // リストの更新など
                    Log.e(TAG, "Refresh");
                }
            }
        }
    };

    public native String  stringFromJNI();
    public native int watch();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("mounttest");
    }
}
