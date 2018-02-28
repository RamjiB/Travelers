package ramji.travelers;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouritePlaceUpdatedWidget extends IntentService {

    private static final String TAG = "FavouritePlaceUpdatedWi";

    private static final String ACTION_UPDATE_RECIPE_WIDGETS = "ramji.travelers.update_fav_widgets";


    public FavouritePlaceUpdatedWidget() {
        super("FavouritePlaceUpdatedWidget");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "widget onHandleIntent");
        final String action = intent.getAction();
        if (ACTION_UPDATE_RECIPE_WIDGETS.equals(action))
            handleActionUpdateWidgets();

    }

    private void handleActionUpdateWidgets() {
        Log.i(TAG, "widget: handleActionUpdateWidgets ");

        final ArrayList<String> location = new ArrayList<>();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child(getString(R.string.dbname_saved_photos))
                    .child(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        location.add(singleSnapshot
                                .child(getString(R.string.db_location)).getValue().toString());
                        Log.i(TAG, "locations: " + location);

                        AppWidgetManager appWidgetManager = AppWidgetManager
                                .getInstance(getApplicationContext());
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                new ComponentName(getApplication(), AppWidgetProvider.class));
                        Log.i(TAG, "location size: " + location.size());
                        AppWidgetProvider.updateAppWidget(getApplicationContext()
                                , appWidgetManager, location, appWidgetIds);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Log.i(TAG, "locations: " + location);
            location.add(getResources().getString(R.string.widget_empty_string) + " \n" +
                    "Please sign in");
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(getApplication(), AppWidgetProvider.class));
            Log.i(TAG, "location size: " + location.size());
            AppWidgetProvider.updateAppWidget(this, appWidgetManager, location, appWidgetIds);
        }


    }

    public static void startActionUpdateFavWidgets(Context context) {
        Intent intent = new Intent(context, FavouritePlaceUpdatedWidget.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        context.startService(intent);
    }

}
