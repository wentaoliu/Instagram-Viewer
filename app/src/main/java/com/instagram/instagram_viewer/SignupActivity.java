package com.instagram.instagram_viewer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private UserRegisterTask mRegisterTask = null;


    private EditText mEmailView;
    private EditText mPasswordView;
    private  EditText mReEnterPasswordView;
    private Button mSignUpButton;
    private TextView mLoginLinkView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mReEnterPasswordView = (EditText) findViewById(R.id.reEnterPassword);
        mSignUpButton = (Button) findViewById(R.id.btn_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        mLoginLinkView = (TextView) findViewById(R.id.link_login);
        mLoginLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignUpButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String email = mEmailView.getText().toString();
        String password1 = mPasswordView.getText().toString();
        String password2 = mReEnterPasswordView.getText().toString();


        // TODO: Implement your own signup logic here.
        mRegisterTask = new UserRegisterTask(email, password1,password2);
        mRegisterTask.execute((Void) null);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        mSignUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email =mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String reEnterPassword = mReEnterPasswordView.getText().toString();



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailView.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailView.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordView.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            mReEnterPasswordView.setError("Password Do not match");
            valid = false;
        } else {
            mReEnterPasswordView.setError(null);
        }

        return valid;
    }
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword1;
        private  final String mPassword2;

        UserRegisterTask(String email, String password, String reEnterPassword) {
            mEmail = email;
            mPassword1 = password;
            mPassword2 = reEnterPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("username", mEmail)
                        .add("password1", mPassword1)
                        .add("password2",mPassword2)
                        .build();
                String response = post("http://imitagram.wnt.io/register", formBody);
                if (response.contains("key")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    String[] tmp = response.split(":");
                    String token = tmp[1].replace("\"","");
                    String token_final = "token "+ token.trim();

                    intent.putExtra("token",token_final);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            } catch (IOException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

    }


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url,RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}