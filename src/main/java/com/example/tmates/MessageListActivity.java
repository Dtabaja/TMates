package com.example.tmates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import javax.annotation.Nullable;


public class MessageListActivity extends AppCompatActivity {
    private ArrayList<Message> messageList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView noMessageYetStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        noMessageYetStr = findViewById(R.id.noMessageYetStr);
    }

    @Override
    public void onResume() {
        super.onResume();
        CollectionReference colRef = db.collection("messages");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(recyclerView != null){
                    recyclerView.removeAllViewsInLayout();
                }
                messageList.clear();
                getDataFromFireBase();

            }
        });
    }

    // Method that init the recycler view.
    public void initRecyclerView(){
        recyclerView = findViewById(R.id.messagesRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MessageListAdapter(messageList);
        recyclerView.setAdapter(mAdapter);
    }

    // Method that get information from the firebase.
    public void getDataFromFireBase(){
        final CollectionReference collectionReference = db.collection("messages");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Message message = document.toObject(Message.class);
                        if(message.getReceiverId().compareTo(mAuth.getCurrentUser().getUid()) == 0){
                            messageList.add(message);
                        }
                    }
                    Collections.sort(messageList);
                    if(!messageList.isEmpty()){
                        noMessageYetStr.setVisibility(View.INVISIBLE);
                        initRecyclerView();
                    } else {
                        noMessageYetStr.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

}
