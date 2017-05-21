package graduation.mo7adraty.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import graduation.mo7adraty.R;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _classText;
    EditText _sectionText;
    Button _signupButton;
    TextView _loginLink;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupRef();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void setupRef() {
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _classText = (EditText) findViewById(R.id.input_class);
        _sectionText = (EditText) findViewById(R.id.input_class);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String classNum = _classText.getText().toString();
        final String sectionNum = _sectionText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        Log.e(TAG, "createUserWithEmail:onComplete:" + task.getException());

                        //
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            onSignupFailed();
                        }else{
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("name",name);
                            childUpdates.put("email",email);
                            if(sectionNum.equals("1")) {
                                childUpdates.put("class", classNum+"-A");
                            }else  if(sectionNum == "2" ) {
                                childUpdates.put("class", classNum+"-B");
                            }else  if(sectionNum == "3" ) {
                                childUpdates.put("class", classNum+"-C");
                            }else  if(sectionNum == "4" ) {
                                childUpdates.put("class", classNum+"-D");
                            }else  if(sectionNum == "5" ) {
                                childUpdates.put("class", classNum+"-E");
                            }
                            childUpdates.put("role", "student");
                            myRef.updateChildren(childUpdates);
                            onSignupSuccess();
                        }
                    }
                });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
        startActivity(new Intent(this,Home.class));
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String classNum = _classText.getText().toString();
        String sectionNum = _sectionText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }
        if(classNum.isEmpty() || classNum.length() < 1|| Integer.valueOf(classNum)>4 || Integer.valueOf(classNum)==0) {
            _classText.setError("enter a valid class number from 1 to 4");
            valid = false;
        } else {
            _classText.setError(null);
        }
        if(sectionNum.isEmpty() || sectionNum.length() < 1|| Integer.valueOf(sectionNum)>5 || Integer.valueOf(sectionNum)==0) {
            _sectionText.setError("enter a valid section number from 1 to 5");
            valid = false;
        } else {
            _sectionText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

