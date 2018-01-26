package com.tinder.edison.tinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.tinder.edison.tinder.Cards.arrayAdapter;
import com.tinder.edison.tinder.Cards.cards;
import com.tinder.edison.tinder.Matches.MatchesActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private cards card_data[];
    private com.tinder.edison.tinder.Cards.arrayAdapter arrayAdapter;
    //private ArrayAdapter<String> arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    SwipeFlingAdapterView flingContainer;
    private String currentUid;
    private DatabaseReference userDb;
    private DatabaseReference userDb_LD;

    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems);



        flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                cards obj =(cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("disliked").child(currentUid).setValue(true);
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj =(cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("liked").child(currentUid).setValue(true);


                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                Toast.makeText(MainActivity.this, "OOOH", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(String userId) {

        DatabaseReference currentUserConnectionsDb = userDb.child(currentUid).child("connections").child("liked").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "You found a match!", Toast.LENGTH_SHORT).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                    //userDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).setValue(true);
                    userDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).child("ChatId").setValue(key);

                    //userDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    userDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals(user.getUid())){
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("sex") != null){
                            userSex = dataSnapshot.child("sex").getValue().toString();
                            oppositeUserSex = "Female";
                            switch (userSex){
                                case "Male":
                                    oppositeUserSex = "Female";
                                    break;
                                case "Female":
                                    oppositeUserSex = "Male";
                                    break;


                            }
                            getOppositeSexUsers();
                        }

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void getOppositeSexUsers(){
        //DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference().child("Users");
        userDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //condition which determines what cards to show Currently : show all profile that exists & not matched
                //filter conditions
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("disliked").hasChild(currentUid) && !dataSnapshot.child("connections").child("liked").hasChild(currentUid) && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)){
                        String profileImageUrl = "default";
                    if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){

                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }

                    cards Item = new cards(dataSnapshot.getKey(), String.valueOf(dataSnapshot.child("name").getValue()), profileImageUrl);
                    rowItems.add(Item);
                    arrayAdapter.notifyDataSetChanged();

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

    public void logoutUser(View v){
        mAuth.signOut();
        Intent i = new Intent(MainActivity.this, ChooseLoginOrRegistrationActivity.class);
        startActivity(i);
        finish();
        return;



    }


    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

        startActivity(intent);
        finish();
        return;

    }

    public void goToMatches(View view) {
            Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
            startActivity(intent);

            return;

    }
}
