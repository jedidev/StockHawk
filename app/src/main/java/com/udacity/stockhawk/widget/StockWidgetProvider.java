package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.udacity.stockhawk.R;

public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int count = appWidgetIds.length;

        for (int n = 0; n < count; n++) {

            Intent intent = new Intent(context, EventWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[n]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);
            rv.setRemoteAdapter(android.R.id.list, intent);
            rv.setEmptyView(android.R.id.list, R.id.tvEmptyList);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }
}
