package com.henallux.bepway.features.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.henallux.bepway.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.nav_list :
                                Intent intentList = new Intent(MainActivity.this, ZoningsActivity.class);
                                startActivity(intentList);
                                break;
                            case R.id.nav_map :
                                Intent intentMap = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intentMap);
                                break;
                            case R.id.nav_disconnect:
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext().getApplicationContext())
                                .edit()
                                .remove("Token")
                                .remove("Login")
                                .remove("Password")
                                .apply();
                                Intent intentDisconnect = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intentDisconnect);
                                break;
                            default : break;
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default : return super.onOptionsItemSelected(item);
        }
    }
}
