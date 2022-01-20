package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class UpdateProfile extends AppCompatActivity {

    EditText nameEt, bioEt, emailEt, profEt, webEt;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();
        documentReference = db.collection("user").document(currentUid);

        nameEt = findViewById(R.id.name_up);
        bioEt = findViewById(R.id.bio_up);
        emailEt = findViewById(R.id.email_up);
        profEt = findViewById(R.id.profession_up);
        webEt = findViewById(R.id.website_up);
        button = findViewById(R.id.btn_up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                updateProfile();

            }
        });

        
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String emailResult = task.getResult().getString("email");
                    String profResult = task.getResult().getString("prof");
                    String webResult = task.getResult().getString("web");

                    nameEt.setText(nameResult);
                    bioEt.setText(bioResult);
                    emailEt.setText(emailResult);
                    profEt.setText(profResult);
                    webEt.setText(webResult);

                }else{
                    Toast.makeText(UpdateProfile.this, "No Such Profile Exists!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void updateProfile() {

        String nameNew = nameEt.getText().toString();
        String emailNew = emailEt.getText().toString();
        String bioNew = bioEt.getText().toString();
        String webNew = webEt.getText().toString();
        String profNew = profEt.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentUid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                transaction.update(sDoc, "name", nameNew);
                transaction.update(sDoc, "prof", profNew);
                transaction.update(sDoc, "email", emailNew);
                transaction.update(sDoc, "bio", bioNew);
                transaction.update(sDoc, "web", webNew);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfile.this, "Update Succesfull!", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}