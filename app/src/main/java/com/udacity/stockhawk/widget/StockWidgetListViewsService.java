package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_ID;

public class StockWidgetListViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            private Context mContext;
            private Cursor mCursor;

            @Override
            public void onCreate() {

                mContext = getApplicationContext();
            }

            @Override
            public void onDataSetChanged() {

                if (mCursor != null) {
                    mCursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                mCursor = getContentResolver().query(Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {

                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);

                if (mCursor.moveToPosition(i)) {

                    remoteViews.setTextViewText(R.id.symbol, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                    remoteViews.setTextViewText(R.id.price, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                    remoteViews.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)));

                    float price =  Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)));
                    if (price < 0) {
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    } else {
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    }
                }

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (mCursor.moveToPosition(i)) {
                    return mCursor.getLong(POSITION_ID);
                }
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
