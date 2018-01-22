package ramji.travelers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment{

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        ButterKnife.bind(this,view);

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
