package skyrealm.ioweyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Cat on 12/31/2015.
 */
public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    String Username;
    Toolbar toolbar;
    TextView usernameTextView;
    SharedPreferences rememberMePreference, currentUserPreference;
    Bitmap userIcon = null;
    ImageView userIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        navigationView.getMenu().getItem(2).setChecked(true);
        setTitle("Search");

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
        LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
        int id = item.getItemId();
        //if who owes me is clicked
        if (id == R.id.nav_who_owes_me) {
            // Handle the camera action
            Intent intent = new Intent(SearchActivity.this, WhoOwesMeActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if who i owe is clicked
        } else if (id == R.id.nav_who_i_owe) {
            Intent intent = new Intent(SearchActivity.this, WhoIOweActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if profile is clicked
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(SearchActivity.this, SettingActivity.class);
            intent.putExtra("Username", Username);
            finish();
            startActivity(intent);
            this.overridePendingTransition(0, 0);

            //if search is clicked
        } else if (id == R.id.nav_search) {

            //if send money is clicked
        } else if (id == R.id.nav_send) {
            inflater = LayoutInflater.from(SearchActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            View view = inflater.inflate(R.layout.popup_send_payment, null);
            builder.setView(view);
            AlertDialog sendPaymentDialog = builder.create();
            sendPaymentDialog.show();

        } else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(SearchActivity.this, LaunchActivity.class);
            finish();
            startActivity(intent);
            rememberMePreference.edit().putBoolean("Checked", false).apply();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

