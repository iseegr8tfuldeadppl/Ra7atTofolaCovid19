package croissonrouge.darelbeida.competitions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class Main2Activity extends AppCompatActivity {

    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sql();
        linesinsql = SQLSharing.mycursor.getCount();
        if(linesinsql>0){
            SQLSharing.mycursor.moveToFirst();
            _ID = SQLSharing.mycursor.getString(0);}
        permissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sql() {
        SQLSharing.mydb = SQL.getInstance(getApplicationContext());
        SQLSharing.mycursor = SQLSharing.mydb.getData();
    }

    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        tofolaa.setTypeface(font);
        boob2.setTypeface(font);
        parental.setTypeface(font);
        enter.setTypeface(font);
        name.setTypeface(font);
        tofola.setTypeface(font);
        organization.setTypeface(font);
        creditter.setTypeface(font);
        titler1.setTypeface(font);
        titler2.setTypeface(font);
        boob.setTypeface(font);
    }

    private TextView tofola, organization, creditter, parental, titler1, titler2, boob, boob2, tofolaa;
    private Button enter;
    private ImageView logo, logo2;
    private FrameLayout loadingscreen;
    private void variables() {
        boob2 = findViewById(R.id.boob2);
        fuckyouloadingscreen = findViewById(R.id.fuckyouloadingscreen);
        parental = findViewById(R.id.parental);
        loadingscreen = findViewById(R.id.loadingscreen);
        name = findViewById(R.id.name);
        tofola = findViewById(R.id.tofola);
        organization = findViewById(R.id.organization);
        enter = findViewById(R.id.enter);
        logo = findViewById(R.id.logo);
        logo2 = findViewById(R.id.logo2);
        creditter = findViewById(R.id.creditter);
        boob = findViewById(R.id.boob);
        titler2 = findViewById(R.id.titler2);
        titler1 = findViewById(R.id.titler1);
        tofolaa = findViewById(R.id.tofolaa);
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


    private void runfrontpage(){
        variables();
        enterkeylistener();
        fonts();
        images();
    }

    private void enterkeylistener() {
        name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_ENTER) { //Whenever you got user click enter. Get text in edittext and check it equal test1. If it's true do your code in listenerevent of button3
                    if(!String.valueOf(name.getText()).equals("")){
                        loadingscreen.setVisibility(View.VISIBLE);
                        login_reload_firebase_login();
                    } else {
                        print(getResources().getString(R.string.ggef));
                    }
                }

                return true;
            }
        });
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

    private String theirname;
    private int linesinsql;
    private String _ID;
    private void isalreadyconnected() {
        sql();
        linesinsql = SQLSharing.mycursor.getCount();
        if(linesinsql>0){
            SQLSharing.mycursor.moveToFirst();
            _ID = SQLSharing.mycursor.getString(0);

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                print("still connected");
                outter();
            } else {
                only_login_to_firebase(false);
            }
        } else {
            only_login_to_firebase(false);
        }
    }

    private void outter() {
        close_sql();
        Intent wein = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(wein);
        finish();
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }

    private final int STORAGE_REQUEST_CODE = 23;
    private LinearLayout fuckyouloadingscreen;
    private void permissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            runfrontpage();


            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        } else {
            runfrontpage();
            fuckyouloadingscreen.setVisibility(View.GONE);
            checknameinfirebse();
        }
    }


    public void menuClicked(View view) {
        permissions();
    }

    private boolean firstrequest = true;
    private boolean thirdrequest = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(STORAGE_REQUEST_CODE==requestCode && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                if(firstrequest){
                    firstrequest = false;
                    fuckyouloadingscreen.setVisibility(View.VISIBLE);
                } else {
                    if(!thirdrequest){
                        permissions();
                        thirdrequest = true;
                    } else {
                        // send to a menu
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            } else {
                runfrontpage();
                fuckyouloadingscreen.setVisibility(View.GONE);
                checknameinfirebse();
            }
        }
    }

    private void exit() {
        finish();
    }

    public void confirmClicked(View view) {
        if(!String.valueOf(name.getText()).equals("")){
            loadingscreen.setVisibility(View.VISIBLE);
            login_reload_firebase_login();
        } else {
            print(getResources().getString(R.string.ggef));
        }
    }

    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    private FirebaseAuth mAuth;
    private void login_reload_firebase_login() {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
            if (mAuth.getCurrentUser() != null) {
                final String uid = mAuth.getCurrentUser().getUid();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = database.getReference("users").child(uid);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        theirname = String.valueOf(name.getText());
                        if(!theirname.equals("")){
                            loadingscreen.setVisibility(View.VISIBLE);
                            if(linesinsql==1){
                                SQLSharing.mydb.updateData(_ID, theirname);
                            } else {
                                SQLSharing.mydb.insertData(theirname);
                            }
                            userRef.child("name").setValue(theirname);
                            outter();
                        } else {
                            print(getResources().getString(R.string.ggef));
                            loadingscreen.setVisibility(View.GONE);
                        }
                        /*userRef.child("drawingmaindisplayversion").setValue("0");
                        userRef.child("cookingmaindisplayversion").setValue("0");
                        userRef.child("readingmaindisplayversion").setValue("0");*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        print(getResources().getString(R.string.doyouhaveinternet));
                        login_reload_firebase_login();
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
                                login_reload_firebase_login();
                            } else {

                                String uid = mAuth.getCurrentUser().getUid();
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference("users").child(uid).child("name");
                                theirname = String.valueOf(name.getText());
                                userRef.child("name").setValue(theirname);
                                if(linesinsql==1){
                                    SQLSharing.mydb.updateData(_ID, theirname);
                                } else {
                                    SQLSharing.mydb.insertData(theirname);
                                }
                                outter();

                            }
                        }
                    });
        }
    }




    private void checknameinfirebse() {
        try{
            mAuth = FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().reload();
                if (mAuth.getCurrentUser() != null) {
                    final String uid = mAuth.getCurrentUser().getUid();

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference("users").child(uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Object name = dataSnapshot.child("name").getValue();
                            if(name!=null){
                                only_login_to_firebase(true);
                            } else {
                                login_reload_firebase_login();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            print(getResources().getString(R.string.doyouhaveinternet));
                            login_reload_firebase_login();
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
                                    login_reload_firebase_login();
                                } else {

                                    String uid = mAuth.getCurrentUser().getUid();
                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference userRef = database.getReference("users").child(uid).child("name");
                                    theirname = String.valueOf(name.getText());
                                    userRef.child("name").setValue(theirname);
                                    if(linesinsql==1){
                                        SQLSharing.mydb.updateData(_ID, theirname);
                                    } else {
                                        SQLSharing.mydb.insertData(theirname);
                                    }
                                    outter();

                                }
                            }
                        });
            }
        } catch(Exception e){
            Log.i("HH", e.toString());
            only_login_to_firebase(false);
        }
    }

    private void only_login_to_firebase(final boolean b) {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
            loadingscreen.setVisibility(View.GONE);
            if(b)
                outter();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if (!task.isSuccessful()) {
                                print(getResources().getString(R.string.doyouhaveinternet));
                                only_login_to_firebase(b);
                            } else {
                                loadingscreen.setVisibility(View.GONE);
                                if(b)
                                    outter();
                            }
                        }
                    });
        }
    }

    public void doneClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            print(getResources().getString(R.string.dpoo));
        }
    }
}
