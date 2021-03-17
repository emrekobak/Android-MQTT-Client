package com.example.mqttcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;


class MqttClass {


    public MqttClass(Context context) {
        this.context = context;
    }

    private Context context;
    static MqttAndroidClient client;
    FirebaseAuth mAuth;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();


    void Disconnect(final Context context) {
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(context, "Oturum Kapatıldı.", Toast.LENGTH_SHORT).show(); //hata

                    //  Log.d("client",client.toString());/////////////
                    ///////
                    /////BURAAAAAA calısıyor

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ConUser(final Context context, final String server, final String user, final String passwd, final String serverName) {

        MqttConnectOptions opt = new MqttConnectOptions();
        opt.setCleanSession(true);
        opt.setAutomaticReconnect(true);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, server, clientId);

        try {
            opt.setUserName(user);
            opt.setPassword(passwd.toCharArray());
            IMqttToken token;
            token = client.connect(opt);//my options
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Intent intent = new Intent(context, ControlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "Bağlandı", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Hata! Bilgileri Kontrol ediniz." + server, Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void ReConUser(final Context context, final String server, String user, final String passwd) {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, server, clientId);
        if (!client.isConnected()) {
            MqttConnectOptions opt = new MqttConnectOptions();
            opt.setCleanSession(true);
            opt.setAutomaticReconnect(true);

            try {

                opt.setUserName(user);
                opt.setPassword(passwd.toCharArray());
                IMqttToken token;
                token = client.connect(opt);//my options
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Intent intent = new Intent(context, ControlActivity.class);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (!TextUtils.isEmpty(passwd)) {
                            Toast.makeText(context, "HATA bilgilerde değişiklik" + server, Toast.LENGTH_SHORT).show();
                            ConState();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    void ReConNotUser(final Context context, final String server) {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, server, clientId);
        if (!client.isConnected()) {
            MqttConnectOptions opt = new MqttConnectOptions();
            opt.setCleanSession(true);
            opt.setAutomaticReconnect(true);

            try {
                IMqttToken token;
                token = client.connect(opt);//my options
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // Connected
                        Toast.makeText(context, "connected" + client, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                        Toast.makeText(context, "Error" + server, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }



    }
    void ConNotUser(final Context context, final String server) {
      /*  if (TextUtils.isEmpty(txtAddress.getText())) { //düzelt server ayrı hata
            AlertDialog alertDialog   = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Uyarı"

            "Sunucu adresi alanı boş bırakılamaz.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            txtAddress.setError("Lütfen bu alanı doldurunuz");
        } */

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, server,
                clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Intent intent = new Intent(context, ControlActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(context, "not user okey" + server, Toast.LENGTH_SHORT).show();
                    // Connected
                    // AuthAnonymous();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    Toast.makeText(context, "error son" + server, Toast.LENGTH_SHORT).show();

                    // Something went wrong e.g. connection timeout or firewall problems


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ManageUsers() {


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        Task<QuerySnapshot> ref = fireDB.collection("Users")
                .whereEqualTo("UserID", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult())
                                document.getReference().delete();

                        } else {
                            Toast.makeText(context, "hata manage", Toast.LENGTH_LONG).show();
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void ConState() {
        if (!client.isConnected()) {


            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            ManageUsers();
            currentUser.delete();
            mAuth.signOut();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

        }


    }


}










