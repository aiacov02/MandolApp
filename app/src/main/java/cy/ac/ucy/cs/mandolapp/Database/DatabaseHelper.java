package cy.ac.ucy.cs.mandolapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.PreparedStatement;

import cy.ac.ucy.cs.mandolapp.ReportActivities.PictureReport;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;


/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Reports.db";
    private static final String TABLE_NAME1 = "SavedReport";
    private static final String TABLE_NAME2 = "SentReport";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "APP";
    private static final String COL_3 = "URL";
    private static final String COL_4 = "Description";
    private static final String COL_5 = "CATEGORIES";
    private static final String COL_6 = "AUTHORITIES";
    private static final String COL_7 = "DATEANDTIME";
    private static final String COL_8 = "IMAGE";
    private static final String COL_9 = "LOCATION";



    private static DatabaseHelper databaseHelper;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public DatabaseHelper getDatabaseHelper(Context context){
        if(this.databaseHelper==null){
            this.databaseHelper = new DatabaseHelper(context);
        }
        return this.databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME1 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,APP TEXT,URL TEXT,DESCRIPTION TEXT,CATEGORIES TEXT, AUTHORITIES TEXT,DATEANDTIME TEXT)");
        db.execSQL("create table " + TABLE_NAME2 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,APP TEXT,URL TEXT,DESCRIPTION TEXT,CATEGORIES TEXT, AUTHORITIES TEXT,DATEANDTIME TEXT, IMAGE BLOB,LOCATION TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);

        onCreate(db);
    }

    public boolean insertDataSent(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,report.getApp());
        contentValues.put(COL_3,report.getUrl());
        contentValues.put(COL_4,report.getDescription());
        contentValues.put(COL_5, ArrayString.convertArrayToString(report.getCategories()));
        contentValues.put(COL_6,ArrayString.convertArrayToString(report.getAuthorities()));
        if(report.getDate()==null){
            contentValues.put(COL_7,ArrayString.getDateTime());
        }
        else{
            contentValues.put(COL_7,report.getDate());
        }
        if(report.getImage()!=null)contentValues.put(COL_8, report.getImage());
        if(report.getLocation()!=null)contentValues.put(COL_9, report.getLocation());
        long result = db.insert(TABLE_NAME2,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataPictureSent(PictureReport report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,report.getApp());
        contentValues.put(COL_4,report.getDescription());
        contentValues.put(COL_5, ArrayString.convertArrayToString(report.getCategories()));
        contentValues.put(COL_6,ArrayString.convertArrayToString(report.getAuthorities()));
        if(report.getDate()==null){
            contentValues.put(COL_7,ArrayString.getDateTime());
        }
        else{
            contentValues.put(COL_7,report.getDate());
        }
        contentValues.put(COL_8, report.getImage());
        contentValues.put(COL_9, report.getLocation());

        long result = db.insert(TABLE_NAME2,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllDataSent() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME2 + " ORDER BY DATEANDTIME DESC",null);
        return res;
    }

    public Cursor getAllData(String app) {
        SQLiteDatabase db = this.getWritableDatabase();
        String app1 = app.toLowerCase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1 + " where APP=\"" + app1+"\" ORDER BY DATEANDTIME DESC",null);
        return res;
    }

    public Cursor getAllData(String app,String pattern){
        String pattern1 = "%" + pattern + "%";
        SQLiteDatabase db = this.getWritableDatabase();
        String app1 = app.toLowerCase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1 + " where (APP=\"" + app1+"\") AND (DESCRIPTION like ?) ORDER BY DATEANDTIME DESC", new String[]{pattern1});
        return res;
    }

    public Cursor getAllDataSearchBy(String pattern){
        String pattern1 = "%" + pattern + "%";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME1 + " where DESCRIPTION like ? ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{pattern1});
        return res;
    }

    public Cursor getAllDataSearchByDate(String date){
        String dateStart = date + " 00:00:00";
        String dateEnd = date + " 23:59:59";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME1 + " where (DATEANDTIME > ?) AND (DATEANDTIME < ?) ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{dateStart,dateEnd});
        return res;
    }

    public Cursor getAllDataSearchByDate(String app ,String date){
        String app1 = app.toLowerCase();
        String dateStart = date + " 00:00:00";
        String dateEnd = date + " 23:59:59";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME1 + " where (APP = ?) AND (DATEANDTIME > ?) AND (DATEANDTIME < ?) ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{app1,dateStart,dateEnd});
        return res;
    }

    public Cursor getAllDataSent(String app) {
        SQLiteDatabase db = this.getWritableDatabase();
        String app1 = app.toLowerCase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME2 + " where APP=\"" + app1+"\" ORDER BY DATEANDTIME DESC",null);
        return res;
    }

    public Cursor getAllDataSent(String app,String pattern){
        String pattern1 = "%" + pattern + "%";
        SQLiteDatabase db = this.getWritableDatabase();
        String app1 = app.toLowerCase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME2 + " where (APP=\"" + app1+"\") AND (DESCRIPTION like ?) ORDER BY DATEANDTIME DESC", new String[]{pattern1});
        return res;
    }

    public Cursor getAllDataSentSearchBy(String pattern){
        String pattern1 = "%" + pattern + "%";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME2 + " where DESCRIPTION like ? ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{pattern1});
        return res;
    }

    public Cursor getAllDataSentSearchByDate(String date){
        String dateStart = date + " 00:00:00";
        String dateEnd = date + " 23:59:59";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME2 + " where (DATEANDTIME > ?) AND (DATEANDTIME < ?) ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{dateStart,dateEnd});
        return res;
    }

    public Cursor getAllDataSentSearchByDate(String app ,String date){
        String app1 = app.toLowerCase();
        String dateStart = date + " 00:00:00";
        String dateEnd = date + " 23:59:59";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from "+TABLE_NAME2 + " where (APP = ?) AND (DATEANDTIME > ?) AND (DATEANDTIME < ?) ORDER BY DATEANDTIME DESC";
        Cursor res = db.rawQuery(sql, new String[]{app1,dateStart,dateEnd});
        return res;
    }

    public boolean updateDataSent(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,report.getId());
        contentValues.put(COL_2,report.getApp());
        contentValues.put(COL_3,report.getUrl());
        contentValues.put(COL_4,report.getDescription());
        contentValues.put(COL_5,ArrayString.convertArrayToString(report.getCategories()));
        contentValues.put(COL_6,ArrayString.convertArrayToString(report.getAuthorities()));
        if(report.getDate()==null){
            contentValues.put(COL_7,ArrayString.getDateTime());
        }
        else{
            contentValues.put(COL_7,report.getDate());
        }

        db.update(TABLE_NAME2, contentValues, "ID = ?",new String[] { report.getId() });
        return true;
    }

    public Integer deleteDataSent(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME2, "ID = ?",new String[] {report.getId()});
    }

    public boolean insertData(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,report.getApp());
        contentValues.put(COL_3,report.getUrl());
        contentValues.put(COL_4,report.getDescription());
        contentValues.put(COL_5, ArrayString.convertArrayToString(report.getCategories()));
        contentValues.put(COL_6,ArrayString.convertArrayToString(report.getAuthorities()));
        if(report.getDate()==null){
            contentValues.put(COL_7,ArrayString.getDateTime());
        }
        else{
            contentValues.put(COL_7,report.getDate());
        }
        long result = db.insert(TABLE_NAME1,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1 + " ORDER BY DATEANDTIME DESC ",null);
        return res;
    }

    public Cursor getReportSent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ID = Integer.parseInt(id);
        Cursor res = db.rawQuery("select * from "+TABLE_NAME2+" where ID =" + ID,null);
        return res;
    }

    public boolean updateData(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,report.getId());
        contentValues.put(COL_2,report.getApp());
        contentValues.put(COL_3,report.getUrl());
        contentValues.put(COL_4,report.getDescription());
        contentValues.put(COL_5,ArrayString.convertArrayToString(report.getCategories()));
        contentValues.put(COL_6,ArrayString.convertArrayToString(report.getAuthorities()));
        if(report.getDate()==null){
            contentValues.put(COL_7,ArrayString.getDateTime());
        }
        else{
            contentValues.put(COL_7,report.getDate());
        }

        db.update(TABLE_NAME1, contentValues, "ID = ?",new String[] { report.getId() });
        return true;
    }

    public Integer deleteData (Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME1, "ID = ?",new String[] {report.getId()});
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME1,"ID",null);
    }

    public void deleteAllDataSent(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2,"ID",null);
    }


}
