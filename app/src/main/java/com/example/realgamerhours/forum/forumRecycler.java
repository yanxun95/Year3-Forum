package com.example.realgamerhours.forum;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class forumRecycler extends RecyclerView.Adapter<forumRecycler.ViewHolder> {

    public List<forumPost> forumPostsList;
    public Context context;
    private ImageView forumUserImage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public forumRecycler(List<forumPost> forumPosts){
        this.forumPostsList = forumPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String forumID = forumPostsList.get(position).forumID;
        String username = forumPostsList.get(position).getUsername();
        holder.setUsername(username);

        String userID = forumPostsList.get(position).getUserID();
        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String userImage = task.getResult().getString("image");
                    holder.setForumUserImage(userImage);
                } else {
                    //Firebase Exception
                }

            }
        });

        //
        String descData = forumPostsList.get(position).getDesc();
        holder.setDescText(descData);

        long milliseconds = forumPostsList.get(position).getTimesStamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, commentsActivity.class);
                commentIntent.putExtra("forumID", forumID);
                context.startActivity(commentIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return forumPostsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView usernameView, forumDate, descView;
        private ImageButton btnComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnComment = view.findViewById(R.id.btnComment);
        }

        public void setUsername(String username){
            usernameView = view.findViewById(R.id.forumUsername);
            usernameView.setText(username);
        }

        public void setDescText(String descText){
            descView = view.findViewById(R.id.forumDesc);
            descView.setText(descText);
        }

        public void setTime(String date){
            forumDate = view.findViewById(R.id.forumPostDate);
            forumDate.setText(date);
        }

        public void setForumUserImage(String image){
            forumUserImage = view.findViewById(R.id.forumUserImage);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_round);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(forumUserImage);
        }

    }
}
