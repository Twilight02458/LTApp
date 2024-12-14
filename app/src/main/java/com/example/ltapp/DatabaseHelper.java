package com.example.ltapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BookingDB";
    private static final int DATABASE_VERSION = 1;
    
    // Tên bảng và các cột
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME_SLOTS = "time_slots";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_COURT_NUMBER = "court_number";
    public static final String COLUMN_COURT_NAME = "ten_san";

    // Bảng comments
    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_COMMENT_ID = "id";
    public static final String COLUMN_COMMENT_USERNAME = "username";
    public static final String COLUMN_COMMENT_CONTENT = "content";
    public static final String COLUMN_COMMENT_RATING = "rating";
    public static final String COLUMN_COMMENT_COURT_ID = "court_id";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_BOOKINGS = 
        "CREATE TABLE " + TABLE_BOOKINGS + "(" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_DATE + " TEXT NOT NULL, " +
        COLUMN_TIME_SLOTS + " TEXT NOT NULL, " +
        COLUMN_TOTAL_PRICE + " INTEGER NOT NULL, " +
        COLUMN_COURT_NUMBER + " INTEGER NOT NULL, " +
        COLUMN_COURT_NAME + " TEXT NOT NULL);";

    // Câu lệnh tạo bảng comments
    private static final String CREATE_TABLE_COMMENTS = 
        "CREATE TABLE " + TABLE_COMMENTS + "(" +
        COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_COMMENT_USERNAME + " TEXT NOT NULL, " +
        COLUMN_COMMENT_CONTENT + " TEXT NOT NULL, " +
        COLUMN_COMMENT_RATING + " FLOAT NOT NULL, " +
        COLUMN_COMMENT_COURT_ID + " INTEGER NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKINGS);
        db.execSQL(CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // Phương thức thêm booking mới
    public long addBooking(String date, String timeSlots, int totalPrice, int courtNumber, String courtName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME_SLOTS, timeSlots);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_COURT_NUMBER, courtNumber);
        values.put(COLUMN_COURT_NAME, courtName);

        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
    }

    // Phương thức thêm comment mới
    public long addComment(String username, String content, float rating, int courtId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_COMMENT_USERNAME, username);
        values.put(COLUMN_COMMENT_CONTENT, content);
        values.put(COLUMN_COMMENT_RATING, rating);
        values.put(COLUMN_COMMENT_COURT_ID, courtId);

        long id = db.insert(TABLE_COMMENTS, null, values);
        db.close();
        return id;
    }

    // Phương thức lấy danh sách comments theo court_id
    public List<Comment> getComments(int courtId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_COMMENTS + 
                           " WHERE " + COLUMN_COMMENT_COURT_ID + " = " + courtId +
                           " ORDER BY " + COLUMN_COMMENT_ID + " DESC";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_USERNAME));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT_CONTENT));
                float rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_COMMENT_RATING));
                
                Comment comment = new Comment(username, content, rating);
                commentList.add(comment);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return commentList;
    }
}
