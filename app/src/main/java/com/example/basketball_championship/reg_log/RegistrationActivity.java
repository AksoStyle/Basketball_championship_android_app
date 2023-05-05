package com.example.basketball_championship.reg_log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
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
import com.example.basketball_championship.interfaces.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    EditText nameEditText;
    EditText emailEditText;
    EditText BirthdayEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userUid;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            userUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        BirthdayEditText = findViewById(R.id.birthdayEditText);

        BirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // current date
                final Calendar currentDate = Calendar.getInstance();
                int currentYear = currentDate.get(Calendar.YEAR);
                int currentMonth = currentDate.get(Calendar.MONTH);
                int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(currentYear - 18, currentMonth, currentDay);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);

                                int age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
                                if (currentDate.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)){
                                    age--;
                                }


                                if (age >= 18) {

                                    BirthdayEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Kérjük, hogy csak 18 évnél idősebb személy regisztráljon.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        currentYear - 18, currentMonth, currentDay);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
    }





    public void register(View view) throws ParseException {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String birthday = BirthdayEditText.getText().toString();
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = (Date)formatter.parse(birthday);


        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if(!password.equals(passwordAgain)){
            Log.e(LOG_TAG, "Nem egyezik a két jelszó.");
        }

        Log.i(LOG_TAG, "Regisztrált: " + name + ", Email: " + email + ", Születési dátum: " + birthday + ", jelszó: " + password);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userUid = user.getUid();
                        Log.d(LOG_TAG, "User created Successfully.");
                        Toast.makeText(RegistrationActivity.this, "User created Successfully.", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveUserToFirebase(name, userUid, email, date, false);
                                navigateToProfileActivity();
                                finish();
                            }
                        }, 2000);
                    } else {
                        Log.e(LOG_TAG, "FirebaseUser is null.");
                    }
                } else {
                    Log.d(LOG_TAG, "User was not created Successfully.");
                    Toast.makeText(RegistrationActivity.this, "User was not created Successfully.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveUserToFirebase(String name, String id, String email, Date birthDate, boolean isAdmin) {

        User user = new User() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getEmail() {
                return email;
            }

            @Override
            public Date getBirthDate() {
                return new Date(String.valueOf(birthDate));
            }

            @Override
            public boolean isAdmin() {
                return isAdmin;
            }
        };

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(id).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "User data saved to Firebase.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Error saving user data to Firebase.", e);
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