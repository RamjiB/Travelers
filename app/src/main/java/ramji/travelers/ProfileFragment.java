package ramji.travelers;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "ProfileFragment";

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        ButterKnife.bind(this,view);

        userDetailsFragment = new UserDetailsFragment();
        userImagesFragment = new UserImagesFragment();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.INVISIBLE);

                fm = getFragmentManager();

                fm.beginTransaction()
                        .add(R.id.userDetailsFragmentPart,userDetailsFragment)
                        .commit();

                fm.beginTransaction()
                        .add(R.id.userImagesFragmentPart,userImagesFragment)
                        .commit();

                userProfileLayout.setVisibility(View.VISIBLE);
            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText email = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                email.setLayoutParams(lp);
                email.setPadding(5,5,5,5);
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

        return view;
    }
}
