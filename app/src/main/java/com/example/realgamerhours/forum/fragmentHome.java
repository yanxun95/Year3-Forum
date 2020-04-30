package com.example.realgamerhours.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realgamerhours.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentHome extends Fragment {

    private RecyclerView forumListView;
    private List<forumPost> forumList;

    private FirebaseFirestore firebaseFirestore;
    private forumRecycler forumRecycler;

    private DocumentSnapshot lastVisible;

    public fragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        forumList = new ArrayList<>();
        forumListView = view.findViewById(R.id.forumList);

        forumRecycler = new forumRecycler(forumList);
        forumListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        forumListView.setAdapter(forumRecycler);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //check the recycler view is reach the bottom
        forumListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean bottomForum = !recyclerView.canScrollVertically(1);

                if(bottomForum){
                    Toast.makeText(container.getContext(),"This is the Bottom.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //load the post
        Query query = firebaseFirestore.collection("Post").orderBy("timesStamp",Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        String forumID = documentChange.getDocument().getId();
                        forumPost forumPost = documentChange.getDocument().toObject(com.example.realgamerhours.forum.forumPost.class).withID(forumID);
                        forumList.add(forumPost);
                        forumRecycler.notifyDataSetChanged();
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
