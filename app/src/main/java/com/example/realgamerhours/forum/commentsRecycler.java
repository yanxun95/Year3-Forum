package com.example.realgamerhours.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.realgamerhours.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class commentsRecycler extends RecyclerView.Adapter<commentsRecycler.ViewHolder> {

    public List<comments> commentsList;
    public Context context;
    private ImageView commentUserImage;
    private FirebaseFirestore firebaseFirestore;

    public commentsRecycler(List<comments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public commentsRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_list, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new commentsRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final commentsRecycler.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String username = commentsList.get(position).getUsername();
        holder.setCommentUsername(username);

        String commentMessage = commentsList.get(position).getComment();
        holder.setCommentMessage(commentMessage);

        String userID = commentsList.get(position).getUserID();
        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String userImage = task.getResult().getString("image");
                    holder.setCommentUserImage(userImage);
                } else {
                    //Firebase Exception
                }

            }
        });
    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {
            return commentsList.size();
        } else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;

        private TextView commentUsername, commentText;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setCommentUsername(String username){
            commentUsername = view.findViewById(R.id.commentUsername);
            commentUsername.setText(username);
        }

        public void setCommentMessage(String message){
            commentText = view.findViewById(R.id.commentText);
            commentText.setText(message);
        }

        public void setCommentUserImage(String image){
            commentUserImage = view.findViewById(R.id.commentUserImage);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_round);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(commentUserImage);
        }
    }

}