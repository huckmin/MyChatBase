package com.example.mac.mychatbase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<ItemData> itemList = new ArrayList<ItemData>();
    private ImageButton sendButton;
    private EditText sendText;
    private Socket socket;
    private Boolean isConnected = true;
    private String mUsername = "himan";
    private RadioGroup sendcho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSocket();

        sendButton = (ImageButton) findViewById(R.id.send_button);
        sendText = (EditText) findViewById(R.id.message_input);
        sendcho = (RadioGroup) findViewById(R.id.howsend);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_continer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //layoutManager를 통해서 여러가지 형태로 구현할수 있다.

        mAdapter = new MyAdapter(getApplicationContext(),itemList); //list를 넘겨준다.
        recyclerView.setAdapter(mAdapter);

        sendcho.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioButton:
                        break;
                    case R.id.radioButton2:
                        joinRoom();
                        break;
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSend();
            }
        });

        //파람 앞에 인자는 날아온 정보
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("new messages",addMessage);


        socket.connect();

    }

    private void createSocket(){
        try {
            socket = IO.socket("https://chat-server-kkmin.c9users.io");
        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        }

    }

    private void sendMessage(String message){
        itemList.add(new ItemData(ItemData.TYPE_COL_ONE,message));
        mAdapter.notifyItemInserted(itemList.size()-1);
        scrollToBottom();
    }

    private void catchMessage(String mess){
        Log.i("catchMessage  ", mess);
        itemList.add(new ItemData(ItemData.TYPE_COL_TWO,mess));
        mAdapter.notifyItemInserted(itemList.size()-1);
        scrollToBottom();
    }

    private void attemptSend(){
        if(!socket.connected()) return;

        //room 인지 아닌지 분기
        if(sendcho.getCheckedRadioButtonId() == R.id.radioButton2){

            JSONObject jsonObject = new JSONObject();
            String mess = sendText.getText().toString().trim();
            sendText.setText("");
            try {
                //roomName은 가변적이지만 여기선 고정
                jsonObject.put("roomName", "test1room");
                jsonObject.put("message", mess);
            }catch(Exception e){
                e.printStackTrace();
            }
            //메세지를 send 하고 내 채팅창에도 출력
            sendMessage(mess);
            Log.e("mess ", mess);
            //보낼 내용을 new message로 정의 하여 전송
            socket.emit("roomMessage", jsonObject);
        }else {

            String mess = sendText.getText().toString().trim();
            sendText.setText("");
            //메세지를 send 하고 내 채팅창에도 출력
            sendMessage(mess);
            Log.e("mess ", mess);
            //보낼 내용을 new message로 정의 하여 전송
            socket.emit("new message", mess);
        }
    }

    private void joinRoom(){
        if(!socket.connected())return;
        Log.i("join Room", "join Room");
        socket.emit("joinRoom","test1room");

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            socket.emit("add user", mUsername);
                        Toast.makeText(getApplicationContext(),
                                "connect", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "error Connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener addMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try{
                        message = data.getString("message");
                        Log.e("addmessage : ", message);
                    }catch(JSONException e){
                        return;
                    }
                    catchMessage(message);
                }
            });
        }
    };


    private void scrollToBottom(){
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    };
}
