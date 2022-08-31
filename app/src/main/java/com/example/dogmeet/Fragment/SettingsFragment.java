package com.example.dogmeet.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogmeet.R;
import com.example.dogmeet.entity.User;
import com.example.dogmeet.mainActivity.AddActivity;
import com.example.dogmeet.mainActivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsFragment extends Fragment {
    private View view;
    private EditText nameEdit, emailEdit,
            passwordOldEdit, passwordNewEdit;
    private ImageButton bioSave, emailSave, passwordSave;
    private DatabaseReference users;
    private String password, email, name, age;
    private TextView exit;
    FirebaseUser user;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_settings, container, false);

        nameEdit=view.findViewById(R.id.editNameSettings);
        emailEdit=view.findViewById(R.id.editEmailSettings);
        passwordOldEdit=view.findViewById(R.id.editOldPassword);
        passwordNewEdit=view.findViewById(R.id.editNewPassword);

        exit=view.findViewById(R.id.exit);

        passwordSave=view.findViewById(R.id.passwordSave);
        emailSave=view.findViewById(R.id.emailSave);
        bioSave=view.findViewById(R.id.bioSaveBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();

        users = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        email=user.getEmail();

        emailEdit.setText(user.getEmail());

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user!=null;
                name=user.getName();
                nameEdit.setText(name);
                age=user.getAge();
                bioSave.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailSave.setVisibility(View.VISIBLE);
                if (emailEdit.getText().toString().equals(email)){
                    emailSave.setVisibility(View.GONE);
                }
            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                bioSave.setVisibility(View.VISIBLE);
                if (nameEdit.getText().toString().equals(name)){
                    bioSave.setVisibility(View.GONE);
                }
            }
        });


        passwordOldEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordNewEdit.setEnabled(true);
            }
        });

        passwordNewEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordSave.setVisibility(View.VISIBLE);
            }
        });

        bioSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.child("name").setValue(nameEdit.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте позже", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), "Данные изменены", Toast.LENGTH_LONG).show();
                            bioSave.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        emailSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.updateEmail(emailEdit.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте позже", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(), "Email успешно изменен", Toast.LENGTH_LONG).show();
                            emailSave.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        passwordSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return view;
    }

    public void updatePassword(){
        password=passwordOldEdit.getText().toString();
        String newPassword=passwordNewEdit.getText().toString();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getContext(), "Что-то пошло не так. Попробуйте чуть позже", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getContext(), "Пароль успешно обновлен", Toast.LENGTH_LONG).show();

                                passwordOldEdit.setText(null);
                                passwordNewEdit.setText(null);
                                passwordNewEdit.setEnabled(false);
                                passwordSave.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Неправильный пароль", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}