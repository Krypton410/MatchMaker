package com.match.edison.match;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MatchesActivity extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_matches, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle sasvedInstanceState) {
        super.onActivityCreated(null);
    }
}
