package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allQuestions, userQuestions;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    QuestionMember questionMember;
    String name, url, privacy, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();


        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit);

        documentReference = db.collection("user").document(currentUserid);

        allQuestions = database.getReference("All Questions");
        userQuestions = database.getReference("User Questions").child(currentUserid);

        questionMember = new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = editText.getText().toString();

                Calendar cdate = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String savedate = currentdate.format(cdate.getTime());

                Calendar ctime = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate + ":" + savetime;

                if(question != null){

                    questionMember.setQuestion(question);
                    questionMember.setName(name);
                    questionMember.setPrivacy(privacy);
                    questionMember.setUrl(url);
                    questionMember.setUserid(uid);
                    questionMember.setTime(time);

                    String id = userQuestions.push().getKey();
                    userQuestions.child(id).setValue(questionMember);

                    String child = allQuestions.push().getKey();
                    questionMember.setKey(id);
                    allQuestions.child(child).setValue(questionMember);

                    Toast.makeText(AskActivity.this, "Submitted!", Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(AskActivity.this, "Please ask something!", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    name = task.getResult().getString("name");
                    privacy = task.getResult().getString("privacy");
                    url = task.getResult().getString("url");
                    uid = task.getResult().getString("uid");

                }else{
                    Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}