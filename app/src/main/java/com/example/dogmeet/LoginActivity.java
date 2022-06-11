package com.example.dogmeet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btnSingIn, btnLinkToRegisterScreen;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users_db;
    EditText inputEmail;
    EditText inputPassword;

    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSingIn=findViewById(R.id.btnLogin);
        btnLinkToRegisterScreen=findViewById(R.id.btnLinkToRegisterScreen);

        auth = FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users_db=db.getReference("Users");

        root=findViewById(R.id.root_element);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        btnLinkToRegisterScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }

        });


        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if ( !email.isEmpty() && !password.isEmpty()) {
                   auth.signInWithEmailAndPassword(email, password)
                           .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                               @Override
                               public void onSuccess(AuthResult authResult) {
                                   startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                   finish();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(getApplicationContext(),
                                   "Ошибка авторизации", Toast.LENGTH_LONG)
                                   .show();
                       }
                   });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Введите данные!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Регистрация");
        dialog.setMessage("Введите данные для регистрации");

        LayoutInflater inflator= LayoutInflater.from(this);
        View register_window= inflator.inflate(R.layout.regisrter_window, null);
        dialog.setView(register_window);

        final EditText name=register_window.findViewById(R.id.name);
        final EditText email=register_window.findViewById(R.id.email);
        final EditText password=register_window.findViewById(R.id.password);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Зарегистрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
               if(TextUtils.isEmpty(name.getText().toString())) {
                   Snackbar.make(root, "Введите вашу имя", Snackbar.LENGTH_LONG).show();
                   return;
               }

                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if((password.getText().toString().length()<5)) {
                    Snackbar.make(root, "Введите пароль, который больше 5 символов", Snackbar.LENGTH_LONG).show();
                    return;
                }


                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Регистрация не удалась!", Toast.LENGTH_LONG)
                                            .show();
                                    Log.v("error", task.getException().getMessage());
                                } else {
                                User user = new User();
                                user.setUid(auth.getUid());
                                user.setName(name.getText().toString());
                                user.setEmail(email.getText().toString());

                                users_db.push().setValue(user);

                                Toast.makeText(getApplicationContext(), name.getText().toString()+", добро пожаловать!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            }

                        });

            }
        });

        dialog.show();


    }
}