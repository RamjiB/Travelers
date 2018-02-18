package ramji.travelers.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.R;
import ramji.travelers.user_details.UserDetailsFragment;
import ramji.travelers.user_details.UserImagesFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ProfileLoginFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "ProfileLoginFragment";

    @BindView(R.id.input_email)
    TextInputEditText email;

    @BindView(R.id.input_password)
    TextInputEditText password;

    @BindView(R.id.login)
    Button login;

    @BindView(R.id.forget_password)
    TextView forgetPassword;

    @BindView(R.id.new_account)
    TextView newAccount;

    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.userDetailsFragmentPart)
    FrameLayout userDetailsFragmentPart;

    @BindView(R.id.userImagesFragmentPart)
    FrameLayout userImagesFragmentPart;

    @BindView(R.id.userProfileLayout)
    LinearLayout userProfileLayout;

    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;

    private UserDetailsFragment userDetailsFragment;
    private UserImagesFragment userImagesFragment;
    private android.support.v4.app.FragmentManager fm;



    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user,container,false);
        ButterKnife.bind(this,view);

        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboards();
            }
        });


        setupFirebaseAuth();

        if (mAuth.getCurrentUser() != null){
            moveIntoUserProfile();
        }else {

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mProgressBar.setVisibility(View.VISIBLE);
                    hideSoftKeyboards();
                    checkforLoginDetails();

                }
            });

            newAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SignUpActivity.class);
                    startActivity(intent);
                }
            });

            forgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final EditText email = new EditText(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(5, 5, 5, 5);
                    email.setLayoutParams(lp);
                    email.setHint(getString(R.string.email));
                    email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Reset Password")
                            .setMessage("Do you want to reset your password?")
                            .setView(email)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!Objects.equals(email.getText().toString(), "")) {
                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        auth.sendPasswordResetEmail(email.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                            Toast.makeText(getContext(),
                                                                    "Reset Link sent to your email",
                                                                    Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();


                }
            });

        }

        return view;
    }

    private void checkforLoginDetails() {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (Email.equals("") || Password.equals("")) {
            Toast.makeText(getContext(), getString(R.string.empty_fields_signup_activity),
                    Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(Email,Password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInwithEmailAndPassword: onComplete: " + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user == null) {
                                Toast.makeText(getContext(), "user not registered", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }else {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "signInEmail:failed ", task.getException());
                                } else {
                                    try {
                                        assert user != null;
                                        if (user.isEmailVerified()) {
                                            Log.d(TAG, "onComplete: success. email is verified");
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            moveIntoUserProfile();

                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Email is not verified \n check your inbox",
                                                    Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            mAuth.signOut();
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }

                            }
                        }
                    });
        }
    }


    private void moveIntoUserProfile() {

        userDetailsFragment = new UserDetailsFragment();
        userImagesFragment = new UserImagesFragment();
        fm = getFragmentManager();

        loginLayout.setVisibility(View.INVISIBLE);

        fm.beginTransaction()
                .add(R.id.userDetailsFragmentPart,userDetailsFragment)
                .commit();

        fm.beginTransaction()
                .add(R.id.userImagesFragmentPart,userImagesFragment)
                .commit();

        userProfileLayout.setVisibility(View.VISIBLE);
    }

    private void setupFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                    Log.d(TAG,"onAuthStateChanged: signed_in "+ user.getUid());
                else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    userProfileLayout.setVisibility(View.INVISIBLE);
                    loginLayout.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void hideSoftKeyboards(){
        if (getActivity().getCurrentFocus() != null){
            InputMethodManager imm =(InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
    }
}
