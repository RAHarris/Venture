package descriptio.net.venture;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import descriptio.net.venture.views.ThaumaManager;

/**
 * Created by rahar on 1/18/2016.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String LOGCAT_TAG = "GeofenceTransitions";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Log.i(LOGCAT_TAG, "geofence intent receieved");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        long astuId = intent.getLongExtra(ThaumaManager.ARG_ASTU_ID, -1);
        int thaumaId = intent.getIntExtra(ThaumaManager.ARG_THAUMA_UID, -1);
        if (event.hasError()) {
            Log.e(LOGCAT_TAG, GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
            return;
        }
        if (astuId == -1 || thaumaId == -1) {
            Log.e(LOGCAT_TAG, "an id attached to geofence is null: " + astuId + ", " + thaumaId);
            return;
        }
        int transition = event.getGeofenceTransition();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List triggering = event.getTriggeringGeofences();
            String transitionDetails = getGeofenceTransitionDetails(this, transition, triggering);
            Intent clickIntent = new Intent(this, VentureActivity.class);
            clickIntent.putExtra(ThaumaManager.ARG_ASTU_ID, astuId);
            clickIntent.putExtra(ThaumaManager.ARG_THAUMA_UID, thaumaId);
            sendNotification(transitionDetails, clickIntent);
        }
    }

    private String getGeofenceTransitionDetails(Context context, int transition, List geofences) {
        return "A geofence was triggered" + geofences.get(0).toString();
    }

    private void sendNotification(String notificationString, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Geofence activated!")
                .setContentText(notificationString)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationString))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }
}
