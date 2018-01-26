package com.tinder.edison.tinder.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tinder.edison.tinder.Matches.MatchesActivity;
import com.tinder.edison.tinder.Matches.MatchesAdapter;
import com.tinder.edison.tinder.Matches.MatchesObject;
import com.tinder.edison.tinder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID, matchId, chatId;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    private EditText mSendEditText;
    private Button mSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        matchId = getIntent().getExtras().getString("matchId");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");

        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");


        mRecyclerView = findViewById(R.id.recyclerView1);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
        getChatId();

        mSendButton = findViewById(R.id.send);
        mSendEditText = findViewById(R.id.message);
        
        
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);
            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);
    }
    private void getChatId(){
         mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     chatId = dataSnapshot.getValue().toString();
                     mDatabaseChat = mDatabaseChat.child(chatId);
                     getChatMessages();
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;



                    if(dataSnapshot.child("text").getValue() != null){
                     message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue() != null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(message != null && createdByUser != null){

                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUserID)){
                                currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        Toast.makeText(ChatActivity.this, dataSnapshot.child("text").getValue().toString(), Toast.LENGTH_SHORT).show();


                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<ChatObject> resultChat = new ArrayList<>();
    private List<ChatObject> getDataSetChat(){return resultChat;}
}
