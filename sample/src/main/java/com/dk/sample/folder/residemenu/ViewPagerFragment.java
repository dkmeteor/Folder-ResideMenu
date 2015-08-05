package com.dk.sample.folder.residemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ViewPagerFragment extends android.support.v4.app.Fragment {
    private ViewPager mPager;

    public ViewPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        mPager = (ViewPager) view.findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(adapter);
        setUpViews();
        return view;
    }

    private void setUpViews() {
    }

   private static class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Fragment f=null;
            switch(position)
            {
                case 0:
                    f = new CalendarFragment();
                    break;
                case 1:
                    f = new CalendarFragment();
                    break;
                case 2:
                    f = new CalendarFragment();
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

