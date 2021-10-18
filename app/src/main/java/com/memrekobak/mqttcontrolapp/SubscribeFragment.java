package com.memrekobak.mqttcontrolapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.memrekobak.mqttcontrolapp.R;
import com.google.android.material.textfield.TextInputLayout;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;


public class SubscribeFragment extends Fragment {
    View view;
    Button btnSub;
    Button btnUnsub;
    TextInputLayout txtSubtopic;
    Spinner qosSpin;
    ListView listInfo;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    SeekBar seekBar;
    CheckBox checkBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        Subscribe();
        return view;
    }


    void Subscribe() {
        btnSub = view.findViewById(R.id.btnSub);
        btnUnsub = view.findViewById(R.id.btnUnSub);
        listInfo = view.findViewById(R.id.listMessage);
        arrayList = new ArrayList<>();
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sub(getContext());
            }
        });
        btnUnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnSub();
            }
        });
    }

    private void Sub(final Context context) {

        txtSubtopic = view.findViewById(R.id.topicSub);
        String topicPub = txtSubtopic.getEditText().getText().toString();
        qosSpin = view.findViewById(R.id.qos);
        String qosMesssage = qosSpin.getSelectedItem().toString();
        final int qos = Integer.parseInt(qosMesssage);
        seekBar = view.findViewById(R.id.seekSub);
        seekBar.setMax(100);
        checkBar = view.findViewById(R.id.checkBar);
        if (checkBar.isChecked()) {
            seekBar.setVisibility(View.VISIBLE);
        } else {
            seekBar.setVisibility(View.INVISIBLE);
        }
        try {
            MqttClass.client.subscribe(topicPub, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        MqttClass.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                arrayList.add(0, "Message: " + new String(message.getPayload()) + " " + "Topic: " + topic);
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);   //////qos eklenecek. mesaj sırası tamam.
                listInfo.setAdapter(adapter);
                int value = Integer.parseInt(message.toString());
                seekBar.setProgress(value);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    private void UnSub() {

        txtSubtopic = view.findViewById(R.id.topicSub);
        final String topic = txtSubtopic.getEditText().getText().toString();
        try {
            IMqttToken unsubToken = MqttClass.client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }


}
