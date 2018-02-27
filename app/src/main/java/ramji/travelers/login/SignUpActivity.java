package ramji.travelers.login;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.FirebaseMethods;
import ramji.travelers.HomeActivity;
import ramji.travelers.R;

public class SignUpActivity extends AppCompatActivity{

    private static final String TAG = "SignUpActivity";

    @BindView(R.id.input_userName)
    TextInputEditText inputUserName;
    @BindView(R.id.input_email)
    TextInputEditText inputEmail;
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;
    @BindView(R.id.signUp)
    Button signUpButton;
    @BindView(R.id.layoutView)
    LinearLayout layoutView;


    private String username;
    private String email;
    private String password;

    private FirebaseMethods firebaseMethods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_account);
        ButterKnife.bind(this);

        if (getResources().getBoolean(R.bool.is_phone))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboards();
            }
        });

        firebaseMethods = new FirebaseMethods(this);
        setupFirebaseAuth();
        signUp();

    }

    private void signUp() {

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = inputUserName.getText().toString();
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                if (checkForInputs(username,email,password)){

                    firebaseMethods.registerNewEmail(username,email,password);

                    Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                    intent.putExtra("fromSignUpActivity",true);
                    startActivity(intent);
                }
            }
        });

    }
    private void hideSoftKeyboards(){
        if (getCurrentFocus() != null){
            InputMethodManager imm =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private boolean checkForInputs(String name, String email, String password) {
        Log.d(TAG,"checking inputs for empty values");
        if (name.equals("") || email.equals("") || password.equals("")){
            Toast.makeText(this, R.string.empty_fields_signup_activity, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupFirebaseAuth() {
        Log.d(TAG,"Setting up firebase authentication");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();

    }

}
