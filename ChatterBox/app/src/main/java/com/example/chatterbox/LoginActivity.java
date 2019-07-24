package com.example.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
//    private FirebaseUser currentUser;
    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink, ForgotPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private DatabaseReference UsersRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

    }

    private void AllowUserToLogin(){
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password...",Toast.LENGTH_SHORT).show();
        }else{
            LoadingBar.setTitle("Sign In");
            LoadingBar.setMessage("Please Wait... ");
            LoadingBar.setCanceledOnTouchOutside(true);
            LoadingBar.show();



            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                UsersRef.child(currentUserId).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                SendUserToMainActivity();
                                                Toast.makeText(LoginActivity.this,"Logged In",Toast.LENGTH_SHORT).show();

                                                LoadingBar.dismiss();
                                            }
                                        });


                                SendUserToMainActivity();
                                LoadingBar.dismiss();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        LoginButton  = (Button) findViewById(R.id.login_button);
        PhoneLoginButton  = (Button) findViewById(R.id.phone_login_button);
        UserEmail  = (EditText) findViewById(R.id.login_email);
        UserPassword  = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink  = (TextView) findViewById(R.id.need_new_account_link);
        ForgotPasswordLink  = (TextView) findViewById(R.id.forget_password_link);
        LoadingBar = new ProgressDialog(this);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(currentUser!=null){
//            SendUserToMainActivity();
//        }
//    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
