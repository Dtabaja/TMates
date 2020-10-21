package com.example.tmates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.tmates.R.id.maleCheckBox;
import static com.example.tmates.R.id.userDescription;
import static com.example.tmates.R.id.welcomeStr;

public class CreateProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private String name, gender, city, description;
    private int age;
    private AppCompatCheckBox maleCheckBox, femaleCheckBox;
    private EditText nameEditText, ageEditText, cityEditText, descriptionEditText;
    private Button uploadImageBtn, continueBtn;
    private ArrayList<String> sportsArray = new ArrayList<>();
    private LinkedHashMap<String, String> sportsMap = new LinkedHashMap<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private Uri filePath;
    private ImageView userImage;
    private LinearLayout basketballLayout, soccerLayout, tennisLayout, footballLayout, volleyballLayout, runningLayout, baseballLayout;
    private ArrayList<LinearLayout> layoutsArray = new ArrayList<>();
    private Boolean editProfile = false;
    private TextView welcomeStr;
    private CheckBox basketballCheckBox, soccerCheckBox, tennisCheckBox, footballCheckBox, volleyballCheckBox, runningCheckBox, baseballCheckBox;
    private Spinner basketballSpinner, soccerSpinner, tennisSpinner, footballSpinner, volleyballSpinner, runningSpinner, baseballSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(getIntent().getExtras() != null){
            editProfile = true;
        }

        findById();
        initLayoutArray();
        initSpinners();
        setClickListeners();

        if(editProfile){
            welcomeStr.setText(R.string.editprofile_str);
            getUserInfo();
            continueBtn.setText(R.string.save_str);
        }

    }

    // Method that check the create profile form.
    public boolean checkFormValidation(){
            if(nameEditText.getText().toString().trim().length() == 0){
                nameEditText.setError("Please enter name.");
                return false;
            }
            if(ageEditText.getText().toString().trim().length() == 0){
                ageEditText.setError("Please enter age.");
                return false;
            }
            if(cityEditText.getText().toString().trim().length() == 0){
                cityEditText.setError("Please enter city");
                return false;
            }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.maleCheckBox:
                if(femaleCheckBox.isChecked()){
                    femaleCheckBox.setChecked(false);
                    maleCheckBox.setChecked(true);
                }
                break;
            case R.id.femaleCheckBox:
                if(maleCheckBox.isChecked()){
                    maleCheckBox.setChecked(false);
                    femaleCheckBox.setChecked(true);
                }
                break;

            case R.id.continueBtn:
                if(checkFormValidation()){
                    name = nameEditText.getText().toString();
                    age = Integer.parseInt(ageEditText.getText().toString());
                    if(maleCheckBox.isChecked()){
                        gender = "Male";
                    } else{
                        gender = "Female";
                    }
                    city = cityEditText.getText().toString();
                    description = descriptionEditText.getText().toString();
                    sportsArray.clear();
                    sportsMap.clear();
                    for(int i = 0 ; i < layoutsArray.size(); i++){
                        if(((CheckBox)layoutsArray.get(i).getChildAt(0)).isChecked()){
                            sportsMap.put(((CheckBox)layoutsArray.get(i).getChildAt(0)).getText().toString(), ((Spinner)layoutsArray.get(i).getChildAt(1)).getSelectedItem().toString());
                        }
                    }
                    User newUser = new User(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), name, gender, description, age, city, sportsMap);
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).set(newUser);
                    String userId = mAuth.getCurrentUser().getUid();
                    StorageReference userImage = mStorageRef.child("users_images/" + userId);
                    if(filePath != null){
                        userImage.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
                    }
                    if(editProfile){
                        //updatePostsDB(userId, newUser);
                        //updateMessagesDB(userId, newUser);
                        finish();
                    } else{
                        startHomePageActivity();
                    }
                }
                break;

            case R.id.uploadImageBtn:
                chooseImage();
                break;

            default:
                if(((CheckBox)view).isChecked()){
                    ((LinearLayout)view.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
                } else {
                    ((LinearLayout)view.getParent()).getChildAt(1).setVisibility(View.INVISIBLE);
                }
        }
    }

    private void updatePostsDB(final String authorId, final User user) {
        CollectionReference postsRef = db.collection("posts");
        postsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Post post = document.toObject(Post.class);
                        if(post.getAuthorId().compareTo(authorId) == 0){
                            post.setAuthor(user);
                            db.collection("posts").document(post.getPostId()).set(post);
                        }
                    }
                }
            }
        });
    }

    private void updateMessagesDB(final String authorId, final User user) {
        CollectionReference postsRef = db.collection("messages");
        postsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Message message = document.toObject(Message.class);
                        if(message.getSenderId().compareTo(authorId) == 0){
                            message.setAuthor(user);
                            db.collection("posts").document(message.getMessageId()).set(message);
                        }
                    }
                }
            }
        });
    }

    // Find views by id method.
    private void findById(){
        welcomeStr = findViewById(R.id.welcomeStr);
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        maleCheckBox = findViewById(R.id.maleCheckBox);
        femaleCheckBox = findViewById(R.id.femaleCheckBox);
        cityEditText = findViewById(R.id.cityEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        continueBtn = findViewById(R.id.continueBtn);
        userImage = findViewById(R.id.userImage);
        basketballLayout = findViewById(R.id.basketballLayout);
        soccerLayout = findViewById(R.id.soccerLayout);
        tennisLayout = findViewById(R.id.tennisLayout);
        footballLayout = findViewById(R.id.footballLayout);
        volleyballLayout = findViewById(R.id.volleyballLayout);
        runningLayout = findViewById(R.id.runningLayout);
        baseballLayout = findViewById(R.id.baseballLayout);
        basketballCheckBox = findViewById(R.id.basketballCheckBox);
        soccerCheckBox = findViewById(R.id.soccerCheckBox);
        tennisCheckBox = findViewById(R.id.tennisCheckBox);
        footballCheckBox = findViewById(R.id.footballCheckBox);
        volleyballCheckBox = findViewById(R.id.volleyballCheckBox);
        runningCheckBox = findViewById(R.id.runningCheckBox);
        baseballCheckBox = findViewById(R.id.baseballCheckBox);
        basketballSpinner = findViewById(R.id.basketballSpinner);
        soccerSpinner = findViewById(R.id.soccerSpinner);
        tennisSpinner = findViewById(R.id.tennisSpinner);
        footballSpinner = findViewById(R.id.footballSpinner);
        volleyballSpinner = findViewById(R.id.volleyballSpinner);
        runningSpinner = findViewById(R.id.runningSpinner);
        baseballSpinner = findViewById(R.id.baseballSpinner);
    }

    // Method that init layout array list.
    public void initLayoutArray(){
        layoutsArray.add(basketballLayout);
        layoutsArray.add(soccerLayout);
        layoutsArray.add(tennisLayout);
        layoutsArray.add(footballLayout);
        layoutsArray.add(volleyballLayout);
        layoutsArray.add(runningLayout);
        layoutsArray.add(baseballLayout);
    }

    // Method that init experience spinners.
    public void initSpinners(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exp_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(int i = 0; i < layoutsArray.size(); i++){
            ((Spinner)layoutsArray.get(i).getChildAt(1)).setAdapter(adapter);
        }
    }

    // Method that set the click listeners.
    private void setClickListeners(){
        maleCheckBox.setOnClickListener(this);
        femaleCheckBox.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        for(int i = 0; i < layoutsArray.size(); i++){
            layoutsArray.get(i).getChildAt(0).setOnClickListener(this);
        }
    }

    // Method that start the home page activity.
    public void startHomePageActivity(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    // Method that give the option to choose image.
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            filePath = data.getData();
            Glide.with(getApplicationContext()).load(filePath).into(userImage);
        }
    }

    // Method that get the user information from the firebase.
    private void getUserInfo() {
        DocumentReference documentReference = db.collection("users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        nameEditText.setText("" + user.getName());
                        ageEditText.setText("" + user.getAge());
                        if(user.getGender().compareTo("Male") == 0){
                            maleCheckBox.setChecked(true);
                            femaleCheckBox.setChecked(false);
                        } else {
                            femaleCheckBox.setChecked(true);
                            maleCheckBox.setChecked(false);
                        }
                        cityEditText.setText("" + user.getCity());
                        descriptionEditText.setText("" + user.getDescription());
                        try {
                            getProfileImage(mAuth.getCurrentUser().getUid());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(!user.getSportsMap().isEmpty()){
                            for(int i=0; i < user.getSportsMap().size(); i++){
                                sportsMap.put(new ArrayList<String>(user.getSportsMap().keySet()).get(i), new ArrayList<String>(user.getSportsMap().values()).get(i));
                            }
                            markCheckBox(sportsMap);
                        }
                    }
                }
            }
        });
    }

    // Method that mark the sports checkbox according to user information.
    private void markCheckBox(Map<String, String> sportsMap){
        ArrayList<String> expArr = new ArrayList<>();
        String exp;
        expArr.add("Beginner");
        expArr.add("Amateur");
        expArr.add("Professional");
        if(sportsMap.containsKey("Basketball")){
            basketballCheckBox.setChecked(true);
            basketballSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Basketball");
            basketballSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Soccer")){
            soccerCheckBox.setChecked(true);
            soccerSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Soccer");
            soccerSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Tennis")){
            tennisCheckBox.setChecked(true);
            tennisSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Tennis");
            tennisSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Football")){
            footballCheckBox.setChecked(true);
            footballSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Foottball");
            footballSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Volleyball")){
            volleyballCheckBox.setChecked(true);
            volleyballSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("volleyball");
            volleyballSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Running")){
            runningCheckBox.setChecked(true);
            runningSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Running");
            runningSpinner.setSelection(expArr.indexOf(exp));
        }
        if(sportsMap.containsKey("Baseball")){
            baseballCheckBox.setChecked(true);
            baseballSpinner.setVisibility(View.VISIBLE);
            exp = sportsMap.get("Baseball");
            baseballSpinner.setSelection(expArr.indexOf(exp));
        }
    }

    // Method that get the user image from the firebase.
    private void getProfileImage(String id) throws IOException {
        String prefix = id;
        final File localFile = File.createTempFile(prefix, "");
        StorageReference userImageRef = mStorageRef.child("users_images/" + prefix);
        userImageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        Glide.with(getApplicationContext()).load(localFile).into(userImage);
                        filePath = Uri.parse(localFile.getPath());
                       // Picasso.with(getApplicationContext()).load(localFile).into(userImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
            }
        });
    }
}
