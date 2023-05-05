package com.example.basketball_championship.reg_log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.basketball_championship.AdministratorActivity;
import com.example.basketball_championship.MainActivity;
import com.example.basketball_championship.NewsActivity;
import com.example.basketball_championship.PlayersActivity;
import com.example.basketball_championship.R;
import com.example.basketball_championship.ScheduleActivity;
import com.example.basketball_championship.ScoresActivity;
import com.example.basketball_championship.TeamsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    EditText emailET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.edittext_email);
        passwordET = findViewById(R.id.edittext_password);
    }

    public void login(View view){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        //Log.i(LOG_TAG, "Bejelentkezett: " + email + ", jelszó: " + password);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User signed in Successfully.");
                    Toast.makeText(LoginActivity.this, "User logged in successfully.", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigateToProfileActivity();
                            finish();
                        }
                    }, 2000);
                } else{
                    Log.d(LOG_TAG, "User could not sign in.");
                    Toast.makeText(LoginActivity.this, "User could not login.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_0:
                        navigateToMainPageActivity();
                        return true;
                    case R.id.menu_item_1:
                        navigateToNewsActivity();
                        return true;
                    case R.id.menu_item_2:
                        navigateToPlayersActivity();
                        return true;
                    case R.id.menu_item_3:
                        navigateToScoresActivity();
                        return true;
                    case R.id.menu_item_4:
                        navigateToTeamsActivity();
                        return true;
                    case R.id.menu_item_5:
                        navigateToScheduleActivity();
                        return true;
                    case R.id.menu_item_6:
                        navigateToAdministratorActivity();
                        return true;
                    case R.id.menu_item_7:
                        navigateToProfileActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu);
        popup.show();
    }

    // Menu registration/login
    public void showMenu_reglog(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_0:
                        navigateToRegistrationActivity();
                        return true;
                    case R.id.menu_item_1:
                        navigateToLoginActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_reg_log);
        popup.show();
    }

    public void navigateToMainPageActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void navigateToNewsActivity() {
        Intent intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
    }

    public void navigateToPlayersActivity() {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
    }

    public void navigateToScoresActivity() {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    public void navigateToTeamsActivity() {
        Intent intent = new Intent(this, TeamsActivity.class);
        startActivity(intent);
    }

    public void navigateToScheduleActivity() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void navigateToAdministratorActivity() {
        Intent intent = new Intent(this, AdministratorActivity.class);
        startActivity(intent);
    }

    public void navigateToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void navigateToRegistrationActivity(){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void navigateToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}