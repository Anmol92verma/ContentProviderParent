package com.mm.contentprovider_parent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mm.contentprovider_parent.contentProvider.ParentContentProvider;

public class MainActivity extends AppCompatActivity {

    private String someName = "MutualMobile";
    private String someUrl = "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg";
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSomeData();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void saveSomeData() {
        ContentValues contentValues = new ContentValues();
        name = someName + System.currentTimeMillis();
        contentValues.put(ParentContentProvider.COLUMN_TABLE_NAME, name);
        contentValues.put(ParentContentProvider.COLUMN_TABLE_URL, someUrl);

        insert(contentValues);
        query();
       // delete();


    }

    private void delete() {
        String[] selectionArgs = new String[]{name};
        getContentResolver().delete(ParentContentProvider.CONTENT_URI, ParentContentProvider.COLUMN_TABLE_NAME + " = ?", selectionArgs);
        query();
    }

    private void query() {
        Cursor cursor = getContentResolver().query(ParentContentProvider.CONTENT_URI, null, null, null, null);
        printCursorValues(cursor);
    }

    private void insert(ContentValues contentValues) {
        Uri resultantUri = getContentResolver().insert(ParentContentProvider.CONTENT_URI, contentValues);
        showToast(resultantUri.getPath());
    }

    private void printCursorValues(Cursor cursor) {
        showToast(DatabaseUtils.dumpCursorToString(cursor));
    }

    private void showToast(String path) {
        Log.e("this", path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
