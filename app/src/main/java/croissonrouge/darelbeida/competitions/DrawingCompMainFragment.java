package croissonrouge.darelbeida.competitions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.storage.StorageReference;

import java.io.File;

import croissonrouge.darelbeida.competitions.SQLite.SQL;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;

@SuppressLint("ValidFragment")
public class DrawingCompMainFragment extends Fragment {

    private CommunicationInterface callback;
    private ImageView display1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawingmainfragment, container, false);
        variables(view);
        fonts();
        if(getContext()!=null){
            if(imageexistsinstorage())
               /* if(wehavecorrectversionfromfirebase()){
                    close_sql();*/
                    load_from_storage_and_display(getContext());
                /*} else{
                    close_sql();
                    download_image(getContext());
                }*/
            else {
                download_image(getContext());
            }
        }
        return view;
    }
/*

    private boolean failed = false;
    private boolean wehavecorrectversionfromfirebase() {
        sql();
        SQLSharing.mycursor.moveToPosition(6);
        if(SQLSharing.mycursor.getString(1).equals("")){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                final String uid = mAuth.getCurrentUser().getUid();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = database.getReference("users").child(uid).child("drawingmaindisplayversion");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object version = dataSnapshot.getValue();
                        if(version!=null){
                            String versionned = version.toString();
                            if(SQLSharing.mycursor.getString(1).equals(versionned)){
                                failed = false;
                            } else {
                                SQLSharing.mydb.updateData(SQLSharing.mycursor.getString(0), versionned);
                                failed = true;
                            }
                        } else {
                            userRef.setValue("0");
                            failed=  true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        userRef.setValue("0");
                        failed = true;
                    }
                });
            }
        }

        return failed;
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }

    private void sql() {
        SQLSharing.mydb = SQL.getInstance(getContext());
        SQLSharing.mycursor = SQLSharing.mydb.getData();
    }
*/

    private void download_image(final Context context) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagereference = storage.getReference().child("images").child("challenge.jpg");
        boolean success = doesnotexist();
        if(success){
            imagereference.getFile(mysubmission).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    load_from_storage_and_display(context);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.i("HH", "failed to download image " + e);
                }
            });

        }


    }

    private TextView explanation, title;
    private void variables(View view) {

        display1 = view.findViewById(R.id.display1);
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
    }
    private void fonts() {
        if(getContext()!=null) {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Tajawal-Regular.ttf");

            title.setTypeface(font);
            explanation.setTypeface(font);
        }
    }


    private boolean imageexistsinstorage() {
        // Assume block needs to be inside a Try/Catch block.
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";

        String submissionpath = mysubmissionsfolderpath + "/" + "challenge.jpg";
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        return mysubmission.exists();
    }

    private boolean doesnotexist() {
        // Assume block needs to be inside a Try/Catch block.
        String mysubmissionsfolderpath = Environment.getExternalStorageDirectory() + "/Android/data/croissonrouge.darelbeida.competitions/.MesSubmissions";

        String submissionpath = mysubmissionsfolderpath + "/" + "challenge.jpg";
        File mySubmissionsFolder = new File(mysubmissionsfolderpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        mysubmission = new File(submissionpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        boolean success = true;
        if (!mySubmissionsFolder.exists()) {
            success = mySubmissionsFolder.mkdirs();
        }
        return success;
    }

    private Bitmap image;
    private File mysubmission;
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
            callback.removeLoadingScreen();
            display1.setImageBitmap(image);
            image = null; // TODO if causes issues remove it
            return true; }
    });




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback= (CommunicationInterface) context;
    }
}