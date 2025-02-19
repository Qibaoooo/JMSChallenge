package nus.iss.sa57.csc_android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import nus.iss.sa57.csc_android.AccountActivity;
import nus.iss.sa57.csc_android.MainActivity;
import nus.iss.sa57.csc_android.R;

public class NavigationBarHandler {
    View bar;
    Context context;
    public NavigationBarHandler(final Context context) {
        this.context = context;
        bar = ((Activity)context).findViewById(R.id.nav_bar);
    }
    public void setupBar(){
        setupCat();
        setupAccount();
    }

    public void setupCat(){
        bar.findViewById(R.id.nav_cat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
    }

    public void setupAccount(){
        bar.findViewById(R.id.nav_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountActivity.class);
                context.startActivity(intent);
            }
        });
    }
}