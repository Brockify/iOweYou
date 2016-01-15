package skyrealm.ioweyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cat on 12/31/2015.
 */
public class SettingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    String Username;
    Toolbar toolbar;
    TextView usernameTextView;
    SharedPreferences rememberMePreference, currentUserPreference;
    Button resetPasswordButton;
    AlertDialog resetPasswordAlertDialog, resetPinAlertDialog;
    ProgressDialog resetPasswordProgressDialog, resetPinProgressDialog;
    ListView settingListView;
    View currentView;
    Bitmap userIcon = null;
    ImageView userIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
        currentView = layoutInflater.inflate(R.layout.activity_setting, null);
        setContentView(currentView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        settingListView = (ListView) findViewById(R.id.settingListView);
        TextView header = new TextView(SettingActivity.this);
        header.setText("Account settings");
        header.setTextSize(25);
        header.setTypeface(null, Typeface.BOLD);

        settingListView.addHeaderView(header);

        //create custom adapter
        SettingsListAdapter settingsListAdapter = new SettingsListAdapter(SettingActivity.this);
        settingListView.setAdapter(settingsListAdapter);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1)
                {
                    LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    View popupView = inflater.inflate(R.layout.popup_reset_password, null);
                    builder.setView(popupView);

                    resetPasswordAlertDialog = builder.create();
                    resetPasswordAlertDialog.show();

                    Button resetPasswordButton = (Button) popupView.findViewById(R.id.resetPasswordButton);
                    final EditText oldPasswordEditText = (EditText) popupView.findViewById(R.id.oldPasswordEditText);
                    final EditText newPasswordEditText = (EditText) popupView.findViewById(R.id.newPasswordEditText);

                    resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isNetworkAvailable()) {
                                new reset_password().execute(Username, oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());
                            } else {
                                Snackbar.make(currentView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if(position == 2)
                {
                    LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    View popupView = inflater.inflate(R.layout.popup_reset_pin, null);
                    builder.setView(popupView);
                    resetPinAlertDialog = builder.create();
                    resetPinAlertDialog.show();

                    final EditText oldPinEditText = (EditText) popupView.findViewById(R.id.oldPinEditText);
                    final EditText newPinEditText = (EditText) popupView.findViewById(R.id.newPinEditText);
                    Button resetPinButton = (Button) popupView.findViewById(R.id.resetPinButton);

                    resetPinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isNetworkAvailable()) {
                                new reset_pin().execute(Username, oldPinEditText.getText().toString(), newPinEditText.getText().toString());
                            } else {
                                Snackbar.make(currentView, "Connection Not Available.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        rememberMePreference = getSharedPreferences("RememberMe", MODE_PRIVATE);

        //get username from extra
        Username = getIntent().getStringExtra("Username");

        currentUserPreference = getSharedPreferences(Username, MODE_PRIVATE);

        //set userIcon if it's not null in preferences
        if(!currentUserPreference.getString("img", "").equals(""))
        {
            byte[] b = Base64.decode(currentUserPreference.getString("img", ""), Base64.DEFAULT);
            userIcon = getCroppedBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
        }

        //setup the toggle with drawer open and drawer closed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            //when the drawer gets opened
            public void onDrawerOpened(View drawer)
            {
                super.onDrawerOpened(drawer);
                usernameTextView = (TextView) findViewById(R.id.usernameTextView);
                usernameTextView.setText(Username);
                userIconImageView = (ImageView) findViewById(R.id.userIconImageView);

                if(!currentUserPreference.getString("img", "").equals(""))
                {
                    byte[] b = Base64.decode(currentUserPreference.getString("img", ""), Base64.DEFAULT);
                    userIcon = getCroppedBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
                    userIconImageView.setImageBitmap(userIcon);
                }

            }
        };

        //set a drawer listener and toggle state
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set the initial drawer choice to the first one
        navigationView.getMenu().getItem(3).setChecked(true);
        setTitle("Setting");

    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
        int id = item.getItemId();
        //if who owes me is clicked
        if (id == R.id.nav_who_owes_me) {
            // Handle the camera action
            Intent intent = new Intent(SettingActivity.this, WhoOwesMeActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if who i owe is clicked
        } else if (id == R.id.nav_who_i_owe) {
            Intent intent = new Intent(SettingActivity.this, WhoIOweActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if profile is clicked
        } else if (id == R.id.nav_setting) {

            //if search is clicked
        } else if (id == R.id.nav_search) {
            Intent intent = new Intent(SettingActivity.this, SearchActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if send money is clicked
        } else if (id == R.id.nav_send) {
            inflater = LayoutInflater.from(SettingActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            View view = inflater.inflate(R.layout.popup_send_payment, null);
            builder.setView(view);
            AlertDialog sendPaymentDialog = builder.create();
            sendPaymentDialog.show();

        } else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(SettingActivity.this, LaunchActivity.class);
            finish();
            startActivity(intent);
            rememberMePreference.edit().putBoolean("Checked", false).apply();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class reset_password extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resetPasswordProgressDialog = new ProgressDialog(SettingActivity.this);
            resetPasswordProgressDialog.setCancelable(false);
            resetPasswordProgressDialog.setMessage("Resetting password");
            resetPasswordProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/reset_password.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));
            nameValuePair.add(new BasicNameValuePair("OldPassword", params[1]));
            nameValuePair.add(new BasicNameValuePair("NewPassword", params[2]));


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
            Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("\nPassword reset\n")) {
                resetPasswordAlertDialog.dismiss();
            }
            resetPasswordProgressDialog.cancel();
        }
    }

    public class reset_pin extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resetPinProgressDialog = new ProgressDialog(SettingActivity.this);
            resetPinProgressDialog.setCancelable(false);
            resetPinProgressDialog.setMessage("Resetting Pin");
            resetPinProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/iOweYou/reset_pin.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));
            nameValuePair.add(new BasicNameValuePair("OldPin", params[1]));
            nameValuePair.add(new BasicNameValuePair("NewPin", params[2]));


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
            Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("\nPin reset\n")) {
                resetPinAlertDialog.dismiss();
            }
            resetPinProgressDialog.cancel();
        }
    }
}
