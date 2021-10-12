package com.example.quakereport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private EarthquakeAdapter mAdapter;
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //textview when no earthquakes are found
    private TextView emptyStateTextView;

    //ProgressBar to display progress
    private ProgressBar onProgressIndicator;

    /** Tag for the log messages */
    public static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: onCreate() called....");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        emptyStateTextView = findViewById(R.id.empty_state_textview);
        earthquakeListView.setEmptyView(emptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            LoaderManager loaderManager = LoaderManager.getInstance(this);

            Log.i(LOG_TAG, "TEST: initLoader() called....");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            //if there is not internet connection then stop the progress indicator and display the appropriate message
            onProgressIndicator = findViewById(R.id.onProgessIndicator);
            onProgressIndicator.setVisibility(View.INVISIBLE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }



        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currEarthQuake = mAdapter.getItem(position);
                Uri earthQuakeURI = Uri.parse(currEarthQuake.getEarthQuakeURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, earthQuakeURI);
                startActivity(intent);
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called....");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "500");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<List<Earthquake>> loader, List<Earthquake> data) {
        //Once the earthquake list view has finished loading set the progress to invisible
        onProgressIndicator = findViewById(R.id.onProgessIndicator);
        onProgressIndicator.setVisibility(View.INVISIBLE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()){
            mAdapter.addAll(data);
        } else {
            emptyStateTextView.setText(R.string.empty_state_textview);
        }
        Log.i(LOG_TAG, "TEST: onLoadFinished() called....");
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
        Log.i(LOG_TAG, "TEST: onLoadReset() called....");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}