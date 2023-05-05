package com.example.basketball_championship;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.basketball_championship.interfaces.Championships;
import com.example.basketball_championship.interfaces.User;
import com.example.basketball_championship.reg_log.LoginActivity;
import com.example.basketball_championship.reg_log.ProfileActivity;
import com.example.basketball_championship.reg_log.RegistrationActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import kotlin.random.Random;

public class AdministratorActivity extends AppCompatActivity {
    private static final String LOG_TAG = AdministratorActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isUserLoggedIn = false;
    private TableLayout userTableLayout;
    private TableLayout championshipTableLayout;


    EditText championship_name;
    EditText championship_date;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        isUserLoggedIn = (user != null);
        updateMenuButtonText();

        userTableLayout = findViewById(R.id.user_table);
        championshipTableLayout = findViewById(R.id.championship_table);

        showUserTable();
        showChampionshipTable();

        championship_name = findViewById(R.id.name_edittext);
        championship_date = findViewById(R.id.date_edittext);



        championship_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar tomorrowDate = Calendar.getInstance();
                tomorrowDate.add(Calendar.DAY_OF_MONTH, 1);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AdministratorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                // format the selected date to a string
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                String dateString = formatter.format(selectedDate.getTime());
                                // set the date string to the EditText
                                championship_date.setText(dateString);
                            }
                        },
                        tomorrowDate.get(Calendar.YEAR), tomorrowDate.get(Calendar.MONTH), tomorrowDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(tomorrowDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });




    }



    public void addChampionship(View view) throws ParseException{
        String name = championship_name.getText().toString();
        String date = championship_date.getText().toString();
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date d = (Date)formatter.parse(date);



        if (name.isEmpty()) {
            championship_name.setError("A név megadása kötelező!");
            championship_name.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            championship_date.setError("A dátum megadása kötelező!");
            championship_date.requestFocus();
            return;
        }

        saveChampionshipToFirebase(name,generateFirebaseId(), d);

    }

    private void saveChampionshipToFirebase( String name, String id, Date date) {

        Championships championships = new Championships() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public Date getDate() {
                return date;
            }

        };
        db.collection("Championships")
                .add(championships)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AdministratorActivity.this, "A bajnokság hozzáadva", Toast.LENGTH_LONG).show();
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdministratorActivity.this, "Hiba történt a bajnokság hozzáadása során", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public String generateFirebaseId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void showUserTable() {
        db.collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TableRow tr = new TableRow(this);
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                            params.setMargins(40, 15, 40, 15);


                            TextView idTextView = new TextView(this);
                            idTextView.setText(document.getString("id"));
                            idTextView.setLayoutParams(params);
                            tr.addView(idTextView);

                            TextView nameTextView = new TextView(this);
                            nameTextView.setText(document.getString("name"));
                            nameTextView.setLayoutParams(params);
                            tr.addView(nameTextView);

                            TextView emailTextView = new TextView(this);
                            emailTextView.setText(document.getString("email"));
                            emailTextView.setLayoutParams(params);
                            tr.addView(emailTextView);

                            TextView birthDateTextView = new TextView(this);
                            Timestamp birthDateTimestamp = document.getTimestamp("birthDate");
                            if (birthDateTimestamp != null) {
                                Date birthDate = birthDateTimestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String birthDateString = sdf.format(birthDate);
                                birthDateTextView.setText(birthDateString);
                            }
                            birthDateTextView.setLayoutParams(params);

                            tr.addView(birthDateTextView);


                            TextView adminTextView = new TextView(this);
                            Boolean isAdmin = document.getBoolean("Admin");
                            if (isAdmin != null) {
                                adminTextView.setText(isAdmin.toString());
                            } else {
                                adminTextView.setText("N/A");
                            }
                            adminTextView.setLayoutParams(params);
                            tr.addView(adminTextView);

                            userTableLayout.addView(tr);
                        }
                    } else {
                        Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void showChampionshipTable() {
        db.collection("Championships")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TableRow tr = new TableRow(this);
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                            params.setMargins(40, 15, 40, 15);

                            TextView idTextView = new TextView(this);
                            idTextView.setText(document.getString("id"));
                            idTextView.setLayoutParams(params);
                            tr.addView(idTextView);

                            TextView nameTextView = new TextView(this);
                            nameTextView.setText(document.getString("name"));
                            nameTextView.setLayoutParams(params);
                            tr.addView(nameTextView);

                            TextView dateTextView = new TextView(this);
                            Timestamp dateTimestamp = document.getTimestamp("date");
                            if (dateTimestamp != null) {
                                Date date = dateTimestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String dateString = sdf.format(date);
                                dateTextView.setText(dateString);
                            }
                            dateTextView.setLayoutParams(params);
                            tr.addView(dateTextView);

                            Button deleteButton = new Button(this);
                            deleteButton.setText("delete");
                            deleteButton.setLayoutParams(params);

                            deleteButton.setOnClickListener(v -> {
                                db.collection("Championships").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Document successfully deleted!", Toast.LENGTH_SHORT).show();
                                            userTableLayout.removeView(tr);
                                            recreate();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error deleting document", Toast.LENGTH_SHORT).show();
                                            Log.w(LOG_TAG, "Error deleting document", e);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    recreate();
                                                }
                                            }, 2000);

                                        });
                            });
                            tr.addView(deleteButton);

                            championshipTableLayout.addView(tr);
                        }
                    } else {
                        Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                    }
                });
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
