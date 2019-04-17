package com.example.garbageremover;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.garbageremover.Adapter.FragmentViewPagerAdapter;
import com.example.garbageremover.Fragments.FragmentWithCleanRequest;
import com.example.garbageremover.Fragments.SearchUsersFragment;
import com.example.garbageremover.Fragments.UserProfileFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        viewPager = findViewById(R.id.fragment_container);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                    bottomNavigationView.getMenu().getItem(i).setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        FragmentWithCleanRequest fragmentWithCleanRequest = new FragmentWithCleanRequest();
        SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        adapter.addFragments(userProfileFragment);
        adapter.addFragments(fragmentWithCleanRequest);
        adapter.addFragments(searchUsersFragment);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(adapter);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.item_user_profile:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.item_request_list:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.item_search_users:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        }
    };
}
