package com.example.tl01e11822;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTOS = "contactos";
    public static final String ID = "id";
    public static final String PAIS = "pais";
    public static final String NOMBRE = "nombre";
    public static final String TELEFONO = "telefono";
    public static final String NOTA = "nota";
    public static final String IMAGEN = "imagen";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACTOS + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PAIS + " TEXT,"
                + NOMBRE + " TEXT,"
                + TELEFONO + " TEXT,"
                + NOTA + " TEXT,"
                + IMAGEN + " BLOB)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }
}