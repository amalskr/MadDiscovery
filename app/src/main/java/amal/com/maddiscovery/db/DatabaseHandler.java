package amal.com.maddiscovery.db;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import amal.com.maddiscovery.model.EventReport;
import amal.com.maddiscovery.model.MadEvent;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MadEventsDb";

    // MadEvents table name
    private static final String TABLE_MadEventS = "MadEvents";
    private static final String TABLE_MadEventReport = "MadEventReports";

    // MadEvents Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "event_name";
    private static final String KEY_DATE = "event_date";
    private static final String KEY_TIME = "event_time";
    private static final String KEY_ORGER = "event_organizer";
    private static final String KEY_LOCA = "event_location";
    private static final String KEY_PIC = "event_picture";
    private static final String KEY_IS_END = "report_end"; // YES NO

    // EventsReports Table Columns names
    private static final String KEY_ID_REPORT = "id";
    private static final String KEY_ID_EVENT = "event_id";
    private static final String KEY_REPORT = "report_message";
    private static final String KEY_DATE_TIME = "report_date_time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //EVENT TABLE
        String CREATE_MadEventS_TABLE = "CREATE TABLE " + TABLE_MadEventS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATE + " DATE,"
                + KEY_TIME + " TIME," + KEY_ORGER + " TEXT," + KEY_LOCA + " TEXT," + KEY_PIC + " TEXT" + "," + KEY_IS_END + " TEXT" + ")";
        db.execSQL(CREATE_MadEventS_TABLE);

        //REPORT TABLE
        String CREATE_REPORT_TABLE = "CREATE TABLE " + TABLE_MadEventReport + "("
                + KEY_ID_REPORT + " INTEGER PRIMARY KEY," + KEY_ID_EVENT + " INTEGER," + KEY_REPORT + " TEXT,"
                + KEY_DATE_TIME + " DATETIME" + ")";
        db.execSQL(CREATE_REPORT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MadEventS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MadEventReport);

        // Create tables again
        onCreate(db);
    }

    // Adding new MadMadEvent
    public long addMadEvent(MadEvent madEvent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, madEvent.getEventName());
        values.put(KEY_DATE, madEvent.getEventDate());
        values.put(KEY_TIME, madEvent.getEventTime());
        values.put(KEY_LOCA, madEvent.getEventLocation());
        values.put(KEY_ORGER, madEvent.getEventOrganizer());
        values.put(KEY_PIC, madEvent.getEventPicturePath());
        values.put(KEY_IS_END, madEvent.getIsEventEnd());

        long statusId = db.insert(TABLE_MadEventS, null, values);
        db.close();
        return statusId;
    }

    // Adding new report
    public long addEventReport(EventReport eventReport) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_EVENT, eventReport.getEventId());
        values.put(KEY_REPORT, eventReport.getMessage());
        values.put(KEY_DATE_TIME, eventReport.getReportDateTime());

        long statusId = db.insert(TABLE_MadEventReport, null, values);
        db.close();
        return statusId;
    }

    // Getting All Reports for event ID
    public List<EventReport> getAllReportsForEvent(int eventid) {
        List<EventReport> reportList = new ArrayList<EventReport>();

        String selectQuery = "SELECT  * FROM " + TABLE_MadEventReport + " WHERE  " + KEY_ID_EVENT + " " +
                "= ? ORDER BY " + KEY_ID_REPORT + " desc";
        String[] values = {String.valueOf(eventid)};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, values);

        if (cursor.moveToFirst()) {
            do {
                EventReport eventReport = new EventReport();
                eventReport.setEventId(Integer.parseInt(cursor.getString(1)));
                eventReport.setMessage(cursor.getString(2));
                eventReport.setReportDateTime(cursor.getString(3));

                reportList.add(eventReport);

            } while (cursor.moveToNext());
        }

        db.close();

        return reportList;
    }

    // Getting single MadMadEvent
    public MadEvent getMadEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MadEventS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DATE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MadEvent madEvent = new MadEvent(cursor.getString(2),
                cursor.getString(1), cursor.getString(2), cursor.getString(2), cursor.getString(2));
        // return MadMadEvent
        return madEvent;
    }

    // Getting All MadEvents
    public List<MadEvent> getAllMadEvents() {
        List<MadEvent> MadEventList = new ArrayList<MadEvent>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MadEventS + " ORDER BY " + KEY_ID + " desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MadEvent madEvent = new MadEvent();
                madEvent.setId(Integer.parseInt(cursor.getString(0)));
                madEvent.setEventName(cursor.getString(1));
                madEvent.setEventDate(cursor.getString(2));
                madEvent.setEventTime(cursor.getString(3));
                madEvent.setEventOrganizer(cursor.getString(4));
                madEvent.setEventLocation(cursor.getString(5));
                madEvent.setEventPicturePath(cursor.getString(6));
                madEvent.setIsEventEnd(cursor.getString(7));

                MadEventList.add(madEvent);

            } while (cursor.moveToNext());
        }

        db.close();
        // return MadMadEvent list
        return MadEventList;
    }


    public boolean isDuplicateEvent(MadEvent madEvent) {
        boolean isHasData = false;

        String selectQuery = "SELECT  * FROM " + TABLE_MadEventS + " WHERE " + KEY_NAME + " = ? " +
                "AND " + KEY_DATE + " = ? AND " + KEY_ORGER + " = ?";
        String causeArg[] = {madEvent.getEventName(), madEvent.getEventDate(), madEvent.getEventOrganizer()};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, causeArg);

        if (cursor.moveToFirst()) {
            // HAS DATA
            isHasData = true;
        } else {
            // no data
            isHasData = false;
        }

        db.close();
        return isHasData;
    }

    // Updating Event ON/OFF
    public int updateEvent(MadEvent madEvent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IS_END, madEvent.getIsEventEnd());

        // updating row
        int id = db.update(TABLE_MadEventS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(madEvent.getId())});

        db.close();

        return id;
    }

    // Updating single MadMadEvent
    public int updateMadEvent(MadEvent madEvent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, madEvent.getEventName());
        values.put(KEY_DATE, madEvent.getEventDate());

        // updating row
        return db.update(TABLE_MadEventS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(madEvent.getId())});
    }

    // Deleting single MadMadEvent
    public void deleteMadEvent(MadEvent madEvent) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MadEventS, KEY_ID + " = ?",
                new String[]{String.valueOf(madEvent.getId())});
        db.close();
    }
}
