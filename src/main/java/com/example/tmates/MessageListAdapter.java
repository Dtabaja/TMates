package com.example.tmates;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {

    private ArrayList<Message> mDataset;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView messageTitleTextView, messageAuthorTextView, messageDateTextView,
                messageDescriptionTextView, messageEmailTextView, messagePhoneTextView;
        public ImageView messageCardUserImage;
        public Button profileBtn, deleteBtn;
        public String messageId, userAuthorId;
        private Context context;
        public MyViewHolder(@NonNull View itemView, String messageId, String userAuthorId) {
            super(itemView);
            messageTitleTextView = itemView.findViewById(R.id.messageTitleTextView);
            messageAuthorTextView = itemView.findViewById(R.id.messageAuthorTextView);
            messageDateTextView = itemView.findViewById(R.id.messageDateTextView);
            messageDescriptionTextView = itemView.findViewById(R.id.messageDescriptionTextView);
            messageEmailTextView = itemView.findViewById(R.id.messageCardEmailTextView);
            messagePhoneTextView = itemView.findViewById(R.id.messageCardPhoneTextView);
            messageCardUserImage = itemView.findViewById(R.id.messageCardUserImage);
            profileBtn = itemView.findViewById(R.id.messageCardProfileBtn);
            deleteBtn = itemView.findViewById(R.id.messageCardDeleteBtn);
            this.messageId = messageId;
            this.userAuthorId = userAuthorId;
            context = itemView.getContext();
            profileBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.messageCardProfileBtn:
                    startProfilePageActivity();
                    break;

                case R.id.messageCardDeleteBtn:

                    break;
            }
        }

        public void startProfilePageActivity(){
            Intent intent = new Intent(context, ProfilePageActivity.class);
            intent.putExtra("otherUserId", this.userAuthorId);
            context.startActivity(intent);
        }
    }


    public MessageListAdapter(ArrayList<Message> mDataset){
        this.mDataset = mDataset;
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }


    @NonNull
    @Override
    public MessageListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelist_row, parent, false);
        MessageListAdapter.MyViewHolder vh = new MessageListAdapter.MyViewHolder(view, "", "");
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageListAdapter.MyViewHolder holder, final int position) {
        holder.messageTitleTextView.setText(mDataset.get(position).getMessageTitle());
        holder.messageAuthorTextView.setText(mDataset.get(position).getAuthor().getName());
        holder.messageDateTextView.setText(mDataset.get(position).getMessageDate());
        holder.messageDescriptionTextView.setText(mDataset.get(position).getMessageText());
        if(TextUtils.isEmpty(mDataset.get(position).getEmail())){
            holder.messageEmailTextView.setVisibility(View.GONE);
        }else {
            holder.messageEmailTextView.setText(mDataset.get(position).getEmail());
        }
        if(TextUtils.isEmpty(mDataset.get(position).getPhone())){
            holder.messagePhoneTextView.setVisibility(View.GONE);
        } else{
            holder.messagePhoneTextView.setText(mDataset.get(position).getPhone());
        }
        holder.userAuthorId = mDataset.get(position).getSenderId();
        holder.messageId = mDataset.get(position).getMessageId();
        try {
            getProfileImage(holder.userAuthorId, holder, holder.messageCardUserImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataset.remove(position);
                notifyItemRemoved(position);
                removeMessageFromFireBase(holder.messageId, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void getProfileImage(String id, final MessageListAdapter.MyViewHolder holder, final ImageView imageView) throws IOException {
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

    private void removeMessageFromFireBase(String messageId, final MessageListAdapter.MyViewHolder holder){
        db.collection("messages").document(messageId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(holder.context.getApplicationContext(),"Delete successfully.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
