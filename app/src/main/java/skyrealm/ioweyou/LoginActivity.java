package skyrealm.ioweyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, verificationEditText, resendUsernameEditText;
    TextView resendTextView, forgotPasswordTextView;
    Button loginButton, verifyButton;
    ProgressDialog pDialog, verifyDialog, resendDialog, forgotPasswordDialog;
    AlertDialog verifyAlert, resendVerifyAlert, forgotPasswordAlertDialog;
    View loginView;
    CheckBox rememberCheckBox;
    SharedPreferences rememberMePreference, currentUserSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        loginView = inflater.inflate(R.layout.activity_login, null);
        setContentView(loginView);

        //setup the button / edit texts
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        resendTextView = (TextView) findViewById(R.id.resendTextView);
        rememberCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        rememberCheckBox.setChecked(false);

        rememberMePreference = getSharedPreferences("RememberMe", MODE_PRIVATE);

        rememberCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rememberCheckBox.isChecked())
                {
                    rememberMePreference.edit().putBoolean("Checked", true).apply();
                } else {
                    rememberMePreference.edit().putBoolean("Checked", false).apply();
                }
            }
        });


        //login button clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    new check_login().execute(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                } else {
                    Snackbar.make(loginView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        //resend verification textview clicked
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = layoutInflater.inflate(R.layout.popup_resend_verification, null);
                builder.setView(view);
                resendVerifyAlert = builder.create();
                resendVerifyAlert.show();

                Button resendButton = (Button) view.findViewById(R.id.resendButton);
                resendUsernameEditText = (EditText) view.findViewById(R.id.usernameEditText);

                resendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isNetworkAvailable()) {
                            new resend_verification_code().execute(resendUsernameEditText.getText().toString());
                        } else {
                            Snackbar.make(loginView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = inflater.inflate(R.layout.popup_forgot_password, null);
                builder.setView(view);
                forgotPasswordAlertDialog = builder.create();
                forgotPasswordAlertDialog.show();

                final EditText usernameEditText = (EditText) view.findViewById(R.id.usernameEditText);
                Button sendPasswordButton = (Button) view.findViewById(R.id.sendButton);

                sendPasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isNetworkAvailable()) {
                            new reset_password().execute(usernameEditText.getText().toString());
                        } else {
                            Snackbar.make(loginView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Overrides onBackPressed
    public void onBackPressed()
    {
        Intent intent = new Intent(LoginActivity.this, LaunchActivity.class);
        startActivity(intent);
        finish();
    }

    //check if the login credentials are correct and logs in if they are
    public class check_login extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Logging in..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/login_user.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));
            nameValuePair.add(new BasicNameValuePair("Password", params[1]));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Failed";
        }

        protected void onPostExecute(String result)
        {
            String test = result.replace("\n", "");
            if(test.toLowerCase().equals(usernameEditText.getText().toString().toLowerCase()))
            {
                Intent intent = new Intent(LoginActivity.this, WhoOwesMeActivity.class);
                intent.putExtra("Username", result.replace("\n", ""));
                rememberMePreference.edit().putString("Username", result.replace("\n", "")).apply();
                currentUserSharedPreference = getSharedPreferences(test, MODE_PRIVATE);
                finish();
                startActivity(intent);
            } else if(result.equals("\nIncorrect Password\n"))
            {
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            } else if(result.equals("\nNone\n")) {
                Toast.makeText(LoginActivity.this, "No user exist with that username", Toast.LENGTH_SHORT).show();
            } else {
                LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = inflater.inflate(R.layout.popup_verify, null);
                builder.setView(view);
                verifyAlert = builder.create();
                verifyAlert.show();
                final String verification = result.replace("\n", "");

                verifyButton = (Button) view.findViewById(R.id.verifyButton);
                verificationEditText = (EditText) view.findViewById(R.id.verificationEditText);

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(verificationEditText.getText().toString().equals(verification))
                        {
                            Toast.makeText(LoginActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                            new set_verification().execute(usernameEditText.getText().toString());
                        } else {
                            Toast.makeText(LoginActivity.this, "Wrong Verification Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            pDialog.cancel();
        }
    }

    //checks to make sure the verification is correct and verifys the user if it is
    public class set_verification extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verifyDialog = new ProgressDialog(LoginActivity.this);
            verifyDialog.setCancelable(false);
            verifyDialog.setMessage("Verifying..");
            verifyDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/set_verify.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void Result)
        {
            verifyDialog.cancel();
            verifyAlert.cancel();
        }
    }

    //resends verification code if the user needs it
    public class resend_verification_code extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resendDialog = new ProgressDialog(LoginActivity.this);
            resendDialog.setCancelable(false);
            resendDialog.setMessage("Resending verification..");
            resendDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            String responseBody= null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/resend_verification.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                responseBody = EntityUtils.toString(response.getEntity());
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            if(s.equals("\nVerification resent\n"))
            {
                resendVerifyAlert.cancel();
            }
            resendDialog.cancel();
        }
    }

    public class reset_password extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            forgotPasswordDialog = new ProgressDialog(LoginActivity.this);
            forgotPasswordDialog.setCancelable(false);
            forgotPasswordDialog.setMessage("Resetting password");
            forgotPasswordDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/send_temp_password.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            if(s.equals("\nPassword sent to email\n"))
            {
                forgotPasswordAlertDialog.dismiss();
            }
            forgotPasswordDialog.cancel();
        }
    }
}
