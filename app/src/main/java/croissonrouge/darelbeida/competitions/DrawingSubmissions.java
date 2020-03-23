package croissonrouge.darelbeida.competitions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import java.util.List;
import croissonrouge.darelbeida.competitions.SQLite.SQL2;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DrawingSubmissions extends AppCompatActivity implements  CommunicationInterface {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_submissions);

        FragmentManager fm = getSupportFragmentManager();
        Fragment first = new SubmissionsFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmento, first);
        fragmentTransaction.commit();
    }

    @Override
    public void removeLoadingScreen() {
        FrameLayout loadingscreen = findViewById(R.id.loadingscreen);
        loadingscreen.setVisibility(GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this, DrawingsMain.class);
        startActivity(back);
        finish();
    }

    @Override
    public void exit(String SUBMITTERSNAME, String SUBMITTERSUID, String IMAGEONCLOUD, int RATING, int RATINGS, int MYVOTEONHISPOST, List<SubmissionsFragment.submission> submissions, int position) {

        Intent opensubmission = new Intent(this, ViewSomeonesSubmission.class);

        SQLSharing.mydb2 = SQL2.getInstance(getApplicationContext());
        SQLSharing.mydb2.delete(this);
        SQLSharing.mydb2 = SQL2.getInstance(getApplicationContext());
        for(int i=0; i<submissions.size(); i++){
            SQLSharing.mydb2.insertData(submissions.get(i).SUBMITTERSNAME, submissions.get(i).SUBMITTERSUID, String.valueOf(submissions.get(i).MYVOTEONHISPOST), String.valueOf(submissions.get(i).RATING), String.valueOf(submissions.get(i).RATINGS), submissions.get(i).IMAGEONCLOUD);
        }
        if(SQLSharing.mycursor2!=null)
            SQLSharing.mycursor2.close();
        if(SQLSharing.mydb2!=null)
            SQLSharing.mydb2.close();

        Bundle bundle = new Bundle();
        bundle.putString("position", String.valueOf(position));
        bundle.putString("SUBMITTERSNAME", SUBMITTERSNAME);
        bundle.putString("SUBMITTERSUID", SUBMITTERSUID);
        bundle.putString("IMAGEONCLOUD", IMAGEONCLOUD);
        bundle.putInt("RATING", RATING);
        bundle.putInt("RATINGS", RATINGS);
        bundle.putInt("MYVOTEONHISPOST", MYVOTEONHISPOST);

        opensubmission.putExtras(bundle);

        try {
            startActivity(opensubmission);
            finish();
        } catch(Exception e){
            Log.i("HH", "bruh " + e);
        }
    }

    @Override
    public void listemptyornot(boolean empty) {
        FrameLayout emptyflag = findViewById(R.id.emptyflag);
        if(empty){
            emptyflag.setVisibility(VISIBLE);
        } else {
            emptyflag.setVisibility(GONE);
        }
    }

    @Override
    public void exit2(String submittersname, String submittersuid, String imageoncloud, int rating, int ratings, int myvoteonhispost, List<ReadingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume) {

    }


    public void checksubmissionsClicked(View view) {
        Intent submit = new Intent(this, Submit.class);
        submit.putExtra("SENDER", "MAIN");
        startActivity(submit);
        finish();
    }
}
