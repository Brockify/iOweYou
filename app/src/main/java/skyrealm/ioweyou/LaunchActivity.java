package skyrealm.ioweyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {

    Button registerButton, loginButton;
    SharedPreferences checkedSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //setup buttons
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        checkedSharedPreference = getSharedPreferences("RememberMe", MODE_PRIVATE);

        if(checkedSharedPreference.getBoolean("Checked", false))
        {
            Intent intent = new Intent(LaunchActivity.this, WhoOwesMeActivity.class);
            intent.putExtra("Username", checkedSharedPreference.getString("Username", null));
            finish();
            startActivity(intent);
        }


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
