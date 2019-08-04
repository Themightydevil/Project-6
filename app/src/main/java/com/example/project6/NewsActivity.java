package com.example.project6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private TextView mEmptyView;

    /**
     * Private int News Loader ID for reference.
     */
    private static final int NEWS_LOADER_ID = 1;
    private static NewsAdapter mAdapter;
    private static final String GUARDIAN_API = "https://content.guardianapis.com/search";
    public static final String TAG = NewsActivity.class.getName();


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves the specified String value from the preferences.
        // Second value is the default one.
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String pageSize = sharedPreferences.getString(getString(R.string.settings_min_page_size_key),
                getString(R.string.settings_min_page_size_default));

        // Parse breaks the URI String apart which is passed in as parameter.
        Uri uri = Uri.parse(GUARDIAN_API);

        // buildUpon prepares the uri that we parsed so that can can add query parameters to it.
        Uri.Builder builder = uri.buildUpon();

        // Append query parameters to it's value. e.g. order-by=newest.
        builder.appendQueryParameter("order-by", orderBy);
        builder.appendQueryParameter("page-size", pageSize);
        builder.appendQueryParameter("api-key", "ab5dac0b-21b2-4d8e-ae53-fc3a3ed5f749");
        builder.appendQueryParameter("show-tags", "contributor");

        // Return the completed URI
        // https://content.guardianapis.com/search?order-by=newest&page-size=25.
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        // Hide loading icon when response has been downloaded.
        View loadingIndicator = findViewById(R.id.progress_circular);
        loadingIndicator.setVisibility(View.GONE);
        if (isConnected()) {
            mEmptyView.setText(R.string.no_news_found);
        } else {
            mEmptyView.setText(R.string.no_connection);
        }

        // If there is a valid list of News, then add them to adapter's data set
        // This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Reset the loader, so we can load our fresh data.
        mAdapter.clear();
    }


    private boolean isConnected() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check if the device has an active internet connection or not
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // This method initializes the Activity's Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the options menu with the list that we made in XML Directory.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(NewsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the list view layout.
        ListView newsList = findViewById(R.id.list_item);

        // Create a new ArrayAdapter of news.
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the ListView so the list can be populated with the results.
        newsList.setAdapter(mAdapter);

        // Set default empty view.
        mEmptyView = findViewById(R.id.empty_view_text);
        newsList.setEmptyView(mEmptyView);

        // Get a reference to the Loader Manager to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();

        // Initialize the loader and pass the constant ID defined and null for the bundle.
        // Pass in the activity for LoaderCallbacks methods since we have implemented
        // loader callback interface.
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        // Set an onClickListener on the ListView so that we can get the ID of the item which
        // the user has interacted with and then take them to it's specific URL.
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current ID of the item pressed
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL to URI and pass it in the browser intent.
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Implicit intent to open the current URL in the browser.
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to a new activity.
                startActivity(intent);
            }
        });

    }
}
