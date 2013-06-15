
package cheng.app.nga.content;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.content.NGASQLiteHelper.AccountColumns;
import cheng.app.nga.content.NGASQLiteHelper.BoardColumns;
import cheng.app.nga.content.NGASQLiteHelper.TABLES;

import java.util.ArrayList;
import java.util.List;

public class NGAApp extends Application {
    static final String TAG = "NGAApp";
    SQLiteDatabase mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        NGASQLiteHelper sh = new NGASQLiteHelper(getApplicationContext());
        if (mDb == null) {
            mDb = sh.getWritableDatabase();
        }
    }

    public List<NgaBoard> loadExtraBoards() {
        Log.d(TAG, "loadExtraBoards");
        Cursor c = null;
        ArrayList<NgaBoard> result = new ArrayList<NgaBoard>();
        try {
            c = mDb.query(TABLES.EXTRABOARD, null, null, null, null, null, BoardColumns._ID
                    + " DESC");
            if (c != null && c.moveToFirst()) {
                do {
                    NgaBoard item = new NgaBoard();
                    item.icon = c.getInt(c.getColumnIndex(BoardColumns.LOGO));
                    item.title = c.getString(c.getColumnIndex(BoardColumns.TITLE));
                    item.summary = c.getString(c.getColumnIndex(BoardColumns.SUMMARY));
                    item.id = c.getInt(c.getColumnIndex(BoardColumns.BOARD_ID));
                    result.add(item);
                } while (c.moveToNext());
            }
            return result;
        } catch (RuntimeException ex) {
            Log.e(TAG, "loadExtraBoards: RuntimeException", ex);
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean addExtraBoard(int boardId, int icon,
            String title, String summary) {
        Log.d(TAG, "addExtraBoard");
        try {
            ContentValues values = new ContentValues();
            values.put(BoardColumns.BOARD_ID, boardId);
            values.put(BoardColumns.TITLE, title);
            values.put(BoardColumns.SUMMARY, summary);
            values.put(BoardColumns.LOGO, icon);
            mDb.replace(TABLES.EXTRABOARD, null, values);
            return true;
        } catch (RuntimeException ex) {
            Log.e(TAG, "addExtraBoard: RuntimeException", ex);
            return false;
        }
    }

    public int cleanExtraBoard() {
        Log.d(TAG, "cleanExtraBoard");
        try {
            return mDb.delete(TABLES.EXTRABOARD, null, null);
        } catch (RuntimeException ex) {
            Log.e(TAG, "cleanExtraBoard: RuntimeException", ex);
            return -1;
        }
    }

    public List<NgaBoard> loadRecentItems() {
        Log.d(TAG, "loadRecentItems");
        Cursor c = null;
        ArrayList<NgaBoard> result = new ArrayList<NgaBoard>();
        try {
            c = mDb.query(TABLES.RECENTVIEW, null, null, null, null, null, BoardColumns._ID
                    + " DESC");
            if (c != null && c.moveToFirst()) {
                do {
                    NgaBoard item = new NgaBoard();
                    item.icon = c.getInt(c.getColumnIndex(BoardColumns.LOGO));
                    item.title = c.getString(c.getColumnIndex(BoardColumns.TITLE));
                    item.summary = c.getString(c.getColumnIndex(BoardColumns.SUMMARY));
                    item.id = c.getInt(c.getColumnIndex(BoardColumns.BOARD_ID));
                    result.add(item);
                } while (c.moveToNext());
            }
            return result;
        } catch (RuntimeException ex) {
            Log.e(TAG, "loadRecentItems: RuntimeException", ex);
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean addToRecent(int boardId, int icon,
            String title, String summary) {
        Log.d(TAG, "addToRecent");
        try {
            ContentValues values = new ContentValues();
            values.put(BoardColumns.BOARD_ID, boardId);
            values.put(BoardColumns.TITLE, title);
            values.put(BoardColumns.SUMMARY, summary);
            values.put(BoardColumns.LOGO, icon);
            mDb.replace(TABLES.RECENTVIEW, null, values);
            return true;
        } catch (RuntimeException ex) {
            Log.e(TAG, "addToRecent: RuntimeException", ex);
            return false;
        }
    }

    public int cleanRecent() {
        Log.d(TAG, "cleanRecent");
        try {
            return mDb.delete(TABLES.RECENTVIEW, null, null);
        } catch (RuntimeException ex) {
            Log.e(TAG, "cleanRecent: RuntimeException", ex);
            return -1;
        }
    }

    public Cursor loadAccounts() {
        Log.d(TAG, "loadAccounts");
        Cursor c = null;
        try {
            c = mDb.query(TABLES.ACCOUNT, null, null, null, null, null, null);
            return c;
        } catch (RuntimeException ex) {
            Log.e(TAG, "loadInBackground: RuntimeException", ex);
            return null;
        }
    }

    public void setDefaultAccount(int newUid) {
        Log.d(TAG, "setDefaultAccount");
        try {
            ContentValues values = new ContentValues();
            values.put(AccountColumns.ISDEFAULT, 0);
            mDb.update(TABLES.ACCOUNT, values, AccountColumns.ISDEFAULT + "=1", null);
            values.put(AccountColumns.ISDEFAULT, 1);
            mDb.update(TABLES.ACCOUNT, values, AccountColumns.UID + "=" + newUid, null);
        } catch (RuntimeException ex) {
            Log.e(TAG, "setDefaultAccount: RuntimeException", ex);
        }
    }

    public String[] getCookie(int index) {
        String[] result = new String[] {"", ""};
        Cursor c = null;
        try {
            c = mDb.query(TABLES.ACCOUNT, null, null, null, null, null, null);
            if (c.moveToPosition(index)) {
                result[0] = c.getString(c.getColumnIndex(AccountColumns.UID));
                result[1] = c.getString(c.getColumnIndex(AccountColumns.SID));
            }
        } catch (RuntimeException ex) {
            Log.e(TAG, "getCookie: RuntimeException", ex);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public String getCookieString(int index) {
        String[] result = getCookie(index);
        if (!TextUtils.isEmpty(result[0]) && !TextUtils.isEmpty(result[1])) {
            return "ngaPassportUid=" + result[0] +
                    "; ngaPassportCid=" + result[1];
        }
        return "";
    }

    public void deleteAccount(int id) {
        Log.d(TAG, "deleteAccount");
        try {
            mDb.delete(TABLES.ACCOUNT, AccountColumns.UID + "=" + id, null);
        } catch (RuntimeException ex) {
            Log.e(TAG, "deleteAccount: RuntimeException", ex);
        }
    }

    public void addAccount(int uid, String sid, String name, String email) {
        Log.d(TAG, "addAccount");
        try {
            ContentValues values = new ContentValues();
            values.put(AccountColumns.UID, uid);
            values.put(AccountColumns.SID, sid);
            values.put(AccountColumns.NAME, name);
            values.put(AccountColumns.EMAIL, email);
            mDb.insert(TABLES.ACCOUNT, null, values);
            setDefaultAccount(uid);
        } catch (RuntimeException ex) {
            Log.e(TAG, "addAccount: RuntimeException", ex);
        }
    }
}
