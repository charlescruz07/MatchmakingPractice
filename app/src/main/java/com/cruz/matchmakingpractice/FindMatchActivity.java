package com.cruz.matchmakingpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindMatchActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private Button findMatchBtn;
    private ListView onlineList;
    private ArrayList<String> onlineUsers = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_match);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
//        final DatabaseReference myConnectionsRef = firebaseDatabase.getReference("users/joe/connections");
        mRef = firebaseDatabase.getReference();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
//                    DatabaseReference con = myConnectionsRef.push();
//                    DatabaseReference con = mRef.child("Online Users").child(mUser.getUid()).push();
                    DatabaseReference con = mRef.child("Online Users").child(mUser.getUid());
                    con.setValue(mUser.getDisplayName());

                    con.onDisconnect().removeValue();
                } else {
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Listener was cancelled");
            }
        });

        Query query = mRef.child("Online Users");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String str = dataSnapshot.getValue().toString();
                onlineUsers.add(str);
                adapter.notifyDataSetChanged();
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

        findMatchBtn = (Button) findViewById(R.id.findMatchBtn);
        onlineList = (ListView) findViewById(R.id.onlineList);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                onlineUsers);

        onlineList.setAdapter(adapter);
    }
}
