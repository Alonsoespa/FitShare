package com.fitshare.app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fitshare_local.db";
    private static final int DATABASE_VERSION = 1;

    public LocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE rutinas_favoritas (" +
                "id TEXT PRIMARY KEY, " +
                "nombre TEXT, " +
                "descripcion TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rutinas_favoritas");
        onCreate(db);
    }

    public void guardarFavorito(String id, String nombre, String descripcion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        db.insertWithOnConflict("rutinas_favoritas", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}