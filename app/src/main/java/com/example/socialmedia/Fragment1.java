package com.example.socialmedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.zip.Inflater;

public class Fragment1 extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameIn, profIn, bioIn, emailIn, webIn;
    ImageButton ib_edit,ib_menu;


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView = getActivity().findViewById(R.id.iv_frg1);
        nameIn = getActivity().findViewById(R.id.tv_name_frg1);
        profIn = getActivity().findViewById(R.id.tv_profession_frg1);
        bioIn = getActivity().findViewById(R.id.tv_bio_frg1);
        emailIn = getActivity().findViewById(R.id.tv_email_frg1);
        webIn = getActivity().findViewById(R.id.tv_website_frg1);

        ib_edit = getActivity().findViewById(R.id.frg1_edit);
        ib_edit.setOnClickListener(this);

        ib_menu = getActivity().findViewById(R.id.frg1_menu);
        ib_menu.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.frg1_edit:
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.frg1_menu:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
                break;
            case R.id.iv_frg1:
                Intent intent1 = new Intent(getActivity(), ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_website_frg1:
                try {

                    String url = webIn.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);

                }catch (Exception e){

                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();

                }
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String emailResult = task.getResult().getString("email");
                    String profResult = task.getResult().getString("prof");
                    String webResult = task.getResult().getString("web");
                    String urlResult = task.getResult().getString("url");

                    Picasso.get().load(urlResult).into(imageView);
                    nameIn.setText(nameResult);
                    bioIn.setText(bioResult);
                    emailIn.setText(emailResult);
                    profIn.setText(profResult);
                    webIn.setText(webResult);

                }else{
                    Intent intent = new Intent(getActivity(), CreateProfile.class);
                    startActivity(intent);
                }

            }
        });


    }
}
