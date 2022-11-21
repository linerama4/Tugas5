package com.must.nano3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvEmail, tvId;
    private EditText etTitle,etDesc;
    private FirebaseAuth mAuth;
    private Button logout, submit;
    Note note;
    private FirebaseDatabase fData;
    private DatabaseReference rData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_note);

        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvId = (TextView) findViewById(R.id.tv_uid);
        logout = (Button)findViewById(R.id.btn_keluar);
        submit = (Button)findViewById(R.id.btn_submit);
        etTitle = (EditText)findViewById(R.id.et_title);
        etDesc = (EditText)findViewById(R.id.et_description);

        mAuth = FirebaseAuth.getInstance();
        fData = FirebaseDatabase.getInstance();
        rData = fData.getReference();
        note = new Note();

        logout.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_keluar:logOut();
                break;
            case R.id.btn_submit:submitData();
                break;
        }
    }

    public void logOut(){
        mAuth.signOut();
        Intent intent = new Intent(InsertNoteActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            tvEmail.setText(currentUser.getEmail());
            tvId.setText(currentUser.getUid());
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            etTitle.setError("Required");
            result = false;
        } else {
            etTitle.setError(null);
        }
        if (TextUtils.isEmpty(etDesc.getText().toString())) {
            etDesc.setError("Required");
            result = false;
        } else {
            etDesc.setError(null);
        }
        return result;
    }

    public void submitData(){
        if (!validateForm()){
            return;
        }
        String title = etTitle.getText().toString();
        String desc = etDesc.getText().toString();
        Note note1 = new Note(title, desc);
        rData.child("notes").child(mAuth.getUid()).push().setValue(note1).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(InsertNoteActivity.this, "Add data", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InsertNoteActivity.this, "Failed to Add data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
