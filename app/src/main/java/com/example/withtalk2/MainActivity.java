package com.example.withtalk2;

import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import com.example.withtalk2.fragment.AccountFragment;
import com.example.withtalk2.fragment.ChatFragment;
import com.example.withtalk2.fragment.PeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavigationview);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background= mFirebaseRemoteConfig.getString("app_color");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_people:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment()).commit();
                        return true;
                    case R.id.action_account:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new AccountFragment()).commit();
                        return true;
                }

                return false;
            }
        });
        passPushTokenToServer();

    }
    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }
}
