package com.eudext.dictionaryapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;

public class LoadDataBaseAsync extends AsyncTask<Void, Void , Boolean> {

    private Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper myDBHelper;

    public LoadDataBaseAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder d = new AlertDialog.Builder(context,R.style.MyDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_db_copying, null);
        d.setTitle("Loading Database...");
        d.setView(dialogView);
        alertDialog = d.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        alertDialog.dismiss();
        MainActivity.openDatabse();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        myDBHelper = new DatabaseHelper(context);
        try{
            myDBHelper.createDataBase();
        }catch (IOException ex) {
            throw new Error("DataBase was not created");
        }
        myDBHelper.close();
        return null;
    }
}
