package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class Fragment2 extends Fragment implements View.OnClickListener{

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, favorite_ref, favorite_listRef;
    RecyclerView recyclerView;
    Boolean favoriteChecker;
    ImageView imageView;

    QuestionMember member;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rv_frg2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = database.getReference("All Questions");
        member = new QuestionMember();
        favorite_ref = database.getReference("favorites");
        favorite_listRef = database.getReference("favorites").child(currentUid);

        imageView = getActivity().findViewById(R.id.iv_frg2);
        fb = getActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentUid);

        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options = new FirebaseRecyclerOptions.Builder<QuestionMember>().setQuery(databaseReference, QuestionMember.class).build();

        FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();

                final String postkey = getRef(position).getKey();

                holder.setitem(getActivity(), model.getName(), model.getUrl(), model.getUserid(), model.getKey(), model.getQuestion(), model.getPrivacy(), model.getTime() );

                String question = getItem(position).getQuestion();
                String name = getItem(position).getName();
                String url = getItem(position).getUrl();
                final String time = getItem(position).getTime();
                String privacy = getItem(position).getPrivacy();
                String userid = getItem(position).getUserid();

                holder.replybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),ReplyActivity.class);
                        intent.putExtra("uid", currentUid);
                        intent.putExtra("question",question);
                        intent.putExtra("postkey", postkey);
                        //intent.putExtra("key", privacy);
                        startActivity(intent);
                    }
                });

                holder.favoriteChecker(postkey);
                holder.favorite_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        favoriteChecker = true;
                        favorite_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(favoriteChecker.equals(true)){
                                    if(snapshot.child(postkey).hasChild(currentUid)){
                                        favorite_ref.child(postkey).child(currentUid).removeValue();
                                        delete(time);
                                        favoriteChecker = false;
                                    }else{
                                        favorite_ref.child(postkey).child(currentUid).setValue(true);
                                        member.setName(name);
                                        member.setTime(time);
                                        member.setPrivacy(privacy);
                                        member.setUserid(userid);
                                        member.setUrl(url);
                                        member.setQuestion(question);

                                        //String id = favorite_listRef.push().getKey();
                                        favorite_listRef.child(postkey).setValue(member);
                                        favoriteChecker = false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });


            }

            @NonNull
            @Override
            public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);

                return new Viewholder_Question(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    void delete(String time){

        Query query = favorite_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    snapshot1.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_frg2:

                BottomSheetFrg2 bottomSheetFrg2 = new BottomSheetFrg2();
                bottomSheetFrg2.show(getFragmentManager(), "bottom");

                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AskActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){
                    String url = task.getResult().getString("url");
                    Picasso.get().load(url).into(imageView);
                }else{
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
