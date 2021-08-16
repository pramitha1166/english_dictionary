package com.eudext.dictionaryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar)findViewById(R.id.settingToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.back);

        TextView clearCache = (TextView)findViewById(R.id.clearCache);
        TextView aboutPage = (TextView)findViewById(R.id.aboutPage);

        helper = new DatabaseHelper(this);

        try{
            helper.openDataBase();
        }catch (SQLiteException e) {
            e.printStackTrace();
        }

        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

        aboutPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, About.class);
                startActivity(intent);
            }
        });

    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this, R.style.MyDialogTheme);
        builder.setTitle("Are you sure to delete cache?");
        builder.setMessage("All history data will be deleted");

        String positive = "Yes";
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteHistory();
            }
        });

        String negative = "No";
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}