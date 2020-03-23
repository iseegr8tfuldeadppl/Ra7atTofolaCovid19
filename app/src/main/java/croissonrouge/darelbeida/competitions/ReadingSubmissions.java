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
import croissonrouge.darelbeida.competitions.SQLite.SQL3;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReadingSubmissions extends AppCompatActivity implements  CommunicationInterface {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_submissions);

        FragmentManager fm = getSupportFragmentManager();
        Fragment first = new ReadingSubmissionsFragment();
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
    public void exit(String SUBMITTERSNAME, String SUBMITTERSUID, String IMAGEONCLOUD, int RATING, int RATINGS, int MYVOTEONHISPOST, List<SubmissionsFragment.submission> submissions, int position) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this, ReadingMain.class);
        startActivity(back);
        finish();
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
    public void exit2(String SUBMITTERSNAME, String SUBMITTERSUID, String IMAGEONCLOUD, int RATING, int RATINGS, int MYVOTEONHISPOST, List<ReadingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume) {
        Intent opensubmission = new Intent(this, ViewSomebodysSubmission2.class);

        SQLSharing.mydb3 = SQL3.getInstance(getApplicationContext());
        SQLSharing.mydb3.delete(this);
        SQLSharing.mydb3 = SQL3.getInstance(getApplicationContext());
        for(int i=0; i<submissions.size(); i++){
            SQLSharing.mydb3.insertData(submissions.get(i).SUBMITTERSNAME, submissions.get(i).SUBMITTERSUID, String.valueOf(submissions.get(i).MYVOTEONHISPOST), String.valueOf(submissions.get(i).RATING), String.valueOf(submissions.get(i).RATINGS), submissions.get(i).IMAGEONCLOUD, submissions.get(i).BOOKTITLE, submissions.get(i).RESUME);
        }
        if(SQLSharing.mycursor3!=null)
            SQLSharing.mycursor3.close();
        if(SQLSharing.mydb3!=null)
            SQLSharing.mydb3.close();

        Bundle bundle = new Bundle();
        bundle.putString("position", String.valueOf(position));
        bundle.putString("SUBMITTERSNAME", SUBMITTERSNAME);
        bundle.putString("SUBMITTERSUID", SUBMITTERSUID);
        bundle.putString("IMAGEONCLOUD", IMAGEONCLOUD);
        bundle.putInt("RATING", RATING);
        bundle.putInt("RATINGS", RATINGS);
        bundle.putInt("MYVOTEONHISPOST", MYVOTEONHISPOST);
        bundle.putString("booktitle", booktitle);
        bundle.putString("resume", resume);

        opensubmission.putExtras(bundle);

        try {
            startActivity(opensubmission);
            finish();
        } catch(Exception e){
            Log.i("HH", "bruh " + e);
        }
    }


    public void checksubmissionsClicked(View view) {
        Intent submit = new Intent(this, Submit.class);
        submit.putExtra("SENDER", "MAIN");
        startActivity(submit);
        finish();
    }
}
