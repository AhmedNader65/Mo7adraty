package graduation.mo7adraty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import graduation.mo7adraty.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,String> value = (HashMap<String,String> )dataSnapshot.getValue();

                            if(value.get("role").equals("dr")){
                                Intent intent = new Intent(MainActivity.this,HomeDr.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }else {
                                startActivity(new Intent(MainActivity.this,Home.class));

                                MainActivity.this.finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    startActivity(new Intent(MainActivity.this , LoginActivity.class));
                }
            }

        }, 1 * 1000);

    }
}
