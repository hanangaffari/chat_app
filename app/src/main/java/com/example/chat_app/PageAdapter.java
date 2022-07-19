package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chat_app.status.statusfragment;

public class PageAdapter extends FragmentPagerAdapter {
    int tabCount;

    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new chatfragment();

            case 1:
                return new statusfragment();

            case 2:
                return new callfragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
