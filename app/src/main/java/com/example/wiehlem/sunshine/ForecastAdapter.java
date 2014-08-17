package com.example.wiehlem.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marcel on 16.08.2014.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = R.layout.list_item_forecast;
        if (viewType == VIEW_TYPE_TODAY)
            layoutId = R.layout.list_item_forecast_today;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(Utility.COL_WEATHER_ID);
        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(Utility.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(Utility.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(Utility.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(Utility.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(low, isMetric));
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}
