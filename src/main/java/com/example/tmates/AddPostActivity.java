package com.example.tmates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText postTitleEditText, postDescriptionEditText;
    private Button confirmPostBtn, cancelPostBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;
    private Handler handler = new Handler();
    private ProgressBar addPostProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        findById();
        confirmPostBtn.setOnClickListener(this);
        cancelPostBtn.setOnClickListener(this);

        DocumentReference documentReference = db.collection("users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.confirmPostBtn:
                addPostProgressBar.setVisibility(View.VISIBLE);
                confirmPostBtn.setClickable(false);
                cancelPostBtn.setClickable(false);
                if(checkFormValidation(postTitleEditText, postDescriptionEditText)){
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    CollectionReference postsRef = db.collection("posts");
                    String id = postsRef.document().getId();
                    Post newPost = new Post(user, postTitleEditText.getText().toString(), postDescriptionEditText.getText().toString(), date, id, user.getUid());
                    db.collection("posts").document(id).set(newPost);
                    Toast.makeText(AddPostActivity.this,"Posted successfully.", Toast.LENGTH_LONG).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                } else {
                    confirmPostBtn.setClickable(true);
                    cancelPostBtn.setClickable(true);
                    addPostProgressBar.setVisibility(View.GONE);
                }
                break;

            case R.id.cancelPostBtn:
                confirmPostBtn.setClickable(false);
                cancelPostBtn.setClickable(false);
                finish();
                break;
        }
    }

    // Method that check the add post form.
    public boolean checkFormValidation(EditText postTitleEditText, EditText postDescriptionEditText){
        if(postTitleEditText.getText().toString().trim().length() == 0){
            Toast.makeText(AddPostActivity.this,"Please enter post title.", Toast.LENGTH_LONG).show();
            return false;
        } else if(postDescriptionEditText.getText().toString().trim().length() == 0){
            Toast.makeText(AddPostActivity.this,"Please enter post description.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        confirmPostBtn.setClickable(true);
        cancelPostBtn.setClickable(true);
        addPostProgressBar.setVisibility(View.GONE);
    }

    // Find views by id method.
    private void findById() {
        postTitleEditText = findViewById(R.id.postTitleEditText);
        postDescriptionEditText = findViewById(R.id.postDescriptionEditText);
        confirmPostBtn = findViewById(R.id.confirmPostBtn);
        cancelPostBtn = findViewById(R.id.cancelPostBtn);
        addPostProgressBar = findViewById(R.id.addPostProgressBar);
    }
}
