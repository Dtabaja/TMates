package com.example.tmates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton profileBtn, messagesBtn, lookForPartnerBtn, whereToPlayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        findById();
        setClickListeners();

    }

    // Method that start the profile page activity.
    public void startProfilePageActivity(){
        Intent intent = new Intent(this, ProfilePageActivity.class);
        startActivity(intent);
    }

    // Method that start the message list activity.
    public void startMessageListActivity(){
        Intent intent = new Intent(this, MessageListActivity.class);
        startActivity(intent);
    }

    // Method that start the where to play activity.
    public void startWhereToPlayActivity(){
        Intent intent = new Intent(HomePageActivity.this, WhereToPlayActivity.class);
        startActivity(intent);
    }

    // Method that start the look for partner activity.
    public void startLookForPartnerActivity(){
        Intent intent = new Intent(this, LookForPartnerActivity.class);
        startActivity(intent);
    }

    // Find views by id method.
    private void findById() {
        profileBtn = findViewById(R.id.profileBtn);
        messagesBtn = findViewById(R.id.messageListBtn);
        lookForPartnerBtn = findViewById(R.id.lookForPartnerBtn);
        whereToPlayBtn = findViewById(R.id.whereToPlayBtn);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profileBtn:
                startProfilePageActivity();
                break;

            case R.id.messageListBtn:
                startMessageListActivity();
                break;

            case R.id.lookForPartnerBtn:
                startLookForPartnerActivity();
                break;

            case R.id.whereToPlayBtn:
                startWhereToPlayActivity();
                break;
        }
    }

    // Method that set the click listeners.
    private void setClickListeners(){
        profileBtn.setOnClickListener(this);
        messagesBtn.setOnClickListener(this);
        lookForPartnerBtn.setOnClickListener(this);
        whereToPlayBtn.setOnClickListener(this);
    }
}
