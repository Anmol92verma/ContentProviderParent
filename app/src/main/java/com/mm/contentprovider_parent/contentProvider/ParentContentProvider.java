package com.mm.contentprovider_parent.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by anmolverma on 1/29/17.
 */

public class ParentContentProvider extends ContentProvider {
    public static final String _ID = "_id";
    public static String COLUMN_TABLE_NAME = "name";
    public static String COLUMN_TABLE_URL = "url";

    public static final String PROVIDER_NAME = ParentContentProvider.class.getName();
    public static final String URL = "content://" + PROVIDER_NAME + "/images";
    public static Uri CONTENT_URI = Uri.parse(URL);
    private SQLiteDatabase sqLiteDatabase;

    static final String DATABASE_NAME = "images_db";
    static final String IMAGES_TABLE_NAME = "android";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + IMAGES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " url TEXT NOT NULL);";

    static final int IMAGES = 1;
    static final int IMAGES_ID = 2;


    static final UriMatcher uriMatcher;
    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "images", IMAGES);
        uriMatcher.addURI(PROVIDER_NAME, "images/#", IMAGES_ID);

        STUDENTS_PROJECTION_MAP = new HashMap<>();
        STUDENTS_PROJECTION_MAP.put(ParentContentProvider._ID, ParentContentProvider._ID);
        STUDENTS_PROJECTION_MAP.put(ParentContentProvider.COLUMN_TABLE_NAME, ParentContentProvider.COLUMN_TABLE_NAME);
        STUDENTS_PROJECTION_MAP.put(ParentContentProvider.COLUMN_TABLE_URL, ParentContentProvider.COLUMN_TABLE_URL);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        sqLiteDatabase = dbHelper.getWritableDatabase();
        return (sqLiteDatabase == null) ? false : true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(IMAGES_TABLE_NAME);
        qb.setProjectionMap(STUDENTS_PROJECTION_MAP);

        switch (uriMatcher.match(uri)) {
            case IMAGES:
                break;
            case IMAGES_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
        }

        if (sortOrder == null || sortOrder == "") {
            /**
             * By default sort on student names
             */
            sortOrder = COLUMN_TABLE_NAME;
        }

        Cursor c = qb.query(sqLiteDatabase, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = sqLiteDatabase.insert(IMAGES_TABLE_NAME, null, contentValues);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case IMAGES:
                count = sqLiteDatabase.delete(IMAGES_TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGES_ID:
                String id = uri.getPathSegments().get(1);
                count = sqLiteDatabase.delete(IMAGES_TABLE_NAME, _ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case IMAGES:
                count = sqLiteDatabase.update(IMAGES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case IMAGES_ID:
                count = sqLiteDatabase.update(IMAGES_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            /**
             * Get all student records
             */
            case IMAGES:
                return "vnd.android.cursor.dir/vnd.example.images";
            /**
             * Get a particular student
             */
            case IMAGES_ID:
                return "vnd.android.cursor.item/vnd.example.images";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE_NAME);
            onCreate(db);
        }
    }
}
