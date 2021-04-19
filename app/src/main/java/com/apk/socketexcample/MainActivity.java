package com.apk.socketexcample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static Socket mSocket;
    public static Emitter.Listener mSocketNewMsgListener;
    EditText messageEt;
    TextView messageTv;
    Button sendButton;

    {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            mSocket = IO.socket("https://socket.thetripsterapp.com", opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageEt = findViewById(R.id.messageEt);
        messageTv = findViewById(R.id.messageTv);
        sendButton = findViewById(R.id.sendButton);

        //socket connect
        mSocket.connect();

        //socket join
        joinSocket("2");

        //for get data
        mSocketNewMsgListener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(args[0] + "");
                            Log.e(TAG, "messageReceived: " + jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        mSocketNewMsgListener.call();
        mSocket.on("newMessage", mSocketNewMsgListener);


        //send data
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage(messageEt.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void joinSocket(String channel_id) {
        mSocket.emit("join", channel_id);
    }


    public void sendMessage(String message) throws JSONException {
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("message", message);//what data send
        messageJSON.put("channel_id", "2");//The channel id to send
        mSocket.emit("createMessage", messageJSON.toString());
    }
}