package it.polito.mad1819.group17.deliveryapp.restaurateur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad1819.group17.restaurateur.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText input_mail_login;
    private EditText input_password;
    private Button btn_login;
    private TextView label_invalid_credentials;
    private TextView label_sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        input_mail_login = findViewById(R.id.input_mail_login);
        input_password = findViewById(R.id.input_password_log_in);
        btn_login = findViewById(R.id.btn_login);
        label_invalid_credentials = findViewById(R.id.label_invalid_credentials);
        label_sign_in = findViewById(R.id.label_sign_in);

        btn_login.setOnClickListener(v -> {

            mAuth.signInWithEmailAndPassword(input_mail_login.getText().toString(), input_password.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FB", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_LONG).show();

                                mDatabase.child("restaurateurs").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Restaurateur restaurateur = (Restaurateur) dataSnapshot.getValue(Restaurateur.class);

                                        // go to MainActivity of the just logged restauratuer
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("restaurateur", restaurateur);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Unable to retrieve restaurater's information :(", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                label_invalid_credentials.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        label_sign_in.setOnClickListener(v ->

        {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
