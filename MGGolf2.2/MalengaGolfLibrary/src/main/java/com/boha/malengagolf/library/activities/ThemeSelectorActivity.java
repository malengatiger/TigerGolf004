package com.boha.malengagolf.library.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ThemeChooser;
import com.boha.malengagolf.library.util.Util;


public class ThemeSelectorActivity extends AppCompatActivity {

    View indigo, blue, blueGray, teal, pink, orange, red, cyan,
            green, brown, amber, grey, purple, lime;
    Activity activity;
    Context ctx;
    int themeDarkColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeChooser.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_chooser);

        activity = this;
        ctx = getApplicationContext();
        setFields();

        int logo = getIntent().getIntExtra("logo", R.drawable.ic_action_edit);
        themeDarkColor = getIntent().getIntExtra("darkColor", R.color.teal_900);
        GolfGroupDTO muni = SharedUtil.getGolfGroup(ctx);
        Util.setCustomActionBar(ctx, getSupportActionBar(),
                muni.getGolfGroupName(), "Themes", ContextCompat.getDrawable(
                        ctx, R.drawable.golf32));

    }

    private void setFields() {
        indigo = findViewById(R.id.TC_indigoLayout);
        blue = findViewById(R.id.TC_blueLayout);
        blueGray = findViewById(R.id.TC_blueGrayLayout);
        teal = findViewById(R.id.TC_tealLayout);
        pink = findViewById(R.id.TC_pinkLayout);
        orange = findViewById(R.id.TC_orangeLayout);
        red = findViewById(R.id.TC_redLayout);
        cyan = findViewById(R.id.TC_cyanLayout);
        green = findViewById(R.id.TC_greenLayout);
        brown = findViewById(R.id.TC_brownLayout);
        amber = findViewById(R.id.TC_amberLayout);
        grey = findViewById(R.id.TC_greyLayout);
        purple = findViewById(R.id.TC_purpleLayout);
        lime = findViewById(R.id.TC_limeLayout);

        indigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeToTheme(MGApp.THEME_INDIGO);

            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeToTheme(MGApp.THEME_BLUE);

            }
        });
        blueGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeToTheme(MGApp.THEME_BLUE_GRAY);

            }
        });
        teal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_TEAL);

            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_ORANGE);

            }
        });
        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_PINK);

            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_RED);

            }
        });
        cyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_CYAN);

            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_GREEN);

            }
        });
        amber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToTheme(MGApp.THEME_AMBER);

            }
        });
        brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_BROWN);

            }
        });
        grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_GREY);

            }
        });
        lime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_LIME);

            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToTheme(MGApp.THEME_PURPLE);

            }
        });

    }

    boolean themeChanged;

    private void changeToTheme(int theme) {
        SharedUtil.setThemeSelection(ctx, theme);
        themeChanged = true;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Log.e("ThemeSelectorActivity", "%%% onBackPressed, themeChanged: " + themeChanged);
        if (themeChanged) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_theme_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
