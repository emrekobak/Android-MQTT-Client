package com.memrekobak.mqttcontrolapp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.memrekobak.mqttcontrolapp.R;


public class FragmentClass {
    private Context context;




    public FragmentClass(Context context){
        this.context=context;
    }



    public void ChangeFragment(Fragment fragment) {
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();




    }



}
