package com.chair49.sentimentkeyboard.analysis;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.chair49.sentimentkeyboard.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NextWord {
    private SQLiteDatabase mydatabase;

    public NextWord(Context c) {
        mydatabase = new DataBaseHelper(c).myDataBase;


    }

    public String[] getNextWords(String first, String second) {
        String[] thirds = new String[3];
        if (first != null && second != null) {
            Cursor mCursor = mydatabase.rawQuery("SELECT third FROM trigrams WHERE first=? AND second =? ORDER BY freq DESC LIMIT 0,3", new String[]{first.toLowerCase(), second.toLowerCase()});
            int i = 0;
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                do {
                    if (i < 3) {
                        thirds[i] = mCursor.getString(mCursor.getColumnIndex("third"));
                    } else {
                        break;
                    }
                    i++;

                } while (mCursor.moveToNext());
                mCursor.close();
            }
        }
        return thirds;
    }


}


class DataBaseHelper extends SQLiteOpenHelper {
    final private Context mycontext;
    private static final String DB_NAME = "w3.db";
    private static final String DB_PATH = "/data"+"/data/" + BuildConfig.APPLICATION_ID + "/databases/";
    private static final int DB_VERSION = 3;
    public SQLiteDatabase myDataBase;
    private boolean shouldBeUpdated;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mycontext = context;
        boolean dbexist = checkdatabase();
        shouldBeUpdated = false;
        if (dbexist) {
            System.out.println("Database exists");
            opendatabase();
        } else {
            System.out.println("Database doesn't exist");
            createdatabase();
            opendatabase();
        }
    }

    private void createdatabase() {
        boolean dbexist = checkdatabase();
        if (dbexist) {
            System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkdatabase() {

        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open("databases/" + DB_NAME);

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    private void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        close();
        if (shouldBeUpdated) {
            try {
                copydatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READONLY);

    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            shouldBeUpdated = true;
    }
}
