package com.example.mqttcontrolapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    EditText txtUser;
    EditText txtPasswd;
    EditText txtAddress;
    EditText txtPort;
    EditText txtName;
    Button button;
    Switch userSw;
    Switch mosqSw;
    String username;
    String passwd;
    String serverAdr;
    String serverName;
    String port;
    private FirebaseAuth mAuth;
    MqttClass mqttClass = new MqttClass(MainActivity.this);
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            if (currentUser != null) {
                ControlAct();

            }
        } else {
            Toast.makeText(MainActivity.this, "Lütfen internet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show();
        }

        txtUser = findViewById(R.id.txtUser);
        txtPasswd = findViewById(R.id.txtPasswd);
        txtAddress = findViewById(R.id.txtAddress);
        txtPort = findViewById(R.id.txtPort);
        txtName = findViewById(R.id.txtName);
        userSw = findViewById(R.id.userSw);
        mosqSw = findViewById(R.id.mosqSw);

        mosqSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mosqSw.setChecked(true);
                    txtName.setText("Mosquitto Sunucusu");
                    txtName.setSelection(txtName.getText().length());
                    txtAddress.setText("my.mqttapp.xyz");
                    txtPort.setText("8883");
                    serverAdr = "ssl://" + txtAddress.getText().toString() + ":" + txtPort.getText().toString();//"ssl://my.mqttapp.xyz:8883";;;
                    userSw.setChecked(true);
                    userSw.setClickable(false);
                    txtAddress.setEnabled(false);
                    txtPort.setEnabled(false);
                    txtName.requestFocus();
                    txtUser.getText().clear();
                    txtPasswd.getText().clear();

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Uyarı");
                    alertDialog.setMessage("Mosquitto sunucusuna yalnız tanımlı kullanıcılar bağlanabilir. Kullanıcı ve şifre bilgisi olmadan sunucuya bağlantı sağlanamaz.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else {
                    txtAddress.setEnabled(true);
                    txtPort.setEnabled(true);
                    txtAddress.getText().clear();
                    txtName.getText().clear();
                    txtPort.getText().clear();
                    txtUser.getText().clear();
                    txtPasswd.getText().clear();
                    userSw.setClickable(true);
                    txtName.requestFocus();
                }

            }
        });

        userSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtUser.setVisibility(View.VISIBLE);
                    txtPasswd.setVisibility(View.VISIBLE);
                    txtName.getText().clear();
                    txtAddress.getText().clear();
                    txtPort.getText().clear();
                    txtName.requestFocus();
                } else {
                    txtUser.setVisibility(View.INVISIBLE);
                    txtPasswd.setVisibility(View.INVISIBLE);
                    txtName.getText().clear();
                    txtAddress.getText().clear();
                    txtPort.getText().clear();
                    txtUser.getText().clear();
                    txtPasswd.getText().clear();
                    txtName.requestFocus();


                }

            }
        });


        button = (Button) findViewById(R.id.btnConnect);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                username = txtUser.getText().toString();
                passwd = txtPasswd.getText().toString();
                serverName = txtName.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mqttClass.ManageUsers();
                    currentUser.delete();
                    mAuth.signOut();
                }
                if (mosqSw.isChecked()) {
                    ConMosquitto();

                } else if (!mosqSw.isChecked() && userSw.isChecked()) {
                    txtName.getText().clear();
                    ConUserpass();


                } else {

                    ConNotUserpass();

                }


            }

        });


    }

    void AuthAnonymous() {

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            HashMap<String, Object> userMap = new HashMap<>();

                            userMap.put("UserID", mAuth.getUid());
                            userMap.put("ServerName", serverName);
                            userMap.put("ServerAddress", serverAdr);
                            userMap.put("ServerUser", username);
                            userMap.put("UserPassword", passwd);
                            userMap.put("Mosquitto", mosqSw.isChecked());
                            userMap.put("PasswordAuth", userSw.isChecked());

                            fireDB.collection("Users").document().set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {


                                        }
                                    });


                        }
                    }
                });


    }


    void ConMosquitto() {
        if (TextUtils.isEmpty(txtUser.getText()) || TextUtils.isEmpty(txtPasswd.getText()) || TextUtils.isEmpty(txtAddress.getText())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Uyarı");
            alertDialog.setMessage("Kullanıcı ve parola doğrulaması ile bağlanmayı seçtiniz. Gerekli Alanları lütfen doldurunuz.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if (TextUtils.isEmpty(txtUser.getText()) && TextUtils.isEmpty(txtPasswd.getText())) {
                txtUser.setError("Lütfen bu alanı doldurunuz!");
                txtPasswd.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtUser.getText())) {
                txtUser.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtPasswd.getText())) {
                txtPasswd.setError("Lütfen bu alanı doldurunuz!");
            }

        } else {

            AuthAnonymous();
            mqttClass.ConUser(getApplicationContext(), serverAdr, username, passwd, serverName);

        }
    }

    void ConUserpass() {
        if (TextUtils.isEmpty(txtUser.getText()) || TextUtils.isEmpty(txtPasswd.getText()) || TextUtils.isEmpty(txtAddress.getText()) || TextUtils.isEmpty(txtPort.getText())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Uyarı");
            alertDialog.setMessage("Lütfen gerekli alanları doldurun.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if (TextUtils.isEmpty(txtUser.getText()) && TextUtils.isEmpty(txtPasswd.getText()) && TextUtils.isEmpty(txtPort.getText()) && TextUtils.isEmpty(txtAddress.getText())) {
                txtUser.setError("Lütfen bu alanı doldurunuz!");
                txtPasswd.setError("Lütfen bu alanı doldurunuz!");
                txtPort.setError("Lütfen bu alanı doldurunuz!");
                txtAddress.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtAddress.getText())) {
                txtAddress.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtPort.getText())) {
                txtPort.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtUser.getText())) {
                txtUser.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtPasswd.getText())) {
                txtPasswd.setError("Lütfen bu alanı doldurunuz!");
            }

        } else {
            serverAdr = txtAddress.getText().toString() + ":" + txtPort.getText().toString();//"ssl://my.mqttapp.xyz:8883"
            AuthAnonymous();
            mqttClass.ConUser(getApplicationContext(), serverAdr, username, passwd, serverName);

        }
    }

    void ConNotUserpass() {


        if (TextUtils.isEmpty(txtAddress.getText()) || TextUtils.isEmpty(txtPort.getText())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Uyarı");
            alertDialog.setMessage("Lütfen gerekli alanları doldurun.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if (TextUtils.isEmpty(txtAddress.getText()) && TextUtils.isEmpty(txtPort.getText())) {
                txtUser.setError("Lütfen bu alanı doldurunuz!");
                txtPasswd.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtAddress.getText())) {
                txtAddress.setError("Lütfen bu alanı doldurunuz!");
            }
            if (TextUtils.isEmpty(txtPort.getText())) {
                txtPort.setError("Lütfen bu alanı doldurunuz!");
            }
        } else {
            serverAdr = txtAddress.getText().toString() + ":" + txtPort.getText().toString();//"ssl://my.mqttapp.xyz:8883"
            AuthAnonymous();
            mqttClass.ConNotUser(getApplicationContext(), serverAdr);

        }

    }

    void ControlAct() {

        fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String serverAddress = (String) document.get("ServerAddress");
                                String user = (String) document.get("ServerUser");
                                String password = (String) document.get("UserPassword");
                                Boolean mosquitto = (boolean) document.get("Mosquitto");
                                Boolean passwdAuth = (boolean) document.get("PasswordAuth");

                                if (mosquitto || passwdAuth) {
                                    mqttClass.ReConUser(getApplicationContext(), serverAddress, user, password);

                                } else if (!passwdAuth) {
                                    mqttClass.ReConNotUser(getApplicationContext(), serverAddress);
                                }

                            }
                        }
                    }
                });

    }
}





