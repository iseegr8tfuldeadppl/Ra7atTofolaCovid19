package croissonrouge.darelbeida.competitions;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import croissonrouge.darelbeida.competitions.SQLite.SQL;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        variables();
        fonts();
        images();

        sql();
        if(SQLSharing.mycursor.getCount()<=1){
            SQLSharing.mydb.insertData("");
            SQLSharing.mydb.insertData("");
            SQLSharing.mydb.insertData("");
            SQLSharing.mydb.insertData("");
            SQLSharing.mydb.insertData("");
            SQLSharing.mydb.insertData("");
        }
        close_sql();
    }

    @Override
    protected void onResume() {
        super.onResume();
        netcheck();
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }

    private void sql() {
        SQLSharing.mydb = SQL.getInstance(getApplicationContext());
        SQLSharing.mycursor = SQLSharing.mydb.getData();
    }

    private void netcheck() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
            if (mAuth.getCurrentUser() != null) {
                final String uid = mAuth.getCurrentUser().getUid();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = database.getReference("users").child(uid);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loadingscreen.setVisibility(GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        print(getResources().getString(R.string.doyouhaveinternet));
                    }
                });
            }

        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if (!task.isSuccessful()) {
                                print(getResources().getString(R.string.doyouhaveinternet));
                                netcheck();
                            } else {
                                loadingscreen.setVisibility(GONE);
                            }
                        }
                    });
        }
    }

    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        tofolaa.setTypeface(font);
        parental.setTypeface(font);
        tofola.setTypeface(font);
        drawing.setTypeface(font);
        connecting.setTypeface(font);
        reading.setTypeface(font);
        cooking.setTypeface(font);
        organization.setTypeface(font);
        creditter.setTypeface(font);
    }

    private TextView tofola, connecting, organization, creditter, parental, tofolaa;
    private Button drawing, reading, cooking;
    private ImageView logo, logo2;
    private FrameLayout loadingscreen;
    private void variables() {
        tofolaa = findViewById(R.id.tofolaa);
        parental = findViewById(R.id.parental);
        cooking = findViewById(R.id.cooking);
        tofola = findViewById(R.id.tofola);
        reading = findViewById(R.id.reading);
        drawing = findViewById(R.id.drawing);
        logo = findViewById(R.id.logo);
        organization = findViewById(R.id.organization);
        logo2 = findViewById(R.id.logo2);
        loadingscreen = findViewById(R.id.loadingscreen);
        connecting = findViewById(R.id.connecting);
        creditter = findViewById(R.id.creditter);
    }

    private long backPressedTime;
    private Toast backToast;
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.areyousure), Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void images() {
        try {
            Glide.with(this).load(R.drawable.logo).into(logo);
        } catch (Exception ignored) {
            logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }
        try {
            Glide.with(this).load(R.drawable.logogg).into(logo2);
        } catch (Exception ignored) {
            logo2.setImageDrawable(getResources().getDrawable(R.drawable.logogg));
        }
    }

    public void drawingsClicked(View view) {
        Intent opendrawingsMain = new Intent(this, DrawingsMain.class);
        startActivity(opendrawingsMain);
        finish();
    }



    public void readingClicked(View view) {
        Intent opendrawingsMain = new Intent(this, ReadingMain.class);
        startActivity(opendrawingsMain);
        finish();
    }

    public void cookingClicked(View view) {
        Intent opendrawingsMain = new Intent(this, CookingMain.class);
        startActivity(opendrawingsMain);
        finish();
    }
}
