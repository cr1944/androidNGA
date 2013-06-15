package cheng.app.nga.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NGASQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "userdata.db";
    private static final int DB_VERSION = 4;

    public interface TABLES {
        public static final String RECENTVIEW = "recent_view";
        public static final String ACCOUNT = "account";
        public static final String EXTRABOARD = "extra_board";
    }

    public interface BoardColumns {
        public static final String _ID = "_id";
        public static final String BOARD_ID = "board_id";
        public static final String TITLE = "board_title";
        public static final String SUMMARY = "board_summary";
        public static final String LOGO = "board_logo";
    }
    public interface AccountColumns {
        public static final String _ID = "_id";
        public static final String SID = "sid";
        public static final String UID = "uid";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String ISDEFAULT = "isdefault";
        public static final String HEAD = "head";
    }

    public NGASQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLES.RECENTVIEW + " ("
                + BoardColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BoardColumns.TITLE + " varchar,"
                + BoardColumns.SUMMARY + " varchar,"
                + BoardColumns.BOARD_ID + " INTEGER NOT NULL DEFAULT 0,"
                + BoardColumns.LOGO + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE(" + BoardColumns.BOARD_ID + "))");
        db.execSQL("CREATE TABLE " + TABLES.ACCOUNT + " ("
                + AccountColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AccountColumns.SID + " varchar,"
                + AccountColumns.UID + " INTEGER NOT NULL DEFAULT 0,"
                + AccountColumns.ISDEFAULT + " INTEGER NOT NULL DEFAULT 0,"
                + AccountColumns.NAME + " varchar,"
                + AccountColumns.EMAIL + " varchar,"
                + AccountColumns.HEAD + "  BLOB,"
                + "UNIQUE(" + AccountColumns.UID + "))");
        db.execSQL("CREATE TABLE " + TABLES.EXTRABOARD + " ("
                + BoardColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BoardColumns.TITLE + " varchar,"
                + BoardColumns.SUMMARY + " varchar,"
                + BoardColumns.BOARD_ID + " INTEGER NOT NULL DEFAULT 0,"
                + BoardColumns.LOGO + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE(" + BoardColumns.BOARD_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLES.RECENTVIEW + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLES.ACCOUNT + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLES.EXTRABOARD + ";");
            onCreate(db);
            return;
        }
    }

}
