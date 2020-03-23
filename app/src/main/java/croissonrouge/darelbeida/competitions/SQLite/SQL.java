package croissonrouge.darelbeida.competitions.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "croissonrougedarelbeida.db"; //not case sensitive
    private static final String COL_1 = "_ID";
    private static final String COL_2 = "_THING";
    private static final String COL_21 = "_SUBMITTERSNAME";
    private static final String COL_22 = "_SUBMITTERSUID";
    private static final String COL_23 = "_MYVOTEONHISPOST";
    private static final String COL_24 = "_RATING";
    private static final String COL_25 = "_RATINGS";
    private static final String COL_26 = "_IMAGEONCLOUD";
    private static final String TABLE_NAME = "parameters";
    private static SQL mInstance = null;

    public SQL(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public static SQL getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new SQL(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public boolean insertData(String _SETTING){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, _SETTING);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues); //returns -1 if failed to add
        if(result == -1) return false;
        else return true;
    }

    public Cursor getData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT" + ");");
        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + ";", null);
    }

    public boolean updateData(String _ID, String _THING){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, _ID);
        contentValues.put(COL_2, _THING);
        sqLiteDatabase.update(TABLE_NAME, contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }
}
