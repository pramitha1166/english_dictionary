package com.eudext.dictionaryapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.eudext.dictionaryapp.fragments.FragmentDefinition;
import com.eudext.dictionaryapp.fragments.FragmentExample;
import com.eudext.dictionaryapp.fragments.FragmentSynonysm;
import com.eudext.dictionaryapp.fragments.FragmentWordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordMeaning extends AppCompatActivity {

    public String definition;
    public String wordType;
    ViewPager viewPager;
    TabLayout tabLayout;
    String word;
    Cursor c;
    DatabaseHelper helper;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        Bundle bundle = getIntent().getExtras();
        word = bundle.getString("word");

        helper = new DatabaseHelper(this);

        try{
            helper.openDataBase();
        }catch (SQLiteException ex) {
            throw  ex;
        }

        c = helper.getMeaning(word);

        if(c.moveToFirst()) {
            definition = c.getString(c.getColumnIndex("definition"));
            wordType = c.getString(c.getColumnIndex("wordtype"));
        }


        helper.insertHistory(word);

        ImageButton btnSpeak = (ImageButton)findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(WordMeaning.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS) {
                            int result = textToSpeech.setLanguage(Locale.getDefault());
                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("Error", "Langualge not support");
                            }else {
                                textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                        else {
                            Log.e("Error", "Speach failed");
                        }
                    }
                });
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.mToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(word);



        viewPager = (ViewPager)findViewById(R.id.tab_viewPager);

        if(viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        toolbar.setNavigationIcon(R.drawable.back);

    }

    public class ViewPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitle = new ArrayList<>();

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitle.add(title);
        }

        ViewPageAdapter(FragmentManager manager) {
            super(manager);
        }



        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitle.get(position);
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFrag(new FragmentDefinition(), "Definition");
        //viewPageAdapter.addFrag(new FragmentSynonysm(), "Synonysm");
        //viewPageAdapter.addFrag(new FragmentExample(), "Example");
        viewPageAdapter.addFrag(new FragmentWordType(), "Wordtype");
        viewPager.setAdapter(viewPageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}