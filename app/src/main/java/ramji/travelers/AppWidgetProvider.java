package ramji.travelers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;


public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    private static final String TAG = "AppWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.i(TAG,"widget onUpdate");
        FavouritePlaceUpdatedWidget.startActionUpdateFavWidgets(context);

    }

    public static void updateAppWidget(Context context,AppWidgetManager appWidgetManager,
                                       ArrayList<String> location,int[] appWidgetIds) {
        Log.i(TAG,"widget updateAppWidget");
        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            Log.i(TAG,"location size: "+ location.size());
            updateAppFavWidget(context,appWidgetManager,location,appWidgetId);
        }

    }

    private static void updateAppFavWidget(Context context, AppWidgetManager appWidgetManager,
                                           ArrayList<String> location,int appWidgetId) {

        Log.i(TAG,"widget updateAppFavWidget");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.appwidget);

        Intent intent = new Intent(context,HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        Log.i(TAG,"location size: "+ location.size());
        String location_list  = "";
        for (int i = 0; i < location.size(); i++){
            int position = i+1;
            Log.i(TAG,"updateAppFavWidget: " + location.get(i));
            location_list = location_list + position  +". " + location.get(i) +" \n";
            remoteViews.setTextViewText(R.id.text_View,location_list);
        }
        Log.i(TAG,"location_list: "+ location.toString());
        remoteViews.setOnClickPendingIntent(R.id.text_View,pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
    }



}
