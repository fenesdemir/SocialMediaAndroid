package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnswerViewholder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameTv, timeTv, ansTv, uptoveTv, votesTv;
    int voteCount;
    DatabaseReference databaseReference;
    FirebaseDatabase database;

    public AnswerViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAnswer(Application application, String name, String answer, String uid, String time, String url){

        imageView = itemView.findViewById(R.id.iv_ans_item);
        nameTv = itemView.findViewById(R.id.tv_name_ans);
        timeTv = itemView.findViewById(R.id.tv_time_ans);
        ansTv = itemView.findViewById(R.id.tv_ans);

        nameTv.setText(name);
        timeTv.setText(time);
        ansTv.setText(answer);
        Picasso.get().load(url).into(imageView);

    }

    public void upvoteChecker(String postkey){

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("votes");

        uptoveTv = itemView.findViewById(R.id.tv_vote_up);
        votesTv = itemView.findViewById(R.id.tv_vote_no);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postkey).hasChild(currentuid)){
                    uptoveTv.setText("VOTES");
                    voteCount = (int)snapshot.child(postkey).getChildrenCount();
                    votesTv.setText(Integer.toString(voteCount)+"-VOTES");
                }else{
                    uptoveTv.setText("UPVOTE");
                    voteCount = (int)snapshot.child(postkey).getChildrenCount();
                    votesTv.setText(Integer.toString(voteCount)+"-VOTES");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
