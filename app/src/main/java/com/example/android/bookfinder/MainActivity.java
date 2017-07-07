package com.example.android.bookfinder;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<BookClass>> {

    /**
     * URL to query the Google Books dataset for Book information
     */
    private static final String GB_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of Books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Value for search query
     */
    private String mQuery;

    /**
     * List of books
     */
    private ListView BookListView;

    /**
     * SearchView that takes the query
     */
    private SearchView searchView;

    /**
     * ProgressBar that is displayed when the data is loaded
     */
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        BookListView = (ListView) findViewById(R.id.book_list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        BookListView.setEmptyView(mEmptyStateTextView);

        // Find the reference to the loading indicator in a layout
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<BookClass>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        BookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected Books.
        BookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                BookClass currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri infoUri = Uri.parse(currentBook.getInfoUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, infoUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        searchView = (SearchView) findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnected()) {
                    BookListView.setVisibility(View.INVISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    mQuery = searchView.getQuery().toString();
                    mQuery = mQuery.replace(" ", "+");
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                } else {
                    BookListView.setVisibility(View.INVISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
    // Helper method to check network connection

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<BookClass>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL

        String requestUrl = "";
        if (mQuery != null && mQuery != "") {
            requestUrl = GB_REQUEST_URL + mQuery;
        } else {
            String defaultQuery = "android";
            requestUrl = GB_REQUEST_URL + defaultQuery;
        }
        return new BookLoader(this, requestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<BookClass>> loader, List<BookClass> BookObjects) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link BookClass}es, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (BookObjects != null && !BookObjects.isEmpty()) {
            mAdapter.addAll(BookObjects);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookClass>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}