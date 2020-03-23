package croissonrouge.darelbeida.competitions.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL3 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "croissonrougedarelbeida3.db"; //not case sensitive
    private static final String COL_1 = "_ID";
    private static final String COL_21 = "_SUBMITTERSNAME";
    private static final String COL_22 = "_SUBMITTERSUID";
    private static final String COL_23 = "_MYVOTEONHISPOST";
    private static final String COL_24 = "_RATING";
    private static final String COL_25 = "_RATINGS";
    private static final String COL_26 = "_IMAGEONCLOUD";
    private static final String COL_27 = "_BOOKTITLE";
    private static final String COL_28 = "_RESUME";
    private static final String TABLE_NAME = "submissions";
    private static SQL3 mInstance = null;

    public SQL3(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_21 + " TEXT, " + COL_22 + " TEXT,  " + COL_23 + " TEXT, " + COL_24 + " TEXT, " + COL_25 + " TEXT, " + COL_26 + " TEXT, " + COL_27 + " TEXT, " + COL_28 + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public static SQL3 getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new SQL3(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public void delete(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }

    public boolean insertData(String _SUBMITTERSNAME, String _SUBMITTERSUID, String MYVOTEONHISPOST, String RATING, String RATINGS, String IMAGEINCLOUD, String BOOKNAME, String resume){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_21, _SUBMITTERSNAME);
        contentValues.put(COL_22, _SUBMITTERSUID);
        contentValues.put(COL_23, MYVOTEONHISPOST);
        contentValues.put(COL_24, RATING);
        contentValues.put(COL_25, RATINGS);
        contentValues.put(COL_26, IMAGEINCLOUD);
        contentValues.put(COL_27, BOOKNAME);
        contentValues.put(COL_28, resume);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues); //returns -1 if failed to add
        if(result == -1) return false;
        else return true;
    }

    public Cursor getData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_21 + " TEXT, " + COL_22 + " TEXT,  " + COL_23 + " TEXT, " + COL_24 + " TEXT, " + COL_25 + " TEXT, " + COL_26 + " TEXT, " + COL_27 + " TEXT, " + COL_28 + " TEXT" + ");");
        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + ";", null);
    }

    public boolean updateData(String _ID, String _SUBMITTERSNAME, String _SUBMITTERSUID, String MYVOTEONHISPOST, String RATING, String RATINGS, String IMAGEINCLOUD, String BOOKNAME, String resume){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, _ID);
        contentValues.put(COL_21, _SUBMITTERSNAME);
        contentValues.put(COL_22, _SUBMITTERSUID);
        contentValues.put(COL_23, MYVOTEONHISPOST);
        contentValues.put(COL_24, RATING);
        contentValues.put(COL_25, RATINGS);
        contentValues.put(COL_26, IMAGEINCLOUD);
        contentValues.put(COL_27, BOOKNAME);
        contentValues.put(COL_28, resume);
        sqLiteDatabase.update(TABLE_NAME, contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }
}
