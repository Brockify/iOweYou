package skyrealm.ioweyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, confirmPasswordEditText, emailEditText, pinEditText;
    Button registerButton;
    search_username_in_database searchUsername;
    search_email_in_database searchEmail;
    int email = 0, password = 0, username = 0, pin = 0;
    register_user registerUser;
    ProgressDialog pDialog;
    View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
        currentView = inflater.inflate(R.layout.activity_register, null);
        setContentView(currentView);

        //setup the edit texts and buttons
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        pinEditText = (EditText) findViewById(R.id.pinEditText);


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isNetworkAvailable()) {
                    if (searchUsername != null) {
                        searchUsername.cancel(true);
                    }
                    if (s.length() >= 4) {
                        searchUsername = new search_username_in_database();
                        searchUsername.execute(s.toString().toLowerCase());
                    } else {
                        usernameEditText.setBackgroundColor(Color.WHITE);
                        username = 0;
                    }
                } else {
                    Snackbar.make(currentView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 6)
                {
                    passwordEditText.setBackgroundColor(Color.GREEN);
                    password = 1;
                } else if(s.length() == 0) {
                    passwordEditText.setBackgroundColor(Color.WHITE);
                    password = 0;
                } else {
                    passwordEditText.setBackgroundColor(Color.RED);
                    password = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(passwordEditText.getText().toString()))
                {
                    confirmPasswordEditText.setBackgroundColor(Color.GREEN);
                } else {
                    confirmPasswordEditText.setBackgroundColor(Color.RED);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isNetworkAvailable()) {
                    if (searchEmail != null) {
                        searchEmail.cancel(true);
                    }
                    if (s.length() < 4) {
                        emailEditText.setBackgroundColor(Color.WHITE);
                    } else {
                        searchEmail = new search_email_in_database();
                        searchEmail.execute(s.toString().toLowerCase());
                    }
                } else {
                    Snackbar.make(currentView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username == 1 && password == 1 && email == 1) {
                    if (isNetworkAvailable()) {
                        registerUser = new register_user();
                        registerUser.execute(usernameEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString(), pinEditText.getText().toString());
                        username = 0;
                        password = 0;
                        email = 0;
                        pin = 0;
                    } else {
                        Snackbar.make(currentView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill out all fields correctly before registering", Toast.LENGTH_LONG).show();
                }
            }
        });

        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 4)
                {
                    pinEditText.setBackgroundColor(Color.GREEN);
                    pin = 1;
                } else if(s.length() == 0) {
                    pinEditText.setBackgroundColor(Color.WHITE);
                    pin = 0;
                } else {
                    pinEditText.setBackgroundColor(Color.RED);
                    pin = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class search_username_in_database extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response;
            String responseBody = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/check_username.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                responseBody =EntityUtils.toString(response.getEntity());
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }


            return responseBody;
        }

        protected void onPostExecute(String result)
        {
            if(result.equals("\ntrue\n"))
            {
                usernameEditText.setBackgroundColor(Color.GREEN);
                username = 1;
            } else {
                usernameEditText.setBackgroundColor(Color.RED);
                username = 0;
            }
        }
    }

    public class search_email_in_database extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response;
            String responseBody = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/check_email.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Email", params[0]));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                responseBody =EntityUtils.toString(response.getEntity());
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }


            return responseBody;
        }

        protected void onPostExecute(String result)
        {
            if(result.equals("\ntrue\n"))
            {
                emailEditText.setBackgroundColor(Color.GREEN);
                email = 1;
            } else {
                emailEditText.setBackgroundColor(Color.RED);
                email = 0;
            }
        }
    }

    public class register_user extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Registering user..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/register_user.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));
            nameValuePair.add(new BasicNameValuePair("Password", params[1]));
            nameValuePair.add(new BasicNameValuePair("Email", params[2]));
            nameValuePair.add(new BasicNameValuePair("Pin", params[3]));


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
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            pDialog.cancel();
            Intent intent = new Intent(RegisterActivity.this, LaunchActivity.class);
            finish();
            startActivity(intent);

        }
    }
}
