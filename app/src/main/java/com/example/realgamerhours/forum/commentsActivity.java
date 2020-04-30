package com.example.realgamerhours.forum;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realgamerhours.R;
import com.example.realgamerhours.user.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class commentsActivity extends AppCompatActivity {

    private EditText commentEditText;
    private ImageButton btnSendComment;
    private RecyclerView recyclerViewCommentList;
    private String forumId, userID, username;
    private commentsRecycler commentsRecycler;
    private List<comments> commentsList;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupUI();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                username = userProfile.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(commentsActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });


        //RecycleView firebase list
        commentsList = new ArrayList<>();
        commentsRecycler = new commentsRecycler(commentsList);
        recyclerViewCommentList.setHasFixedSize(true);
        recyclerViewCommentList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCommentList.setAdapter(commentsRecycler);

        firebaseFirestore.collection("Post/" + forumId +"/Comments").addSnapshotListener(commentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        String commentID = documentChange.getDocument().getId();
                        comments comments = documentChange.getDocument().toObject(com.example.realgamerhours.forum.comments.class);
                        commentsList.add(comments);
                        commentsRecycler.notifyDataSetChanged();
                    }
                }
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentText = commentEditText.getText().toString();

                if(!commentText.isEmpty()){

                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("comment", commentText);
                    commentMap.put("userID", userID);
                    commentMap.put("username", username);
                    commentMap.put("timesStamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Post/" + forumId +"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(commentsActivity.this, "Error Posting Comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                //make it empty every time after user send the comment
                                commentEditText.setText("");
                            }
                        }
                    });
                }
            }
        });
    }

    public void setupUI(){
        commentEditText = findViewById(R.id.commentEditText);
        btnSendComment = findViewById(R.id.btnSendComment);
        recyclerViewCommentList = findViewById(R.id.commentRecycleList);
        forumId = getIntent().getStringExtra("forumID");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
    }
}
