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
import static com.udacity.stockhawk.ui.StockDetailActivity.SELECTED_HISTORY;
import static com.udacity.stockhawk.ui.StockDetailActivity.SELECTED_SYMBOL;

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

                    String symbolString = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                    String priceString = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
                    String changeString = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
                    String historyString = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

                    remoteViews.setTextViewText(R.id.symbol, symbolString);
                    remoteViews.setTextViewText(R.id.price, priceString);
                    remoteViews.setTextViewText(R.id.change, changeString);

                    float price =  Float.parseFloat(priceString);
                    if (price < 0) {
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    } else {
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    }

                    final Intent fillInIntent = new Intent();
                    fillInIntent.putExtra(SELECTED_SYMBOL, symbolString);
                    fillInIntent.putExtra(SELECTED_HISTORY, historyString);
                    remoteViews.setOnClickFillInIntent(R.id.list_item, fillInIntent);
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
