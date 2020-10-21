package com.example.tmates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
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
import java.util.regex.Pattern;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private User curUser;
    private String otherUserId;
    private EditText messageTitleEditText, messageDescriptionEditText, messageEmailEditText, messagePhoneEditText;
    private Button confirmMessageBtn, cancelMessageBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    private ProgressBar sendMessageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findById();
        if(getIntent().getExtras() != null){
            otherUserId = getIntent().getExtras().getString("otherUserId");
            getCurUser(mAuth.getCurrentUser().getUid());
        }
        confirmMessageBtn.setOnClickListener(this);
        cancelMessageBtn.setOnClickListener(this);
    }

    // Find views by id method.
    public void findById(){
        messageTitleEditText = findViewById(R.id.messageTitleEditText);
        messageDescriptionEditText = findViewById(R.id.messageDescriptionEditText);
        messageEmailEditText = findViewById(R.id.messageEmailEditText);
        messagePhoneEditText = findViewById(R.id.messagePhoneEditText);
        confirmMessageBtn = findViewById(R.id.confirmMessageBtn);
        cancelMessageBtn = findViewById(R.id.cancelMessageBtn);
        sendMessageProgressBar = findViewById(R.id.sendMessageProgressBar);
    }

    // Method that check the send message form.
    public boolean checkFormValidation(EditText messageTitleEditText, EditText messageDescriptionEditText, EditText messageEmailEditText, EditText messagePhoneEditText){
        if(messageTitleEditText.getText().toString().trim().length() == 0){
            Toast.makeText(SendMessageActivity.this,"Please enter message title.", Toast.LENGTH_LONG).show();
            return false;
        } else if(messageDescriptionEditText.getText().toString().trim().length() == 0){
            Toast.makeText(SendMessageActivity.this,"Please enter message description.", Toast.LENGTH_LONG).show();
            return false;
        } else if((messageEmailEditText.getText().toString().trim().length() == 0) && (messagePhoneEditText.getText().toString().trim().length() == 0)){
            Toast.makeText(SendMessageActivity.this,"Please fill at least one of contact information.", Toast.LENGTH_LONG).show();
            return false;
        } else if(messageEmailEditText.getText().toString().trim().length() != 0){
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if(!pattern.matcher(messageEmailEditText.getText().toString()).matches()) {
                Toast.makeText(SendMessageActivity.this,"Please enter valid email address.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    // Method that get the current user information from the firebase.
    public void getCurUser(String id){
        DocumentReference documentReference = db.collection("users").document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        curUser = documentSnapshot.toObject(User.class);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirmMessageBtn:
                sendMessageProgressBar.setVisibility(View.VISIBLE);
                confirmMessageBtn.setClickable(false);
                cancelMessageBtn.setClickable(false);
                if(checkFormValidation(messageTitleEditText, messageDescriptionEditText, messageEmailEditText, messagePhoneEditText)){
                    Message newMessage;
                    String title = messageTitleEditText.getText().toString();
                    String description = messageDescriptionEditText.getText().toString();
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    CollectionReference messagesRef = db.collection("messages");
                    String messageId = messagesRef.document().getId();
                    if(messageEmailEditText.getText().toString().trim().length() == 0){
                        String phone = messagePhoneEditText.getText().toString();
                        newMessage = new Message(curUser, messageId, mAuth.getCurrentUser().getUid(), otherUserId, title, description, date, phone);
                    } else if(messagePhoneEditText.getText().toString().trim().length() == 0){
                        String email = messageEmailEditText.getText().toString();
                        newMessage = new Message(curUser, messageId, mAuth.getCurrentUser().getUid(), otherUserId, title, description, date, email);
                    } else {
                        String email = messageEmailEditText.getText().toString();
                        String phone = messagePhoneEditText.getText().toString();
                        newMessage = new Message(curUser, messageId, mAuth.getCurrentUser().getUid(), otherUserId, title, description, date, email, phone);
                    }
                    db.collection("messages").document(messageId).set(newMessage);
                    Toast.makeText(SendMessageActivity.this,"Message sent.", Toast.LENGTH_LONG).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                } else {
                    confirmMessageBtn.setClickable(true);
                    cancelMessageBtn.setClickable(true);
                    sendMessageProgressBar.setVisibility(View.GONE);
                }
                break;

            case R.id.cancelMessageBtn:
                confirmMessageBtn.setClickable(false);
                cancelMessageBtn.setClickable(false);
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        confirmMessageBtn.setClickable(true);
        cancelMessageBtn.setClickable(true);
        sendMessageProgressBar.setVisibility(View.GONE);
    }
}
