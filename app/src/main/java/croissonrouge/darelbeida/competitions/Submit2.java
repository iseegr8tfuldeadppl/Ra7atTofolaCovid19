package croissonrouge.darelbeida.competitions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import croissonrouge.darelbeida.competitions.SQLite.SQL;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Submit2 extends AppCompatActivity {

    private Button btnCamera;
    private Button btnUpload;
    private TextView title, percentage;
    private FrameLayout loadingscreen;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private String idd;
    private boolean step1 = true;
    private boolean step2 = false;
    private boolean step3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit2);

        try{
            variables();
            fonts();
            firebase();
            sql();
            SQLSharing.mycursor.moveToFirst();
            SQLSharing.mycursor.moveToNext();
            SQLSharing.mycursor.moveToNext();
            idd = SQLSharing.mycursor.getString(0);
            String imagename = SQLSharing.mycursor.getString(1);
            SQLSharing.mycursor.moveToNext();
            idd2 = SQLSharing.mycursor.getString(0);
            String temp = SQLSharing.mycursor.getString(1);
            SQLSharing.mycursor.moveToNext();
            idd3 = SQLSharing.mycursor.getString(0);
            if(!temp.equals("")){
                titletext = temp;
                resume.setVisibility(VISIBLE);
                step2 = true;
                step1 = false;
                title.setVisibility(GONE);
                resumetext = SQLSharing.mycursor.getString(1);
                booktitle.setText(titletext);
                resume.setText(resumetext);
                /*btnCamera.setVisibility(VISIBLE);*/
            }
            close_sql();
            if(!imagename.equals("")) {
                if (pathtoimage(imagename)) {
                    /*btnCamera.setVisibility(VISIBLE);*/
                    loadingscreen2.setVisibility(VISIBLE);
                    step1 = false;
                    step2 = true;// TODO revert
                    step3 = false;
                    load_from_storage_and_display(this);
                }
            }
        } catch(Exception e){
            Log.i("HH", "bruh2 " + e);
        }
    }

    private String idd2, idd3;
    private boolean pathtoimage(String imagename) {
        allow_upload = false;
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";
        String submissionpath = mysubmissionsfolderpath + "/" + imagename + ".jpg";
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        return mysubmission.exists();
    }
    private Bitmap image;
    private void load_from_storage_and_display(final Context context) {
        Runnable descaleimageandsaveitRunnable=new Runnable() {@Override public void run() {
            // TODO: check image size and set descale accordingly
            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(mysubmission));
                displayfromstorage.sendEmptyMessage(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }};
        Thread descaleimageandsaveitThread = new Thread(descaleimageandsaveitRunnable);
        descaleimageandsaveitThread.start();
    }

    private Handler displayfromstorage = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            /*btnCamera.setText(getResources().getString(R.string.changepicture));*/
            loadingscreen2.setVisibility(GONE);
            image = null; // TODO if causes issues remove it
            return true; }
    });

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

    private void firebase() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        btnUpload.setTypeface(font);
        title.setTypeface(font);
        percentage.setTypeface(font);
    }

    public void cameraClicked(View view) {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnCamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            newone = true;
            takePicture();
        }*/
    }
    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    private Uri file;
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean success = prepare_link();
        if(success){
            file = Uri.fromFile(mysubmission);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

            startActivityForResult(intent, 100);
        } else {
            print(getResources().getString(R.string.gggg));
        }
    }

    private File MySubmissionsFolder, mysubmission;
    private String submissionpurename;
    private boolean newone = true;
    private boolean allow_upload = false;
    private boolean prepare_link() {
        if(newone) {
            newone = false;
            allow_upload = true;
            // Assume block needs to be inside a Try/Catch block.
            String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";
            submissionpurename = random();

            String submissionpath = mysubmissionsfolderpath + "/" + submissionpurename + ".jpg";
            MySubmissionsFolder = new File(mysubmissionsfolderpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        }
        boolean success = true;
        if (!MySubmissionsFolder.exists()) {
            success = MySubmissionsFolder.mkdirs();
        }
        return success;
    }

    private int MAX_LENGTH = 16;
    public String random() {
        return RandomStringUtils.randomAlphanumeric(MAX_LENGTH);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(0==requestCode && grantResults.length > 0){
            if(grantResults[0] != PackageManager.PERMISSION_DENIED){
                btnCamera.setEnabled(true);
                newone = true;
                takePicture();
            }
        }
    }*/

    private FrameLayout loadingscreen2;
    private EditText booktitle, resume;
    private void variables() {

        resume = findViewById(R.id.resume);
        booktitle = findViewById(R.id.bookname);
        loadingscreen2 = findViewById(R.id.loadingscreen2);
        title = findViewById(R.id.title);
        percentage = findViewById(R.id.percentage);
        loadingscreen = findViewById(R.id.loadingscreen);
        btnUpload = findViewById(R.id.btnUpload);
        /*btnCamera = findViewById(R.id.btnCamera);*/
    }


    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100 && resultCode == RESULT_OK) {
                final Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                /*btnCamera.setText(getResources().getString(R.string.changepicture));*/
                filePath = file;
                Runnable descaleimageandsaveitRunnable=new Runnable() {@Override public void run() {
                    try {
                        // TODO: check image size and set descale accordingly
                        storeImage(image);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                } };
                Thread descaleimageandsaveitThread = new Thread(descaleimageandsaveitRunnable);
                descaleimageandsaveitThread.start();

            } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();
                // Setting image on image view using Bitmap
                final Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                /*btnCamera.setVisibility(View.INVISIBLE);*/

                Runnable descaleimageandsaveitRunnable=new Runnable() {@Override public void run() {
                    // TODO: check image size and set descale accordingly
                    try {
                        storeImage(image);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }};

                Thread descaleimageandsaveitThread = new Thread(descaleimageandsaveitRunnable);
                descaleimageandsaveitThread.start();
            }
        }
        catch (IOException e) {
            // Log the exception
            e.printStackTrace();
        }
    }


    private Uri filePath;
    private FirebaseAuth mAuth;
    private void uploadImage() {
        loadingscreen.setVisibility(VISIBLE);
        Runnable uploadimagerunnable=new Runnable() {@Override public void run() {
            try {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser!=null){
                    final String uid = currentUser.getUid();
                    if (filePath != null) {
                        StorageReference ref = storageReference.child("images/" + submissionpurename);

                        ref.putFile(filePath)
                                .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference userRef = database.getReference("users").child(uid).child("mysubmissions").child("readingcomp");
                                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Object olderimageoncloudobject = dataSnapshot.child("imageoncloud").getValue();
                                                        userRef.child("imageoncloud").setValue(submissionpurename);
                                                        userRef.child("rating").setValue("0");
                                                        userRef.child("ratings").setValue("0");
                                                        userRef.child("titletext").setValue(titletext);
                                                        userRef.child("resumetext").setValue(resumetext);

                                                        // if user submitted before then remove the older image
                                                        if(olderimageoncloudobject!=null){
                                                            String olderimageoncloud = olderimageoncloudobject.toString();
                                                            StorageReference olderimagereference = storageReference.child("images/" + olderimageoncloud);
                                                            olderimagereference.delete();
                                                        }

                                                        Message message = new Message();
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("SUBJECT", "SUCCESS");
                                                        message.setData(bundle);
                                                        uploadstatusdisplayer.sendMessage(message);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("SUBJECT", "SUCCESS");
                                        bundle.putString("DATA", e.toString());
                                        message.setData(bundle);
                                        uploadstatusdisplayer.sendMessage(message);
                                    }
                                })
                                .addOnProgressListener(
                                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                                            // Progress Listener for loading
                                            // percentage on the dialog box
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress
                                                        = (100.0
                                                        * taskSnapshot.getBytesTransferred()
                                                        / taskSnapshot.getTotalByteCount());

                                                Message message = new Message();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("SUBJECT", "PROGRESS");
                                                bundle.putString("DATA", String.valueOf((int) progress));
                                                message.setData(bundle);
                                                uploadstatusdisplayer.sendMessage(message);
                                            }
                                        });
                    }
                }
                else {
                    //print("Not logged on, wait a bit then press upload again");
                    login_reload_firebase_login();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        } };
        Thread uploadimagethread = new Thread(uploadimagerunnable);
        uploadimagethread.start();

    }

    private void login_reload_firebase_login() {
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.d("FirebaseAuth", "signInAnonymously:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful())
                            {
                                Log.w("FirebaseAuth", "signInAnonymously", task.getException());
                            } else {
                                //print("Successfully logged in!");
                                //print("Submitting..");
                                if(allow_upload){
                                    allow_upload = false;
                                    uploadImage();
                                } else {
                                    print(getResources().getString(R.string.choooosenew));
                                }
                            }
                        }
                    });
        }
    }

    private Handler uploadstatusdisplayer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.getData()!=null) {
                String subject = msg.getData().getString("SUBJECT");
                if (subject != null) {
                    switch (subject) {
                        case "ERROR":
                            //print("Failure: " + msg.getData().getString("DATA"));
                            noworkgoingon = true;
                            loadingscreen.setVisibility(GONE);
                            break;
                        case "PROGRESS":
                            percentage.setText(msg.getData().getString("DATA") + getResources().getString(R.string.percent));
                            break;
                        case "SUCCESS":
                            percentage.setText(getString(R.string.hunna) + getResources().getString(R.string.percent));
                            noworkgoingon = true;
                            loadingscreen.setVisibility(GONE);
                            print(getResources().getString(R.string.donee));
                            SQLSharing.mydb = SQL.getInstance(getApplicationContext());
                            SQLSharing.mydb.updateData(idd, submissionpurename);
                            close_sql();
                            exit();
                            break;
                    }
                }
            }
            return true; }
    });

    private boolean ready_to_upload = false;
    private void storeImage(Bitmap image) throws IOException {
        boolean success = prepare_link();
        if (success) {
            OutputStream fOut = new FileOutputStream(mysubmission);

            image.compress(Bitmap.CompressFormat.JPEG, 50, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getContentResolver(),mysubmission.getAbsolutePath(),mysubmission.getName(),mysubmission.getName());
            while(!mysubmission.exists()){}
            step3 = false;// TODO revert
            step2 = true;
            step1 = false;
            ready_to_upload = true;
            printer.sendEmptyMessage(0);
        } else {
            printer2.sendEmptyMessage(0);
        }
    }


    private void backtomain() {
        Intent openDrawingsMain = new Intent(this, ReadingMain.class);
        startActivity(openDrawingsMain);
        finish();
    }


    private void exit() {
        if(getIntent()!=null){
            String sender = getIntent().getStringExtra("SENDER");
            if(sender!=null){
                if(sender.equals("MAIN")){
                    backtomain();
                } else if(sender.equals("VIEW")){
                    Intent onviewsomebodys = new Intent(this, ViewSomebodysSubmission2.class);
                    Bundle bundle = getIntent().getExtras();
                    if(bundle!=null){
                        String SUBMITTERSNAME = bundle.getString("SUBMITTERSNAME");
                        String SUBMITTERSUID = bundle.getString("SUBMITTERSUID");
                        String IMAGEONCLOUD = bundle.getString("IMAGEONCLOUD");
                        String BOOKNAME = bundle.getString("booktitle");
                        String RESUME = bundle.getString("resume");
                        int currentposition = 0;
                        if(bundle.getString("position")!=null)
                            currentposition = Integer.parseInt(bundle.getString("position"));

                        int RATING = bundle.getInt("RATING");

                        int RATINGS = bundle.getInt("RATINGS");
                        int MYVOTEONHISPOST = bundle.getInt("MYVOTEONHISPOST");
                        bundle.putString("position", String.valueOf(currentposition));
                        bundle.putString("SUBMITTERSNAME", SUBMITTERSNAME);
                        bundle.putString("SUBMITTERSUID", SUBMITTERSUID);
                        bundle.putString("IMAGEONCLOUD", IMAGEONCLOUD);
                        bundle.putInt("RATING", RATING);
                        bundle.putInt("RATINGS", RATINGS);
                        bundle.putInt("MYVOTEONHISPOST", MYVOTEONHISPOST);
                        bundle.putString("booktitle", BOOKNAME);
                        bundle.putString("resume", RESUME);

                        onviewsomebodys.putExtras(bundle);
                        startActivity(onviewsomebodys);
                        finish();
                    }
                } else {
                    backtomain();
                }
            }
        } else {
            backtomain();
        }
    }

    private boolean noworkgoingon = true;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(noworkgoingon){
            exit();
        }
    }

    private boolean uploadplease = false;
    private String resumetext, titletext;
    public void uploadImageClicked(View view) {
        if(step1){
            titletext = booktitle.getText().toString();
            if(!titletext.equals("")) {
                step1 = false;
                step2 = true;
                /*btnCamera.setVisibility(VISIBLE);*/
                booktitle.setVisibility(GONE);
                resume.setVisibility(VISIBLE);
                btnUpload.setText(getResources().getString(R.string.upload));
            } else print(getResources().getString(R.string.pleasewritatitle));
        } else if(step2) { // upload without image
            titletext = booktitle.getText().toString();
            resumetext = resume.getText().toString();
            if(!resumetext.equals("") && !titletext.equals("")) {
                btnUpload.setText(getResources().getString(R.string.upload));
                step2 = true;
                step1 = false;
                loadingscreen.setVisibility(VISIBLE);
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser!=null) {
                    final String uid = currentUser.getUid();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference("users").child(uid).child("mysubmissions").child("readingcomp");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userRef.child("titletext").setValue(titletext);
                            userRef.child("resumetext").setValue(resumetext);
                            userRef.child("rating").setValue("0");
                            userRef.child("ratings").setValue("0");


                            noworkgoingon = true;
                            loadingscreen.setVisibility(GONE);
                            print(getResources().getString(R.string.donee));
                            SQLSharing.mydb = SQL.getInstance(getApplicationContext());
                            SQLSharing.mydb.updateData(idd, "");
                            SQLSharing.mydb.updateData(idd2, titletext);
                            SQLSharing.mydb.updateData(idd3, resumetext);
                            close_sql();
                            exit();

                            exit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else print(getResources().getString(R.string.doyouhaveinternet));
            } else print(getResources().getString(R.string.pleasewriteresume));
        } else if(step3){
            titletext = booktitle.getText().toString();
            resumetext = resume.getText().toString();
            if(!resumetext.equals("") && !titletext.equals("")) {
                btnUpload.setText(getResources().getString(R.string.upload));
                if(ready_to_upload) {
                    if(allow_upload){
                        allow_upload = false;
                        noworkgoingon = false;
                        uploadImage();
                    } else {
                        print(getResources().getString(R.string.already));
                    }
                } else {
                    if(newone){
                        print(getResources().getString(R.string.choosefirst));
                    }
                    allow_upload = false;
                    uploadplease = true;
                }
            } else print(getResources().getString(R.string.pleasewritatitle));
        }
    }

    private Handler printer = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(uploadplease){
                uploadplease = false;
                uploadImage();
            }
            //print("Image Ready to be uploaded!!");
            return true; }
    });

    private Handler printer2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            print(getResources().getString(R.string.gggg));
            return true; }
    });
}
