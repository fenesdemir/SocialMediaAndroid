package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Viewholder_Question extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView time_result, name_result, question_result, deleteBtn, replybtn;
    ImageButton favorite_btn;
    DatabaseReference favoriteRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public Viewholder_Question(View itemView){
        super(itemView);
    }

    public void setitem(FragmentActivity activity, String name, String url, String userid, String key, String question, String privacy, String time){

        imageView = itemView.findViewById(R.id.iv_que_item);
        time_result = itemView.findViewById(R.id.tv_time_que);
        name_result = itemView.findViewById(R.id.tv_name_que);
        question_result = itemView.findViewById(R.id.tv_question_que);
        replybtn = itemView.findViewById(R.id.tv_reply_que);

        Picasso.get().load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);

    }

    public void favoriteChecker(String postkey) {

        favorite_btn = itemView.findViewById(R.id.ib_favorite);

        favoriteRef = database.getReference("favorites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();

        favoriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postkey).hasChild(currentid)){
                    favorite_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                }else{
                    favorite_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setitemRelated(Application activity, String name, String url, String userid, String key, String question, String privacy, String time){

        TextView timeTv = itemView.findViewById(R.id.tv_time_related);
        ImageView imageView = itemView.findViewById(R.id.iv_que_related);
        TextView nameTv = itemView.findViewById(R.id.tv_name_related);
        TextView queTv = itemView.findViewById(R.id.tv_question_related);
        TextView replyBtn = itemView.findViewById(R.id.tv_reply_related);

        Picasso.get().load(url).into(imageView);
        nameTv.setText(name);
        queTv.setText(question);
        timeTv.setText(time);

    }

    public void setitemDelete(Application activity, String name, String url, String userid, String key, String question, String privacy, String time){

        TextView timeTv = itemView.findViewById(R.id.tv_time_yq);
        ImageView imageView = itemView.findViewById(R.id.iv_que_yq);
        TextView nameTv = itemView.findViewById(R.id.tv_name_yq);
        TextView queTv = itemView.findViewById(R.id.tv_question_yq);
        deleteBtn = itemView.findViewById(R.id.tv_delete_yq);

        Picasso.get().load(url).into(imageView);
        nameTv.setText(name);
        queTv.setText(question);
        timeTv.setText(time);

    }

}
