package com.example.tugas3_pmb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailid;
    TextInputEditText pass;
    Button signin;
    TextView tvdaftar, forgotText;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;
    CheckBox remember;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String PREF_NAME = "prefs";
    String KEY_REMEMBER = "remember";
    String KEY_USERNAME = "username";
    String KEY_PASS = "password";


    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
 
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailid = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        signin = findViewById(R.id.button);
        tvdaftar = findViewById(R.id.daftar);
        forgotText = findViewById(R.id.forgotPassword);
        progressBar = findViewById(R.id.progressBar2);
        remember = findViewById(R.id.remember);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(LoginActivity.this, "Anda telah masuk", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }else{
                    return;
                }
            }
        };


        if(sharedPreferences.getBoolean(KEY_REMEMBER, false)){
            remember.setChecked(true);
        }else{
            remember.setChecked(false);
        }

        emailid.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        pass.setText(sharedPreferences.getString(KEY_PASS, ""));


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailid.getText().toString();
                String password = pass.getText().toString();
                if(email.isEmpty()){
                    emailid.setHint("Tolong masukkan email");
                    emailid.requestFocus();
                }else if(password.isEmpty()){
                    pass.setHint("Tolong masukkan password");
                    pass.requestFocus();
                }else if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Isi yang kosong", Toast.LENGTH_SHORT).show();

                }else if(!(email.isEmpty() && password.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login error, silahkan login kembali", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }else{

                                if(remember.isChecked()) {
                                    editor.putString(KEY_USERNAME, emailid.getText().toString().trim());
                                    editor.putString(KEY_PASS, pass.getText().toString().trim());
                                    editor.putBoolean(KEY_REMEMBER, true);
                                    editor.apply();

                                }else{
                                    editor.putBoolean(KEY_REMEMBER, false);
                                    editor.remove(KEY_PASS);
                                    editor.remove(KEY_USERNAME);
                                    editor.apply();

                                }

                                Intent intoHome = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intoHome);
                            }

                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        tvdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intoDftr = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intoDftr);
            }
        });

        forgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Masukkan email untuk mendapatkan reset link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        mFirebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset link telah dikirim ke email Anda.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}