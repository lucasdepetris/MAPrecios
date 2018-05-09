package com.preciosclaros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.SignInButton;


public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener {

    static SignInActivity activityA;
    static boolean close;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityA = this;
        setContentView(R.layout.activity_sign_in);
        // Views

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]
    }
    public static SignInActivity getInstance()
    {
        return   activityA;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //signIn();
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}

