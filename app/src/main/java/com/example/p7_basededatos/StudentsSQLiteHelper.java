package com.example.p7_basededatos;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class StudentsSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Students.db";

    private static final String SQL_CREATE_TABLE_STUDENTS =
            "CREATE TABLE " + StudentsDatabaseContract.StudentsTable.TABLE + " ("
                    + StudentsDatabaseContract.StudentsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD + " TEXT UNIQUE, "
                    + StudentsDatabaseContract.StudentsTable.COLUMN_NAME + " TEXT, "
                    + StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME + " TEXT, "
                    + StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE + " TEXT, "
                    + StudentsDatabaseContract.StudentsTable.COLUMN_COURSE + " TEXT)";

    private static final String SQL_DROP_TABLE_STUDENTS =
            "DROP TABLE IF EXISTS " + StudentsDatabaseContract.StudentsTable.TABLE;

    public StudentsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STUDENTS);
        Log.d("DatabaseCreation", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_STUDENTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertStudent(String idCard, String name, String surname, String cycle, String course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD, idCard);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_NAME, name);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME, surname);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE, cycle);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_COURSE, course);

        return db.insert(StudentsDatabaseContract.StudentsTable.TABLE, null, values);
    }

    public Cursor getStudentByIdCard(String idCard) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD,
                StudentsDatabaseContract.StudentsTable.COLUMN_NAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE,
                StudentsDatabaseContract.StudentsTable.COLUMN_COURSE
        };

        String selection = StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD + " = ?";
        String[] selectionArgs = {idCard};

        return db.query(
                StudentsDatabaseContract.StudentsTable.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public void getStudentsByCycleAndCreateFile(String cycle, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD,
                StudentsDatabaseContract.StudentsTable.COLUMN_NAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE,
                StudentsDatabaseContract.StudentsTable.COLUMN_COURSE
        };

        String selection = StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE + " = ?";
        String[] selectionArgs = {cycle};

        Cursor cursor = db.query(
                StudentsDatabaseContract.StudentsTable.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        createFileFromCursor(cursor, cycle, null, context);
    }

    public void getStudentsByCourseAndCreateFile(String cycle, String course, Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD,
                StudentsDatabaseContract.StudentsTable.COLUMN_NAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME,
                StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE,
                StudentsDatabaseContract.StudentsTable.COLUMN_COURSE
        };

        String selection = StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE + " = ? AND "
                + StudentsDatabaseContract.StudentsTable.COLUMN_COURSE + " = ?";
        String[] selectionArgs = {cycle, course};

        Cursor cursor = db.query(
                StudentsDatabaseContract.StudentsTable.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        createFileFromCursor(cursor, cycle, course, context);
    }

    private void createFileFromCursor(Cursor cursor, String cycle, String course, Context context) {
        try {
            if (cursor != null && cursor.getCount() > 0) {
                String fileName = (course != null) ? cycle + course + ".txt" : cycle + ".txt";
                FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                cursor.moveToFirst();
                do {
                    try {
                        String idCard = cursor.getString(cursor.getColumnIndexOrThrow(StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentsDatabaseContract.StudentsTable.COLUMN_NAME));
                        String surname = cursor.getString(cursor.getColumnIndexOrThrow(StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME));

                        // Write to the file
                        bufferedWriter.write(idCard + ", " + name + " " + surname + "\n");
                    } catch (IllegalArgumentException e) {
                        // Handle the exception if a column does not exist
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());

                bufferedWriter.close();
                fileOutputStream.close();

                Toast.makeText(context, "File created for " + ((course != null) ? "course " + course : "cycle") + " " + cycle, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "No students for " + ((course != null) ? "course " + course : "cycle") + " " + cycle, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public int deleteStudentByIdCard(String idCard) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD + " = ?";
        String[] selectionArgs = {idCard};

        return db.delete(StudentsDatabaseContract.StudentsTable.TABLE, selection, selectionArgs);
    }


    public int updateStudent(String idCard, String newName, String newSurname, String newCycle, String newCourse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_NAME, newName);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME, newSurname);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE, newCycle);
        values.put(StudentsDatabaseContract.StudentsTable.COLUMN_COURSE, newCourse);

        String selection = StudentsDatabaseContract.StudentsTable.COLUMN_ID_CARD + " = ?";
        String[] selectionArgs = {idCard};

        return db.update(StudentsDatabaseContract.StudentsTable.TABLE, values, selection, selectionArgs);
    }
}
