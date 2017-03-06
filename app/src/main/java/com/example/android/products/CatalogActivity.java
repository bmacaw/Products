package com.example.android.products;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.products.data.ProductContract.ProductEntry;

import java.util.List;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdaptor mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        final ListView productListView = (ListView) findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdaptor(this, null);
        productListView.setAdapter(mCursorAdapter);

        // TODO // Setup item click listener

        ListView listView = (ListView) findViewById(R.id.list_view);
        mCursorAdapter = new ProductCursorAdaptor(this, null);
        listView.setAdapter(mCursorAdapter);

    }

    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Dive Snorkel Set");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 39.95);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_sample_data:
                insertProduct();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE};
        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.changeCursor(null);

    }
}
