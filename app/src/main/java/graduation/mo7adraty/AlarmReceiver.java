package graduation.mo7adraty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ahmed on 14/04/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service1 = new Intent(context, NotifyService.class).putExtra("type",intent.getStringExtra("type"));
        context.startService(service1);
    }
}
