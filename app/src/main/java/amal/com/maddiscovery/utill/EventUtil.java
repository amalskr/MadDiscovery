package amal.com.maddiscovery.utill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import amal.com.maddiscovery.model.MadEvent;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */
public class EventUtil {
    public static String eventPicPath = "NONE";
    public static String selectedPicPath = "NONE";
    public static String selectedEventName = "NONE";
    public static MadEvent selectedEvent;

    public static double dft_lat = 51.483342;
    public static double dft_lng = -0.005983;

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void shareNow(Activity activity, String message, String picurl) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.setType("image/png");
        share.putExtra(Intent.EXTRA_TEXT, message);
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(picurl));
        activity.startActivity(Intent.createChooser(share, "Share Image"));
    }
}
