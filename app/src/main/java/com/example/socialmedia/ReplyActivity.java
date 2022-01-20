package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ReplyActivity extends AppCompatActivity {

    String uid, question, post_key, key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference, reference2;

    TextView nametv, questiontv, replytv;
    RecyclerView recyclerView;
    ImageView iv_main, iv_user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference votesRef, allQuestions;

    boolean voteChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        nametv = findViewById(R.id.name_reply_tv);
        questiontv = findViewById(R.id.que_reply_tv);
        iv_main = findViewById(R.id.iv_reply_main);
        iv_user = findViewById(R.id.iv_reply_user);
        replytv = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));



        Bundle extra = getIntent().getExtras();
        if(extra != null){
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("question");
            //key = extra.getString("key");
        }else {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        allQuestions = database.getReference("All Questions").child(post_key).child("Answer");
        votesRef = database.getReference("votes");

        reference = db.collection("user").document(uid);
        reference2 = db.collection("user").document(currentUid);

        replytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReplyActivity.this, AnswerActivity.class);
                intent.putExtra("uid", uid);
                //intent.putExtra("question", question);
                intent.putExtra("postkey", post_key);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){
                    String url = task.getResult().getString("url");
                    String name = task.getResult().getString("name");
                    Picasso.get().load(url).into(iv_main);
                    nametv.setText(name);
                    questiontv.setText(question);
                }else{
                    Toast.makeText(ReplyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        reference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){
                    String url = task.getResult().getString("url");
                    Picasso.get().load(url).into(iv_user);

                }else{
                    Toast.makeText(ReplyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        FirebaseRecyclerOptions<AnswerMember> options = new FirebaseRecyclerOptions.Builder<AnswerMember>().setQuery(allQuestions, AnswerMember.class).build();

        FirebaseRecyclerAdapter<AnswerMember, AnswerViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AnswerMember, AnswerViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AnswerViewholder holder, int position, @NonNull AnswerMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();

                final String postKey = getRef(position).getKey();

                holder.setAnswer(getApplication(), model.getName(), model.getAnswer(), model.getUid(), model.getTime(), model.getUrl());

                holder.upvoteChecker(postKey);
                holder.uptoveTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        voteChecker = true;
                        votesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(voteChecker == true){
                                    if (snapshot.child(postKey).hasChild(currentUid)){
                                        votesRef.child(postKey).child(currentUid).removeValue();
                                        voteChecker =false;
                                    }else{
                                        votesRef.child(postKey).child(currentUid).setValue(true);
                                        voteChecker =false;
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
            public AnswerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_layout, parent, false);

                return new AnswerViewholder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}