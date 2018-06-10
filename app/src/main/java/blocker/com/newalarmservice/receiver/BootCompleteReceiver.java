package blocker.com.newalarmservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import blocker.com.newalarmservice.services.OnBootCompleteService;


public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("onBootREceive", "Success");
        Intent bootIntent = new Intent(context, OnBootCompleteService.class);
        context.startService(bootIntent);
    }
}
