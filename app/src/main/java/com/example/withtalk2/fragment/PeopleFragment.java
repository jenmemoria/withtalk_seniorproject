package com.example.withtalk2.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.withtalk2.R;
import com.example.withtalk2.chat.MessageActivity;
import com.example.withtalk2.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(),SelectFriendActivity.class));
            }
        });

        return view;

    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModels;

        public PeopleFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userModels.clear(); //누적 데이터 없애줌(중복x)
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        if(userModel.uid != null && userModel.uid.equals(myUid)){
                            continue;
                        }
                        userModels.add(userModel);
                    }
                    notifyDataSetChanged(); //데이터가 쌓이고 새로 고침 해야됨
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(holder.getAdapterPosition()).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder) holder).textView.setText(userModels.get(holder.getAdapterPosition()).userName);


            //친구 클릭 시 채팅방 넘어가기
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getView().getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",userModels.get(holder.getAdapterPosition()).uid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getView().getContext(),R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());
                }
            });

        }



        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public CustomViewHolder(View view){
                super(view);
                imageView = view.findViewById(R.id.profilephoto);
                textView=view.findViewById(R.id.profilename);
            }
        }
    }
}
