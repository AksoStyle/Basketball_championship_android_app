package com.example.basketball_championship;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.basketball_championship.reg_log.LoginActivity;
import com.example.basketball_championship.reg_log.ProfileActivity;
import com.example.basketball_championship.reg_log.RegistrationActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeamsActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private boolean isUserLoggedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        isUserLoggedIn = (user != null);
        updateMenuButtonText();
    }

    @SuppressLint("SetTextI18n")
    private void updateMenuButtonText() {
        Button menuButton = findViewById(R.id.reglog_button);
        if (isUserLoggedIn) {
            menuButton.setText(getString(R.string.logout));

        } else {
            menuButton.setText(getString(R.string.reg_log));
        }
    }

    public void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("User").document(user.getUid());

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    boolean isAdmin = documentSnapshot.exists() && Boolean.TRUE.equals(documentSnapshot.getBoolean("Admin"));
                    popup.getMenu().findItem(R.id.menu_item_6).setVisible(isAdmin);

                    if (!isAdmin) {
                        popup.getMenu().findItem(R.id.menu_item_2).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_item_3).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_item_4).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_item_5).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_item_7).setVisible(false);
                    }
                    popup.show();
                }
            });
        } else {
            popup.getMenu().findItem(R.id.menu_item_2).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_3).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_4).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_5).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_6).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_7).setVisible(false);

            popup.show();
        }


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


    }

    // Menu registration/login
    public void showMenu_reglog(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_reg_log, popup.getMenu());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            popup.getMenu().findItem(R.id.menu_item_0).setVisible(false);
            popup.getMenu().findItem(R.id.menu_item_1).setVisible(false);
            popup.getMenu().add(0, R.id.menu_item_2, Menu.NONE, "Logout");
        }

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
                    case R.id.menu_item_2:
                        if (currentUser != null) {
                            FirebaseAuth.getInstance().signOut();
                            recreate();
                            navigateToMainPageActivity();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public void navigateToMainPageActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToNewsActivity() {
        Intent intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToPlayersActivity() {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToScoresActivity() {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToTeamsActivity() {
        Intent intent = new Intent(this, TeamsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToScheduleActivity() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToAdministratorActivity() {
        Intent intent = new Intent(this, AdministratorActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}