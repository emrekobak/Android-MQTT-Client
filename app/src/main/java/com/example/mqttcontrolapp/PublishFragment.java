package com.example.mqttcontrolapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.eclipse.paho.client.mqttv3.MqttException;


public class PublishFragment extends Fragment {
    View view;
    TextInputLayout txtTopic;
    TextInputLayout txtMessage;
    Spinner qosSpinPub;
    Switch retSw;
    Button btnPub;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_publish, container, false);
        Publish();
        return view;
    }


    private void Publish() {
        btnPub = view.findViewById(R.id.btnPub);
        btnPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pub(getContext());
            }
        });

    }


    private void Pub(Context context) {
        txtTopic = view.findViewById(R.id.topicPub);
        txtMessage = view.findViewById(R.id.messagePub);
        qosSpinPub = view.findViewById(R.id.qosPub);
        retSw = view.findViewById(R.id.retainSw);
        String qosPubmessage = qosSpinPub.getSelectedItem().toString();
        final int qos = Integer.parseInt(qosPubmessage);
        String topicPub = txtTopic.getEditText().getText().toString();
        String message = txtMessage.getEditText().getText().toString();
        boolean ret = retSw.isChecked();


        try {

            MqttClass.client.publish(topicPub, message.getBytes(), qos, ret);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}









