package com.example.basketball_championship.reg_log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basketball_championship.AdministratorActivity;
import com.example.basketball_championship.MainActivity;
import com.example.basketball_championship.NewsActivity;
import com.example.basketball_championship.PlayersActivity;
import com.example.basketball_championship.R;
import com.example.basketball_championship.ScheduleActivity;
import com.example.basketball_championship.ScoresActivity;
import com.example.basketball_championship.TeamsActivity;
import com.example.basketball_championship.interfaces.User;
import com.example.basketball_championship.interfaces.UserImpl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;


    private TextView nameTextView;
    private TextView emailTextView;
    private TextView birthdateTextView;

    private ListenerRegistration userListener;

    private boolean isUserLoggedIn = false;

    public ProfileActivity(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        isUserLoggedIn = (user != null);
        updateMenuButtonText();

        db = FirebaseFirestore.getInstance();


        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        birthdateTextView = findViewById(R.id.birthdate_text_view);

        EditText updatenametext = findViewById(R.id.name_update_text);
        EditText updateemailtext = findViewById(R.id.email_update_text);

        Button updatebutton = findViewById(R.id.update_button);
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = updatenametext.getText().toString();
                String email = updateemailtext.getText().toString();
                updateUser(user.getUid(), name, email);

            }
        });
    }
    public void updateUser(String uid, String username, String email){
        if(email.isEmpty()){
            Toast.makeText(ProfileActivity.this, "Az email nem lehet üres.", Toast.LENGTH_LONG).show();

            return;
        }
        if(username.isEmpty()){
            Toast.makeText(ProfileActivity.this, "A felhasznalónév nem lehet üres.", Toast.LENGTH_LONG).show();

            return;
        }

        db.collection("User").document(uid).update("name", username, "email", email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfileActivity.this, "Sikeres frissítés.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Nem sikerült frissíteni az adatokat.", Toast.LENGTH_LONG).show();
            }
        });


    }




    @Override
    public void onStart() {
        super.onStart();
        Query userQuery = db.collection("User").whereEqualTo("id", mAuth.getUid());
        userListener = userQuery.addSnapshotListener(this, new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(LOG_TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && !snapshot.isEmpty()) {
                    DocumentSnapshot document = snapshot.getDocuments().get(0);
                    User user = document.toObject(UserImpl.class);

                    if (user != null) {
                        nameTextView.setText(user.getName());
                        emailTextView.setText(user.getEmail());
                        Date birthdate = user.getBirthDate();
                        if (birthdate != null) {
                            String formattedBirthdate = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault()).format(birthdate);
                            birthdateTextView.setText(formattedBirthdate);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
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