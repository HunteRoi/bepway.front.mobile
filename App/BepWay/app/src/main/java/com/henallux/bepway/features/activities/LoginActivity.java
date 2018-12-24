package com.henallux.bepway.features.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.TokenDAO;
import com.henallux.bepway.features.adapters.SectionsPageAdapter;
import com.henallux.bepway.features.fragment.GuestFragment;
import com.henallux.bepway.features.fragment.LogFragment;
import com.henallux.bepway.features.fragment.RegisterFragment;
import com.henallux.bepway.model.Token;


public class LoginActivity extends FragmentActivity {

    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString("LoginModel","hoiho")
                .apply();

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("LoginModel","Not found");*/

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.loginViewPager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.loginTab);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new GuestFragment(), getString(R.string.title_tab_guest));
        adapter.addFragment(new LogFragment(), getString(R.string.title_tab_login));
        viewPager.setAdapter(adapter);
    }

}
