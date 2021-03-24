package com.example.tugas3_pmb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText emailid;
    TextInputEditText pass;
    Button daftar;
    TextView masuk;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailid = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        daftar = findViewById(R.id.button);
        masuk = findViewById(R.id.login);
        progressBar =findViewById(R.id.progressBar);

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailid.getText().toString();
                String pwd =  pass.getText().toString();

                if(email.isEmpty()){
                    emailid.setHint("Tolong masukkan email");
                    emailid.requestFocus();
                }else if(pwd.isEmpty()){
                    pass.setHint("Tolong masukkan password");
                    pass.requestFocus();
                }else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(MainActivity.this, "Isi yang kosong", Toast.LENGTH_SHORT).show();

                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Pendaftaran gagal, coba lagi", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }else{
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(a);
            }
        });

    }
}