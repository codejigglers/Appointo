package examples.sdk.android.clover.com.appointo.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper mInstance;
    private static final String TAG = "SQLiteHelper";

    // Database Info
    private static final String DATABASE_NAME = "UserDB.db";
    private static final int DATABASE_VERSION = 1;

    //table name
    private static final String USER_TABLE = "UserTable";

    //column names
    private static final String USER_NAME = "userName";
    private static final String EMAIL_ID_PK = "emailId"; //primary key
    private static final String COLLEGE_ID = "collegeId";
    private static final String USER_PHOTO = "userPhoto";

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SQLiteHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE +
                "(" +
                EMAIL_ID_PK + " TEXT PRIMARY KEY," +
                USER_NAME + " TEXT," +
                COLLEGE_ID + " TEXT," +
                USER_PHOTO + " BLOB" +
                ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
    }

    public long addOrUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long rowId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(USER_NAME, user.getUserName());
            values.put(EMAIL_ID_PK, user.getEmailId());
            values.put(COLLEGE_ID, user.getCollegeId());
            values.put(USER_PHOTO, user.getUserPic());

            if (getUserInfo(user.getEmailId()) == null) {
                //user doesn't exist in sqlite, create new entry
                rowId = db.insertOrThrow(USER_TABLE, null, values);
                Log.d(TAG, "Inserted row with userId: " + user.getUserName());
            } else {
                //update existing record in db
                rowId = db.update(USER_TABLE, values, EMAIL_ID_PK + "= ?",
                        new String[]{String.valueOf(user.getEmailId())});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add user");
        } finally {
            db.endTransaction();
        }
        return rowId;
    }

    public List<User> getAllData() {
        List<User> users = new ArrayList<>();

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE, null, null, null, null, null, null);
        Log.d(TAG, "Retrieved rows : " + cursor.getCount());

        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
                    user.setEmailId(cursor.getString(cursor.getColumnIndex(EMAIL_ID_PK)));
                    user.setCollegeId(cursor.getString(cursor.getColumnIndex(COLLEGE_ID)));
                    user.setUserPic(cursor.getBlob(cursor.getColumnIndex(USER_PHOTO)));

                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get user from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

    public User getUserInfo(final String emailId) {
        String SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = ?",
                        USER_TABLE, EMAIL_ID_PK);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(emailId)});
        if (cursor.moveToFirst() == false) {
            return null;
        } else {
            User user = new User();
            user.setEmailId(cursor.getString(cursor.getColumnIndex(EMAIL_ID_PK)));
            user.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            user.setCollegeId(cursor.getString(cursor.getColumnIndex(COLLEGE_ID)));
            user.setUserPic(cursor.getBlob(cursor.getColumnIndex(USER_PHOTO)));
            return user;
        }
    }

    public int deleteUserInfo(final String emailId) {
        SQLiteDatabase db = getWritableDatabase();
        int deletedRowCount = 0;
        db.beginTransaction();
        try {
            deletedRowCount = db.delete(USER_TABLE, EMAIL_ID_PK + "=" + emailId, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while delete user record with emailId: " + emailId);
        } finally {
            db.endTransaction();
        }
        return deletedRowCount;
    }
}
