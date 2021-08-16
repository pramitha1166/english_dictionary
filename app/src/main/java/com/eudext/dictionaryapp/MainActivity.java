package com.eudext.dictionaryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    public static DatabaseHelper myDBHelper;
    public static Boolean dataBaseOpened=false;

    SimpleCursorAdapter suggestion_adapter;

    RecyclerView recyclerView;
    ArrayList<History> histories;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdpater;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        myDBHelper = new DatabaseHelper(this);
        if(myDBHelper.checkDB()) {
            openDatabse();
        }else {
            LoadDataBaseAsync task = new LoadDataBaseAsync(MainActivity.this);
            task.execute();
        }

        final String[] from = new String[] {"word"};
        final int[] to = new int[] {R.id.suggestion_text};

        suggestion_adapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestions_row, null, from, to, 0) {
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };

        searchView.setSuggestionsAdapter(suggestion_adapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String clicked_word = cursor.getString(cursor.getColumnIndex("word"));
                searchView.setQuery(clicked_word, false);

                searchView.clearFocus();
                searchView.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaning.class);
                Bundle bundle = new Bundle();
                bundle.putString("word", clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String text = searchView.getQuery().toString();

                Cursor cursor = myDBHelper.getMeaning(text);

                if(cursor.getCount()==0) {
                    searchView.setQuery("",false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search word agin");

                    String positive = getString(android.R.string.ok);
                    builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    String negative = getString(android.R.string.cancel);
                    builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            searchView.clearFocus();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else {

                    searchView.clearFocus();
                    searchView.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaning.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word",text);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchView.setIconifiedByDefault(false);
                Cursor cursorSuggestion = myDBHelper.getSuggestions(newText);
                suggestion_adapter.changeCursor(cursorSuggestion);

                return false;
            }
        });

        emptyHistory = (RelativeLayout)findViewById(R.id.empty_history);

        recyclerView = (RecyclerView)findViewById(R.id.view_history_recycler);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        fetchHistoryData();

    }

    public void fetchHistoryData() {

        histories = new ArrayList<>();
        historyAdpater = new HistoryRecyclerViewAdapter(this, histories);
        recyclerView.setAdapter(historyAdpater);


        if(dataBaseOpened) {


            cursorHistory = myDBHelper.getHistoryData();

            if(cursorHistory.moveToFirst()) {
                do{

                    histories.add(new History(cursorHistory.getString(cursorHistory.getColumnIndex("word"))));


                }while (cursorHistory.moveToNext());
            }

            historyAdpater.notifyDataSetChanged();

            if(historyAdpater.getItemCount()==0) {
                emptyHistory.setVisibility(View.VISIBLE);
            }else {
                emptyHistory.setVisibility(View.GONE);
            }

        }


    }

    public static void openDatabse() {
        try {
            myDBHelper.openDataBase();
            dataBaseOpened=true;

        }catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_exit) {
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchHistoryData();
    }
}