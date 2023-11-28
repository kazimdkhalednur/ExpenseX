package com.example.expensex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.expensex.fragments.DashboardFragment;
import com.example.expensex.fragments.ExpenseFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frameLayoutID);
        bottomNavigationView = findViewById(R.id.navigationID);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigationDashboardID) {
                    loadFragment(new DashboardFragment(), false);
                } else if(id == R.id.navigationExpensesID){
                    loadFragment(new ExpenseFragment(), false);
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navigationDashboardID);
    }

    public void loadFragment(Fragment fragment, Boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag){
            ft.add(R.id.frameLayoutID, fragment);
        } else{
            ft.replace(R.id.frameLayoutID, fragment);
        }
        ft.commit();
    }
}