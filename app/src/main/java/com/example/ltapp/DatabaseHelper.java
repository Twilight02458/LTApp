package com.example.ltapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_BOOKINGS = 
        "CREATE TABLE " + TABLE_BOOKINGS + "(" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_DATE + " TEXT NOT NULL, " +
        COLUMN_TIME_SLOTS + " TEXT NOT NULL, " +
        COLUMN_TOTAL_PRICE + " INTEGER NOT NULL, " +
        COLUMN_COURT_NUMBER + " INTEGER NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // Phương thức thêm booking mới
    public long addBooking(String date, String timeSlots, int totalPrice, int courtNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME_SLOTS, timeSlots);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_COURT_NUMBER, courtNumber);

        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
    }
}
