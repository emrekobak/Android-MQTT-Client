package com.example.mqttcontrolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;




public class ControlActivity extends AppCompatActivity {


    MqttClass mqttClass = new MqttClass(ControlActivity.this);
    private FirebaseAuth mAuth;
    FragmentClass fragmentClass = new FragmentClass(ControlActivity.this);
    Button btnSub;
    Button btnPub;


    FirebaseFirestore fireDB=FirebaseFirestore.getInstance();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mqttClass.Disconnect(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mqttClass.ManageUsers();
        currentUser.delete();
        mAuth.signOut();
        Intent intent = new Intent(ControlActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        mAuth = FirebaseAuth.getInstance();
        if (savedInstanceState == null) {

            fragmentClass.ChangeFragment(new InfoFragment());
            LayoutName();

        }

        btnPub = findViewById(R.id.btnPublish);
        btnSub = findViewById(R.id.btnSubscribe);
        final Drawable drawPub = btnPub.getBackground();
        final Drawable drawSub = btnSub.getBackground();
        btnPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentClass.ChangeFragment(new PublishFragment());
               // btnPub.setBackground(getResources().getDrawable(R.drawable.border));
               // btnSub.setBackground(drawSub);
                setTitle("Yayınla");

            }
        });
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentClass.ChangeFragment(new SubscribeFragment());
               // btnSub.setBackground(getResources().getDrawable(R.drawable.border));
                //btnPub.setBackground(drawPub);
                setTitle("Abone ol");

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//menü Bağlantı fonk.

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cont_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//menu seçim durumu
        if (item.getItemId() == R.id.log_out) {
            try {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                mqttClass.ManageUsers();
                currentUser.delete();
                mAuth.signOut();
                Intent intent = new Intent(ControlActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                mqttClass.Disconnect(getApplicationContext());


            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (item.getItemId() == R.id.info) {
            try {
                fragmentClass.ChangeFragment(new InfoFragment());
                LayoutName();
               // btnPub.setBackgroundResource(android.R.drawable.btn_default_small);
               // btnSub.setBackgroundResource(android.R.drawable.btn_default_small);



            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        return super.onOptionsItemSelected(item);
    }
    void LayoutName() {
        fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String servername = (String) document.get("ServerName");
                                setTitle(servername);



                            }
                        } else {
                            setTitle("hata");
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


}




