package android.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail,edtusername,edtpassword;
    private Button btnsignup,btnsignin;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail=findViewById(R.id.editEmail);
        edtusername=findViewById(R.id.edtusername);
        edtpassword=findViewById(R.id.edtpassword);
        btnsignin=findViewById(R.id.btnsignin);
        btnsignup=findViewById(R.id.btnsignup);

        mAuth = FirebaseAuth.getInstance();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             signUp();
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
         transitiontosocialmediaactivity();
        }
    }
    private void signUp(){
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtpassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Signing Up Successfull",Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("username").setValue(edtusername.getText().toString());

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtusername.getText().toString())
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       Toast.makeText(MainActivity.this,"Display Name Updated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    transitiontosocialmediaactivity();
                }
                else {
                    Toast.makeText(MainActivity.this,"Signing Up Failed",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Signing In Successfull",Toast.LENGTH_LONG).show();
                    transitiontosocialmediaactivity();
                }
                else{
                    Toast.makeText(MainActivity.this,"Signing In Failed",Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void transitiontosocialmediaactivity() {
        Intent intent=new Intent(this,SocialMediaActivity.class);
        startActivity(intent);
    }
}