package com.example.tmates;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<Post> mDataset;
    private StorageReference mStorageRef;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView postTitleTextView, postAuthorTextView, postDateTextView, postDescriptionTextView;
        public ImageView cardUserImage;
        public Button profileBtn, messageBtn;
        public String postId, userAuthorId;
        private Context context;

        public MyViewHolder(View v, String postId, String userAuthorId) {
            super(v);
            postTitleTextView = v.findViewById(R.id.postTitleTextView);
            postAuthorTextView = v.findViewById(R.id.postAuthorTextView);
            postDateTextView = v.findViewById(R.id.postDateTextView);
            postDescriptionTextView = v.findViewById(R.id.postDescriptionTextView);
            cardUserImage = v.findViewById(R.id.cardUserImage);
            profileBtn = v.findViewById(R.id.cardProfileBtn);
            messageBtn = v.findViewById(R.id.cardContactBtn);
            this.postId = postId;
            this.userAuthorId = userAuthorId;
            context = v.getContext();
            profileBtn.setOnClickListener(this);
            messageBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.cardProfileBtn:
                    startProfilePageActivity();
                    break;

                case R.id.cardContactBtn:
                    startSendMessagePageActivity();
                    break;
            }
        }

        public void startProfilePageActivity(){
            Intent intent = new Intent(context, ProfilePageActivity.class);
            intent.putExtra("otherUserId", this.userAuthorId);
            context.startActivity(intent);
        }

        public void startSendMessagePageActivity(){
            Intent intent = new Intent(context, SendMessageActivity.class);
            intent.putExtra("otherUserId", this.userAuthorId);
            context.startActivity(intent);
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Post> myDataset) {
        mDataset = myDataset;
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        MyViewHolder vh = new MyViewHolder(view, "", "");
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.postTitleTextView.setText(mDataset.get(position).getPostTitle());
        holder.postAuthorTextView.setText(mDataset.get(position).getAuthor().getName());
        holder.postDateTextView.setText(mDataset.get(position).getPostDate());
        holder.postDescriptionTextView.setText(mDataset.get(position).getPostDescription());
        holder.postId = mDataset.get(position).getPostId();
        holder.userAuthorId = mDataset.get(position).getAuthorId();
        try {
            getProfileImage(holder.userAuthorId, holder, holder.cardUserImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void getProfileImage(String id, final MyViewHolder holder, final ImageView imageView) throws IOException {
        String prefix = id;
        final File localFile = File.createTempFile(prefix, "");
        StorageReference userImage = mStorageRef.child("users_images/" + prefix);
        userImage.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        Glide.with(holder.context.getApplicationContext()).load(localFile).circleCrop().into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                Glide.with(holder.context.getApplicationContext()).load(R.drawable.profile_png).circleCrop().into(imageView);
            }
        });
    }
}


