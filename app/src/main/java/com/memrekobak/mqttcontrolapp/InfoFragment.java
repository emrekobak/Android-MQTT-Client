package com.memrekobak.mqttcontrolapp;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class InfoFragment extends Fragment {

    View view;
    private FirebaseAuth mAuth;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();
    ListView listInfo;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ListInfo(getContext());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);

        return view;
    }


    void ListInfo(final Context context) {
        mAuth = FirebaseAuth.getInstance();
        fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = (String) document.get("UserID");
                                String serverAddress = (String) document.get("ServerAddress");
                                String servername = (String) document.get("ServerName");
                                String user = (String) document.get("ServerUser");
                                listInfo = view.findViewById(R.id.listInfo);
                                arrayList = new ArrayList<>();
                                arrayList.add("User ID: " + userId);
                                arrayList.add("Server Name: " + servername);
                                arrayList.add("Server Address: " + serverAddress);
                                arrayList.add("User: " + user);

                                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
                                listInfo.setAdapter(adapter);


                            }
                        }
                    }
                });

    }


}
