package com.memrekobak.mqttcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import android.widget.Toast;

import androidx.annotation.NonNull;

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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.HashMap;
import java.util.concurrent.Executor;


class MqttClass {


    public MqttClass(Context context) {
        this.context = context;
    }

    private Context context;
    static MqttAndroidClient client;
    MainActivity mainActivity;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();


    void Disconnect(final Context context) {
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(context, "Disconnected.", Toast.LENGTH_SHORT).show();


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
    void AuthAnonymous(final String servername, final String serverAdr, final String username, final String passwd, final boolean mosq, final boolean user) {

        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            HashMap<String, Object> userMap = new HashMap<>();

                            userMap.put("UserID", mAuth.getUid());
                            userMap.put("ServerName", servername);
                            userMap.put("ServerAddress", serverAdr);
                            userMap.put("ServerUser", username);
                            userMap.put("UserPassword", passwd);
                            userMap.put("Mosquitto", mosq);
                            userMap.put("PasswordAuth", user);

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


    void ConUser(final Context context, final String server, final String user, final String passwd, final String serverName,final boolean mosqSw,final boolean userSw) {

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
                    AuthAnonymous(serverName,server,user,passwd,mosqSw,userSw);
                    Intent intent = new Intent(context, ControlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Fail! Check Information", Toast.LENGTH_SHORT).show();

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
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (!TextUtils.isEmpty(passwd)) {
                            Toast.makeText(context, "Fail! Check Information", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(context, ControlActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                        Toast.makeText(context, "Fail! Check Information", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }



    }
    void ConNotUser(final Context context, final String server, final String serverName,final boolean mosqSw,final boolean userSw) {

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, server,
                clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    AuthAnonymous(serverName,server,null,null,mosqSw,userSw);
                    Intent intent = new Intent(context, ControlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                    // Connected
                    // AuthAnonymous();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    Toast.makeText(context, "Fail! Check Information", Toast.LENGTH_SHORT).show();

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
        assert currentUser != null;
        Task<QuerySnapshot> ref = fireDB.collection("Users")
                .whereEqualTo("UserID", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult())
                                document.getReference().delete();

                        } else {
                            Toast.makeText(context, "Fail! Manage Users", Toast.LENGTH_SHORT).show();
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










