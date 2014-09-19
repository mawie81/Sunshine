package com.example.wiehlem.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wiehlem.sunshine.data.WeatherContract;

/**
 * Created by wiehlem on 11.08.2014.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_LOADER = 1;

    private String mForecast;
    private String mDate;
    private ShareActionProvider provider;
    private TextView mDateTextView;
    private TextView mDescTextView;
    private TextView mLowTextView;
    private TextView mHighTextView;
    private TextView mHumidityTextView;
    private TextView mPressureTextView;
    private TextView mWindTextView;
    private ImageView mIcon;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_share));
        if (provider != null)
            provider.setShareIntent(createShareIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescTextView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mLowTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHighTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_forecast_humidity);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_forecast_pressure);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_forecast_wind);
        mIcon = (ImageView) rootView.findViewById(R.id.detail_forecast_icon);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mDate = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        return rootView;
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecast + " #SunshineApp");
        return intent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String location = Utility.getPreferredLocation(getActivity());

        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, mDate);

        return new CursorLoader(getActivity(),
                weatherUri,
                Utility.DETAILS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            boolean isMetric = Utility.isMetric(getActivity());
            String date = Utility.formatDate(mDate);
            String desc = data.getString(Utility.COL_WEATHER_DESC);
            String minTemp = Utility.formatTemperature(getActivity(), data.getDouble(Utility.COL_WEATHER_MIN_TEMP), isMetric);
            String maxTemp = Utility.formatTemperature(getActivity(), data.getDouble(Utility.COL_WEATHER_MAX_TEMP), isMetric);
            String wind = Utility.getFormattedWind(getActivity(), data.getFloat(Utility.COL_WEATHER_WIND_SPEED), data.getFloat(Utility.COL_WEATHER_WIND_DEGREE));
            String pressure = getActivity().getString(R.string.format_pressure, data.getFloat(Utility.COL_WEATHER_PRESSURE));
            String humitity = getActivity().getString(R.string.format_humidity, data.getFloat(Utility.COL_WEATHER_HUMIDITY));

            mDescTextView.setText(desc);
            mDateTextView.setText(date);
            mLowTextView.setText(minTemp);
            mHighTextView.setText(maxTemp);
            mWindTextView.setText(wind);
            mPressureTextView.setText(pressure);
            mHumidityTextView.setText(humitity);
            mIcon.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(Utility.COL_WEATHER_ID)));

            mForecast = String.format("%s - %s - %s / %s", date, desc, maxTemp, minTemp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
    }
}