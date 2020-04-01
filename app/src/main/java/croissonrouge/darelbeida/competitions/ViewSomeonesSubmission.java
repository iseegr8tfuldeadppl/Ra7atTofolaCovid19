package croissonrouge.darelbeida.competitions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import croissonrouge.darelbeida.competitions.SQLite.SQL2;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewSomeonesSubmission extends AppCompatActivity {

    private ImageView drawing;
    private String IMAGEONCLOUD, SUBMITTERSNAME, SUBMITTERSUID;
    private int RATING, RATINGS, MYVOTEONHISPOST;
    private ImageView starone, startwo, starthree, starfour, starfive;
    private ImageView starone2, startwo2, starthree2, starfour2, starfive2;
    private int currentposition;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_someones_submission);

        try{
            variables();
            fonts();
            images();
            getsubmissionsfromsql();

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                SUBMITTERSNAME = bundle.getString("SUBMITTERSNAME");
                SUBMITTERSUID = bundle.getString("SUBMITTERSUID");
                IMAGEONCLOUD = bundle.getString("IMAGEONCLOUD");
                if(bundle.getString("position")!=null)
                    currentposition = Integer.parseInt(bundle.getString("position"));

                RATING = bundle.getInt("RATING");

                RATINGS = bundle.getInt("RATINGS");
                MYVOTEONHISPOST = bundle.getInt("MYVOTEONHISPOST");
            }

            if(Submissions.size()==1){
                next.setVisibility(GONE);
                previous.setVisibility(GONE);
            } else if(Submissions.size()==currentposition+1){
                next.setVisibility(GONE);
            } else if(currentposition==0) {
                previous.setVisibility(GONE);
            }

            submittersdisplay.setText(SUBMITTERSNAME);


            display_overall_rating(this, getResources(), RATING);
            update_my_rating(this, getResources(), MYVOTEONHISPOST);

            if(imageexistsinstorage())
                load_from_storage_and_display();
            else {
                download_image();
            }
        } catch(Exception ignored){

        }

    }

    private void getsubmissionsfromsql() {
        Submissions = new ArrayList<>();
        SQLSharing.mydb2 = SQL2.getInstance(getApplicationContext());
        SQLSharing.mycursor2 = SQLSharing.mydb2.getData();
        while(SQLSharing.mycursor2.moveToNext()){
            Submissions.add(new SubmissionsFragment.submission() {{
                SUBMITTERSNAME = SQLSharing.mycursor2.getString(1);
                SUBMITTERSUID = SQLSharing.mycursor2.getString(2);
                MYVOTEONHISPOST = Integer.parseInt(SQLSharing.mycursor2.getString(3));
                RATING = Integer.parseInt(SQLSharing.mycursor2.getString(4));
                RATINGS = Integer.parseInt(SQLSharing.mycursor2.getString(5));
                IMAGEONCLOUD = SQLSharing.mycursor2.getString(6);
            }});
        }
        if(SQLSharing.mycursor2!=null)
            SQLSharing.mycursor2.close();
        if(SQLSharing.mydb2!=null)
            SQLSharing.mydb2.close();
    }

    private List<SubmissionsFragment.submission> Submissions;
    private void images() {
        try {
            Glide.with(this).load(R.drawable.previous).into(previous);
        } catch (Exception ignored) {
            previous.setImageDrawable(getResources().getDrawable(R.drawable.previous));
        }
        try {
            Glide.with(this).load(R.drawable.next).into(next);
        } catch (Exception ignored) {
            next.setImageDrawable(getResources().getDrawable(R.drawable.next));
        }
    }

    private TextView submittersdisplay, percentage, ratingsview;
    private FrameLayout loadingscreen;
    private LinearLayout submitterstarsholder;
    private ImageView previous, next;
    private void variables() {
        previous = findViewById(R.id.arrowleft);
        next = findViewById(R.id.arrowright);

        submitterstarsholder = findViewById(R.id.submitterstarsholder);
        ratingsview = findViewById(R.id.ratings);
        submittersdisplay = findViewById(R.id.submittersdisplay);
        loadingscreen = findViewById(R.id.loadingscreen);

        drawing = findViewById(R.id.drawing);
        percentage = findViewById(R.id.percentage);

        starone = findViewById(R.id.starone);
        startwo = findViewById(R.id.startwo);
        starthree = findViewById(R.id.starthree);
        starfour = findViewById(R.id.starfour);
        starfive = findViewById(R.id.starfive);

        starone2 = findViewById(R.id.starone2);
        startwo2 = findViewById(R.id.startwo2);
        starthree2 = findViewById(R.id.starthree2);
        starfour2 = findViewById(R.id.starfour2);
        starfive2 = findViewById(R.id.starfive2);
        submitmine = findViewById(R.id.submitmine);
        backtosubmissions = findViewById(R.id.backtosubmissions);
    }

    private Button submitmine, backtosubmissions;
    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        submittersdisplay.setTypeface(font);
        backtosubmissions.setTypeface(font);
        submitmine.setTypeface(font);
        percentage.setTypeface(font);
        ratingsview.setTypeface(font);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this, DrawingSubmissions.class);
        startActivity(back);
        finish();
    }

    private void load_from_storage_and_display() {
        Runnable descaleimageandsaveitRunnable=new Runnable() {@Override public void run() {
            // TODO: check image size and set descale accordingly
            try {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser()!=null) {
                    uid = mAuth.getCurrentUser().getUid();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference("users").child(uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot myvotessnapshot = dataSnapshot.child("myvotes");
                            if(myvotessnapshot.exists()) {
                                if (myvotessnapshot.getChildrenCount() != 0) {
                                    for(DataSnapshot apostivotedon: myvotessnapshot.getChildren()){
                                        Object Objectasubmitter = apostivotedon.getKey();
                                        if(Objectasubmitter!=null){
                                            String asubmitter = Objectasubmitter.toString();
                                            if(asubmitter.equals(SUBMITTERSUID)){
                                                Object Objectarating = apostivotedon.getValue();
                                                if(Objectarating!=null){
                                                    Message gg = new Message();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("MYRATING", Objectarating.toString());
                                                    gg.setData(bundle);
                                                    updatemyrating.sendMessage(gg);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                getsubmissionsfromsql();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(mysubmission));
                displayfromstorage.sendEmptyMessage(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }};
        Thread descaleimageandsaveitThread = new Thread(descaleimageandsaveitRunnable);
        descaleimageandsaveitThread.start();
    }

    private void download_image() {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagereference = storage.getReference().child("images").child(IMAGEONCLOUD);
        boolean success = doesnotexist();
        if(success){

            imagereference.getFile(mysubmission).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    load_from_storage_and_display();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.i("HH", "failed to download image " + e);
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress
                            = (100.0
                            * taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());
                    percentage.setText((int) progress + getResources().getString(R.string.percent));
                }
            });

        }


    }

    private Handler updatemyrating = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String myrating = msg.getData().getString("MYRATING");
            if(myrating!=null) {
                MYVOTEONHISPOST = Integer.parseInt(myrating);
                calculate_new_overall_rating(MYVOTEONHISPOST);
                /*if(RATINGS==0){
                    print("Be The First To Rate This Post!");
                }*/


                update_my_rating(getApplicationContext(), getResources(), Integer.parseInt(myrating));
                display_overall_rating(getApplicationContext(), getResources(), RATING);
            }
            return true; }
    });

    private void update_my_rating(Context context, Resources resources, int myrating) {
        switch(myrating){
            case 5:
                try {
                    Glide.with(context).load(R.drawable.on).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                break;
            case 4:
                try {
                    Glide.with(context).load(R.drawable.on).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 3:
                try {
                    Glide.with(context).load(R.drawable.on).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 2:
                try {
                    Glide.with(context).load(R.drawable.on).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 1:
                try {
                    Glide.with(context).load(R.drawable.on).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            default:
                try {
                    Glide.with(context).load(R.drawable.off).into(starone);
                } catch (Exception ignored) {
                    starone.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(startwo);
                } catch (Exception ignored) {
                    startwo.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starthree);
                } catch (Exception ignored) {
                    starthree.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour);
                } catch (Exception ignored) {
                    starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive);
                } catch (Exception ignored) {
                    starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
        }
    }

    private Handler displayfromstorage = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            loadingscreen.setVisibility(GONE);
            drawing.setImageBitmap(image);
            image = null; // TODO if causes issues remove it
            return true; }
    });
    private File mysubmission;

    private boolean doesnotexist() {
        // Assume block needs to be inside a Try/Catch block.
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";

        String submissionpath = mysubmissionsfolderpath + "/" + IMAGEONCLOUD + ".jpg";
        File mySubmissionsFolder = new File(mysubmissionsfolderpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        boolean success = true;
        if (!mySubmissionsFolder.exists()) {
            success = mySubmissionsFolder.mkdirs();
        }
        return success;
    }

    private boolean imageexistsinstorage() {
        // Assume block needs to be inside a Try/Catch block.
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";

        String submissionpath = mysubmissionsfolderpath + "/" + IMAGEONCLOUD + ".jpg";
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        return mysubmission.exists();
    }


    public void checksubmissionsClicked(View view) {
        Intent checkSubmissions = new Intent(this, DrawingSubmissions.class);
        startActivity(checkSubmissions);
        finish();
    }

    public void submitClicked(View view) {
        Intent submit = new Intent(this, Submit.class);
        Bundle bundle = new Bundle();
        bundle.putString("position", String.valueOf(currentposition));
        bundle.putString("SUBMITTERSNAME", SUBMITTERSNAME);
        bundle.putString("SUBMITTERSUID", SUBMITTERSUID);
        bundle.putString("IMAGEONCLOUD", IMAGEONCLOUD);
        bundle.putInt("RATING", RATING);
        bundle.putInt("RATINGS", RATINGS);
        bundle.putInt("MYVOTEONHISPOST", MYVOTEONHISPOST);
        submit.putExtras(bundle);
        startActivity(submit);
        finish();
    }

    public void oneClicked(View view) {
        calculate_new_overall_rating(1);
        updatefirebase();
        update_my_rating(this, getResources(), 1);
        display_overall_rating(this, getResources(), RATING);
    }

    public void twoClicked(View view) {
        calculate_new_overall_rating(2);
        updatefirebase();
        update_my_rating(this, getResources(), 2);
        display_overall_rating(this, getResources(), RATING);
    }

    public void threeClicked(View view) {
        calculate_new_overall_rating(3);
        updatefirebase();
        update_my_rating(this, getResources(), 3);
        display_overall_rating(this, getResources(), RATING);
    }

    public void fourClicked(View view) {
        calculate_new_overall_rating(4);
        updatefirebase();
        update_my_rating(this, getResources(), 4);
        display_overall_rating(this, getResources(), RATING);
    }
    public void fiveClicked(View view) {
        calculate_new_overall_rating(5);
        updatefirebase();
        update_my_rating(this, getResources(), 5);
        display_overall_rating(this, getResources(), RATING);
    }

    String uid;
    private void updatefirebase() {
        if(uid!=null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("users").child(SUBMITTERSUID).child("mysubmissions").child("drawingcomp").child("ratings").setValue(String.valueOf(RATINGS));
            database.getReference("users").child(SUBMITTERSUID).child("mysubmissions").child("drawingcomp").child("rating").setValue(String.valueOf(RATING));
            database.getReference("users").child(uid).child("myvotes").child(SUBMITTERSUID).setValue(String.valueOf(MYVOTEONHISPOST));
        }
    }

    private void calculate_new_overall_rating(int vote) {


        if(RATINGS==0){
            MYVOTEONHISPOST = vote;
            RATING = vote;
            RATINGS = 1;
        } else {
            if(MYVOTEONHISPOST==-1) {
                float total = RATINGS * RATING;
                total += vote;

                RATINGS += 1;
                RATING = Math.round(total / RATINGS);

            } else {
                float total = RATINGS * RATING;
                total -= MYVOTEONHISPOST;
                total += vote;

                RATING = Math.round(total / RATINGS);
            }

            // TODO update this in firebase aswell
            MYVOTEONHISPOST = vote;
        }


        SQLSharing.mydb2 = SQL2.getInstance(getApplicationContext());
        SQLSharing.mycursor2 = SQLSharing.mydb2.getData();
        while(SQLSharing.mycursor2.moveToNext()){
            if(SQLSharing.mycursor2.getString(2).equals(String.valueOf(SUBMITTERSUID))){
                SQLSharing.mydb2.updateData(SQLSharing.mycursor2.getString(0),
                        SUBMITTERSNAME,
                        SUBMITTERSUID,
                        String.valueOf(MYVOTEONHISPOST),
                        String.valueOf(RATING),
                        String.valueOf(RATINGS),
                        IMAGEONCLOUD);
            }
        }

        if(SQLSharing.mycursor2!=null)
            SQLSharing.mycursor2.close();
        if(SQLSharing.mydb2!=null)
            SQLSharing.mydb2.close();

        for(int i=0; i<Submissions.size(); i++){
            if(Submissions.get(i).SUBMITTERSUID.equals(SUBMITTERSUID)){
                Submissions.get(i).RATING = RATING;
                Submissions.get(i).RATINGS = RATINGS;
                Submissions.get(i).MYVOTEONHISPOST = MYVOTEONHISPOST;

            }
        }

    }

    public void makepicturezoomableClicked(View view) {
    }

    private void display_overall_rating(Context context, Resources resources, int RATING) {
        if(RATINGS==0){
            ratingsview.setText(resources.getString(R.string.no_votes_yet));
        } else if(RATINGS==1){
            ratingsview.setText(resources.getString(R.string.onevoted));
        } else if(RATINGS==2){
            ratingsview.setText(resources.getString(R.string.twovoted));
        } else if(RATINGS<11){
            ratingsview.setText(resources.getString(R.string.votedfrom) + " " + RATINGS + " " + resources.getString(R.string.peoplevoted));
        } else {
            ratingsview.setText(resources.getString(R.string.votedfrom) + " " + RATINGS + " " + resources.getString(R.string.chakhs));
        }
        switch(RATING){
            case 5:
                submitterstarsholder.setVisibility(VISIBLE);
                try {
                    Glide.with(context).load(R.drawable.on).into(starone2);
                } catch (Exception ignored) {
                    starone2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo2);
                } catch (Exception ignored) {
                    startwo2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree2);
                } catch (Exception ignored) {
                    starthree2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfour2);
                } catch (Exception ignored) {
                    starfour2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfive2);
                } catch (Exception ignored) {
                    starfive2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                break;
            case 4:
                submitterstarsholder.setVisibility(VISIBLE);
                try {
                    Glide.with(context).load(R.drawable.on).into(starone2);
                } catch (Exception ignored) {
                    starone2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo2);
                } catch (Exception ignored) {
                    startwo2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree2);
                } catch (Exception ignored) {
                    starthree2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starfour2);
                } catch (Exception ignored) {
                    starfour2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive2);
                } catch (Exception ignored) {
                    starfive2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 3:
                submitterstarsholder.setVisibility(VISIBLE);
                try {
                    Glide.with(context).load(R.drawable.on).into(starone2);
                } catch (Exception ignored) {
                    starone2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo2);
                } catch (Exception ignored) {
                    startwo2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(starthree2);
                } catch (Exception ignored) {
                    starthree2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour2);
                } catch (Exception ignored) {
                    starfour2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive2);
                } catch (Exception ignored) {
                    starfive2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 2:
                submitterstarsholder.setVisibility(VISIBLE);
                try {
                    Glide.with(context).load(R.drawable.on).into(starone2);
                } catch (Exception ignored) {
                    starone2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(startwo2);
                } catch (Exception ignored) {
                    startwo2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starthree2);
                } catch (Exception ignored) {
                    starthree2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour2);
                } catch (Exception ignored) {
                    starfour2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive2);
                } catch (Exception ignored) {
                    starfive2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 1:
                submitterstarsholder.setVisibility(VISIBLE);
                try {
                    Glide.with(context).load(R.drawable.on).into(starone2);
                } catch (Exception ignored) {
                    starone2.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(startwo2);
                } catch (Exception ignored) {
                    startwo2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starthree2);
                } catch (Exception ignored) {
                    starthree2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfour2);
                } catch (Exception ignored) {
                    starfour2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(starfive2);
                } catch (Exception ignored) {
                    starfive2.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            default:
                submitterstarsholder.setVisibility(View.GONE);
                break;
        }
    }

    public void previousClicked(View view) {
        drawing.setImageBitmap(null);
        loadingscreen.setVisibility(VISIBLE);
        currentposition -= 1;
        SUBMITTERSNAME = Submissions.get(currentposition).SUBMITTERSNAME;
        SUBMITTERSUID =  Submissions.get(currentposition).SUBMITTERSUID;
        IMAGEONCLOUD = Submissions.get(currentposition).IMAGEONCLOUD;

        RATING = Submissions.get(currentposition).RATING;
        RATINGS = Submissions.get(currentposition).RATINGS;
        MYVOTEONHISPOST = Submissions.get(currentposition).MYVOTEONHISPOST;

        String[] ggt = SUBMITTERSNAME.split(" ");
        if(ggt.length>1){
            String gg = ggt[ggt.length-1];
            submittersdisplay.setText(gg);
        } else {
            submittersdisplay.setText(SUBMITTERSNAME);
        }

        if(currentposition==0){
            previous.setVisibility(View.GONE);
            next.setVisibility(VISIBLE);
        } else {
            previous.setVisibility(VISIBLE);
            next.setVisibility(VISIBLE);
        }

        display_overall_rating(this, getResources(), RATING);
        update_my_rating(this, getResources(), MYVOTEONHISPOST);

        if(imageexistsinstorage())
            load_from_storage_and_display();
        else {
            download_image();
        }
    }

    public void nextClicked(View view) {
        drawing.setImageBitmap(null);
        loadingscreen.setVisibility(VISIBLE);
        currentposition += 1;
        SUBMITTERSNAME = Submissions.get(currentposition).SUBMITTERSNAME;
        SUBMITTERSUID =  Submissions.get(currentposition).SUBMITTERSUID;
        IMAGEONCLOUD = Submissions.get(currentposition).IMAGEONCLOUD;

        RATING = Submissions.get(currentposition).RATING;
        RATINGS = Submissions.get(currentposition).RATINGS;
        MYVOTEONHISPOST = Submissions.get(currentposition).MYVOTEONHISPOST;

        submittersdisplay.setText(SUBMITTERSNAME);

        if(Submissions.size()==currentposition+1){
            next.setVisibility(GONE);
            previous.setVisibility(VISIBLE);
        } else {
            previous.setVisibility(VISIBLE);
            next.setVisibility(VISIBLE);
        }

        display_overall_rating(this, getResources(), RATING);
        update_my_rating(this, getResources(), MYVOTEONHISPOST);

        if(imageexistsinstorage())
            load_from_storage_and_display();
        else {
            download_image();
        }
    }
}
