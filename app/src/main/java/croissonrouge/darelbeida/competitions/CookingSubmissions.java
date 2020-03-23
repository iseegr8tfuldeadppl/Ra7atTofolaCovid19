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
import croissonrouge.darelbeida.competitions.SQLite.SQL4;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CookingSubmissions extends AppCompatActivity implements  CommunicationInterface3 {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_submissions);

        FragmentManager fm = getSupportFragmentManager();
        Fragment first = new CookingSubmissionsFragment();
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
        Intent back = new Intent(this, CookingMain.class);
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
    public void exit2(String submittersname, String submittersuid, String imageoncloud, int rating, int ratings, int myvoteonhispost, List<CookingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume) {
        Intent opensubmission = new Intent(this, ViewSomeonesSubmission3.class);

        SQLSharing.mydb4 = SQL4.getInstance(getApplicationContext());
        SQLSharing.mydb4.delete(this);
        SQLSharing.mydb4 = SQL4.getInstance(getApplicationContext());
        for(int i=0; i<submissions.size(); i++){
            SQLSharing.mydb4.insertData(submissions.get(i).SUBMITTERSNAME, submissions.get(i).SUBMITTERSUID, String.valueOf(submissions.get(i).MYVOTEONHISPOST), String.valueOf(submissions.get(i).RATING), String.valueOf(submissions.get(i).RATINGS), submissions.get(i).IMAGEONCLOUD);
        }
        if(SQLSharing.mycursor4!=null)
            SQLSharing.mycursor4.close();
        if(SQLSharing.mydb4!=null)
            SQLSharing.mydb4.close();

        Bundle bundle = new Bundle();
        bundle.putString("position", String.valueOf(position));
        bundle.putString("SUBMITTERSNAME", submittersname);
        bundle.putString("SUBMITTERSUID", submittersuid);
        bundle.putString("IMAGEONCLOUD", imageoncloud);
        bundle.putInt("RATING", rating);
        bundle.putInt("RATINGS", ratings);
        bundle.putInt("MYVOTEONHISPOST", myvoteonhispost);

        opensubmission.putExtras(bundle);

        try {
            startActivity(opensubmission);
            finish();
        } catch(Exception e){
            Log.i("HH", "bruh " + e);
        }
    }


    public void checksubmissionsClicked(View view) {
        Intent submit = new Intent(this, Submit3.class);
        submit.putExtra("SENDER", "MAIN");
        startActivity(submit);
        finish();
    }
}
