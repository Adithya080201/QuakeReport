package com.example.quakereport;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    String mUrl;
    /** Tag for the log messages */
    public static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    public EarthquakeLoader(@NonNull @NotNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called....");
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null){
            return null;
        }
        Log.i(LOG_TAG, "TEST: loadInBackground() called....");
        return QueryUtils.fetchEarthquake(mUrl);
    }
}
