package com.rymo.felfel.common;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rymo.felfel.features.reports.fragments.GetMessagesReportFragment;
import com.rymo.felfel.features.reports.fragments.NotSendMessagesUserReportFragment;
import com.rymo.felfel.features.reports.fragments.SendMessagesReportFragment;

import java.util.ArrayList;


public class GlobalFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int REPORTS = 1;
    private final int count;
    private final int page;
    private ArrayList<Fragment> fragments;

    public GlobalFragmentPagerAdapter(FragmentManager manager, int count, int page) {
        super(manager);
        this.page = page;
        this.count = count;
    }

    public GlobalFragmentPagerAdapter(FragmentManager manager, int count, int page, ArrayList<Fragment> fragments) {
        super(manager);
        this.fragments = fragments;
        this.page = page;
        this.count = count;
    }


    @Override
    public Fragment getItem(int position) {
        switch (page) {

            case REPORTS:
                switch (position) {
                    case Constants.SEND_MESSAGES_REPORT:
                        return SendMessagesReportFragment.Companion.newInstance();

                    case Constants.GET_MESSAGES_REPORT:
                        return GetMessagesReportFragment.Companion.newInstance();

                    case Constants.NOT_GET_MESSAGES_USERS_REPORT:
                        return NotSendMessagesUserReportFragment.Companion.newInstance();

                }
                break;


        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}