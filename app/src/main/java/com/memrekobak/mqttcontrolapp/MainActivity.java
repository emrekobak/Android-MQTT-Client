package com.memrekobak.mqttcontrolapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    EditText txtUser;
    EditText txtPasswd;
    EditText txtAddress;
    EditText txtPort;
    EditText txtName;
    Button button;
    SwitchCompat userSw;
    SwitchCompat mosqSw;
    String username;
    String passwd;
    String serverAdr;
    String serverName;
    MqttClass mqttClass = new MqttClass(MainActivity.this);
    FirebaseUser user;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();
    boolean mosqsw;
    boolean usersw = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mqttClass.mAuth = FirebaseAuth.getInstance();
        user = mqttClass.mAuth.getCurrentUser();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            if (user != null) {
                ControlAct();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please check internet connection.", Toast.LENGTH_SHORT).show();
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
                    txtName.setText("Mosquitto Broker");
                    txtName.setSelection(txtName.getText().length());
                    txtAddress.setText("my.mqttapp.xyz");
                    txtPort.setText("8883");
                    serverAdr = "ssl://" + txtAddress.getText().toString() + ":" + txtPort.getText().toString();//"ssl://my.mqttapp.xyz:8883";;; ** currently out of service
                    userSw.setChecked(true);
                    userSw.setClickable(false);
                    txtAddress.setEnabled(false);
                    txtPort.setEnabled(false);
                    txtName.requestFocus();
                    txtUser.getText().clear();
                    txtPasswd.getText().clear();
                    mosqsw = true;
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Caution");
                    alertDialog.setMessage("Mosquitto broker is not available. If you want, you can make corrections to the code and add your own broker.");
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
                    mosqsw = false;
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
                    usersw = true;
                } else {
                    txtUser.setVisibility(View.INVISIBLE);
                    txtPasswd.setVisibility(View.INVISIBLE);
                    txtName.getText().clear();
                    txtAddress.getText().clear();
                    txtPort.getText().clear();
                    txtUser.getText().clear();
                    txtPasswd.getText().clear();
                    txtName.requestFocus();
                    usersw = false;


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
                if (user != null) {
                    mqttClass.ManageUsers();
                    user.delete();
                    mqttClass.mAuth.signOut();
                }
                if (mosqSw.isChecked()) {
                   // ConMosquitto();

                } else if (!mosqSw.isChecked() && userSw.isChecked()) {
                    txtName.getText().clear();
                    ConUserpass();


                } else {

                    ConNotUserpass();
                }

            }

        });


    }

//    void AuthAnonymous() {
//
//        mqttClass.mAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            HashMap<String, Object> userMap = new HashMap<>();
//
//                            userMap.put("UserID", mqttClass.mAuth.getUid());
//                            userMap.put("ServerName", serverName);
//                            userMap.put("ServerAddress", serverAdr);
//                            userMap.put("ServerUser", username);
//                            userMap.put("UserPassword", passwd);
//                            userMap.put("Mosquitto", mosqSw.isChecked());
//                            userMap.put("PasswordAuth", userSw.isChecked());
//
//                            fireDB.collection("Users").document().set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//
//                                }
//                            })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//
//
//                                        }
//                                    });
//
//
//                        }
//                    }
//                });
//
//
//    }


//    void ConMosquitto() {  ** Not Avaible **
//        if (TextUtils.isEmpty(txtUser.getText()) || TextUtils.isEmpty(txtPasswd.getText()) || TextUtils.isEmpty(txtAddress.getText())) {
//            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//            alertDialog.setTitle("Caution");
//            alertDialog.setMessage("You have chosen to connect with user and password authentication. Please fill in the required fields.\n");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
//            if (TextUtils.isEmpty(txtUser.getText()) && TextUtils.isEmpty(txtPasswd.getText())) {
//                txtUser.setError("Please fill this field!");
//                txtPasswd.setError("Please fill this field!");
//            }
//            if (TextUtils.isEmpty(txtUser.getText())) {
//                txtUser.setError("Please fill this field!");
//            }
//            if (TextUtils.isEmpty(txtPasswd.getText())) {
//                txtPasswd.setError("Please fill this field!");
//            }
//
//        } else {
//
//            AuthAnonymous();
//            mqttClass.ConUser(getApplicationContext(), serverAdr, username, passwd, serverName);
//
//        }
//    }

    void ConUserpass() {
        if (TextUtils.isEmpty(txtUser.getText()) || TextUtils.isEmpty(txtPasswd.getText()) || TextUtils.isEmpty(txtAddress.getText()) || TextUtils.isEmpty(txtPort.getText())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Caution");
            alertDialog.setMessage("Please fill in the required fields.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if (TextUtils.isEmpty(txtUser.getText()) && TextUtils.isEmpty(txtPasswd.getText()) && TextUtils.isEmpty(txtPort.getText()) && TextUtils.isEmpty(txtAddress.getText())) {
                txtUser.setError("Please fill this field!");
                txtPasswd.setError("Please fill this field!");
                txtPort.setError("Please fill this field!");
                txtAddress.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtAddress.getText())) {
                txtAddress.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtPort.getText())) {
                txtPort.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtUser.getText())) {
                txtUser.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtPasswd.getText())) {
                txtPasswd.setError("Please fill this field!");
            }

        } else {
            serverAdr = "tcp://" + txtAddress.getText().toString() + ":" + txtPort.getText().toString();
//            AuthAnonymous();
            mqttClass.ConUser(getApplicationContext(), serverAdr, username, passwd, serverName, mosqsw, usersw);

        }
    }

    void ConNotUserpass() {


        if (TextUtils.isEmpty(txtAddress.getText()) || TextUtils.isEmpty(txtPort.getText())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Caution");
            alertDialog.setMessage("Please fill in the required fields.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            if (TextUtils.isEmpty(txtAddress.getText()) && TextUtils.isEmpty(txtPort.getText())) {
                txtUser.setError("Please fill this field!");
                txtPasswd.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtAddress.getText())) {
                txtAddress.setError("Please fill this field!");
            }
            if (TextUtils.isEmpty(txtPort.getText())) {
                txtPort.setError("Please fill this field!");
            }
        } else {
            serverAdr = "tcp://" + txtAddress.getText().toString() + ":" + txtPort.getText().toString();
            mqttClass.ConNotUser(getApplicationContext(), serverAdr, serverName, mosqsw, usersw);

        }

    }

    void ControlAct() {

        fireDB.collection("Users")
                .whereEqualTo("UserID", mqttClass.mAuth.getUid())
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

                                } else {
                                    mqttClass.ReConNotUser(getApplicationContext(), serverAddress);
                                }

                            }
                        }
                    }
                });

    }
}





