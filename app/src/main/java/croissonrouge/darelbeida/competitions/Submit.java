package croissonrouge.darelbeida.competitions;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import com.bumptech.glide.Glide;
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


public class Submit extends AppCompatActivity {

    private ImageView btnSelect, btnCamera;
    private Button btnUpload;
    private TextView title, percentage;
    private FrameLayout loadingscreen;

    // view for image view
    private ImageView imageView;

    private Bitmap bitmap;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        try{
            // TODO: lower quality of images before uploading them
            variables();
            fonts();
            images();
            firebase();
            sql();
            SQLSharing.mycursor.moveToFirst();
            SQLSharing.mycursor.moveToNext();
            idd = SQLSharing.mycursor.getString(0);
            String imagename = SQLSharing.mycursor.getString(1);
            if(!imagename.equals("")){
                if(pathtoimage(imagename)){
                    btnSelect.setVisibility(GONE);
                    /*btnCamera.setVisibility(GONE);*/
                    redarrow.setVisibility(VISIBLE);
                    loadingscreen2.setVisibility(VISIBLE);
                    load_from_storage_and_display(this);
                }
            }
            close_sql();
        } catch(Exception e){
            Log.i("HH", "bruh3 " + e);
        }
    }
    private String idd;

    private Handler displayfromstorage = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            loadingscreen2.setVisibility(GONE);
            imageView.setImageBitmap(image);
            image = null; // TODO if causes issues remove it
            return true; }
    });


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

    private void images() {
        try {
            Glide.with(this).load(R.drawable.galerie).into(btnSelect);
        } catch (Exception ignored) {
            btnSelect.setImageDrawable(getResources().getDrawable(R.drawable.galerie));
        }
        /*try {
            Glide.with(this).load(R.drawable.cam).into(btnCamera);
        } catch (Exception ignored) {
            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.cam));
        }*/
    }

    private boolean noworkgoingon = true;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(noworkgoingon){
            exit();
        }
    }


    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        btnUpload.setTypeface(font);
        redarrow.setTypeface(font);
        title.setTypeface(font);
        percentage.setTypeface(font);
    }

    private void exit() {
        if(getIntent()!=null){
            String sender = getIntent().getStringExtra("SENDER");
            if(sender!=null){
                if(sender.equals("MAIN")){
                    backtomain();
                } else if(sender.equals("VIEW")){
                        Intent onviewsomebodys = new Intent(this, ViewSomeonesSubmission.class);
                        Bundle bundle = getIntent().getExtras();
                        if(bundle!=null){
                            String SUBMITTERSNAME = bundle.getString("SUBMITTERSNAME");
                            String SUBMITTERSUID = bundle.getString("SUBMITTERSUID");
                            String IMAGEONCLOUD = bundle.getString("IMAGEONCLOUD");
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

    private void backtomain() {
        Intent openDrawingsMain = new Intent(this, DrawingsMain.class);
        startActivity(openDrawingsMain);
        finish();
    }

    private void firebase() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private FrameLayout loadingscreen2;
    private void variables() {
        loadingscreen2 = findViewById(R.id.loadingscreen2);
        redarrow = findViewById(R.id.btnCancel);
        title = findViewById(R.id.title);
        percentage = findViewById(R.id.percentage);
        loadingscreen = findViewById(R.id.loadingscreen);
        imageView = findViewById(R.id.imageView);
        btnSelect = findViewById(R.id.btnSelect);
        btnUpload = findViewById(R.id.btnUpload);
        /*btnCamera = findViewById(R.id.btnCamera);*/
    }


    public void SelectImageClicked(View view) {
        newone = true;
        SelectImage();
    }

    private boolean uploadplease = false;
    public void uploadImageClicked(View view) {
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
    }

    public void cameraClicked(View view) {
       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnCamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            newone = true;
            takePicture();
        }*/
    }

    private Uri file;
    private Button redarrow;
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
            ready_to_upload = true;
            printer.sendEmptyMessage(0);
        } else {
            printer2.sendEmptyMessage(0);
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

    private boolean pathtoimage(String imagename) {
        allow_upload = false;
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";
        String submissionpath = mysubmissionsfolderpath + "/" + imagename + ".jpg";
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        return mysubmission.exists();
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

    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }
    private void downsizeBitmap() {
        Log.i("HH", "before " + BitmapCompat.getAllocationByteCount(bitmap));
        bitmap = getResizedBitmap(bitmap, 3000);
        Log.i("HH", "after " + BitmapCompat.getAllocationByteCount(bitmap));
        imageView.setImageBitmap(bitmap);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    // Select Image method
    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100 && resultCode == RESULT_OK) {
                    final Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                    imageView.setImageBitmap(image);
                    redarrow.setVisibility(VISIBLE);
                    /*btnCamera.setVisibility(View.INVISIBLE);*/
                    btnSelect.setVisibility(View.INVISIBLE);
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
                imageView.setImageBitmap(image);
                redarrow.setVisibility(VISIBLE);
                /*btnCamera.setVisibility(View.INVISIBLE);*/
                btnSelect.setVisibility(View.INVISIBLE);

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

    private int MAX_LENGTH = 16;
    public String random() {
        return RandomStringUtils.randomAlphanumeric(MAX_LENGTH);
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
                                                final DatabaseReference userRef = database.getReference("users").child(uid).child("mysubmissions").child("drawingcomp");
                                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Object olderimageoncloudobject = dataSnapshot.child("imageoncloud").getValue();
                                                        userRef.child("imageoncloud").setValue(submissionpurename);
                                                        userRef.child("rating").setValue("0");
                                                        userRef.child("ratings").setValue("0");

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

    public void cancelClicked(View view) {
        imageView.setImageBitmap(null);
        redarrow.setVisibility(GONE);
        btnSelect.setVisibility(VISIBLE);
        /*btnCamera.setVisibility(VISIBLE);*/
        allow_upload = false;
        noworkgoingon = true;
        uploadplease = false;
    }
}
