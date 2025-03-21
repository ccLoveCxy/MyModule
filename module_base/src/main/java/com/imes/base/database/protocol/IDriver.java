package com.imes.base.database.protocol;

import android.database.sqlite.SQLiteException;

import com.imes.base.database.DatabaseResult;

import java.util.List;


/**
 * Created by linjiang on 29/05/2018.
 *
 * Database driver：SQLite、ContentProvider
 */

public interface IDriver<T extends IDescriptor> {
    List<T> getDatabaseNames();

    List<String> getTableNames(T databaseDesc) throws SQLiteException;

    void executeSQL(T databaseDesc, String query, DatabaseResult result) throws SQLiteException;
}
