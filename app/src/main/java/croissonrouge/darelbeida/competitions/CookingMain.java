package croissonrouge.darelbeida.competitions;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.List;
import croissonrouge.darelbeida.competitions.SQLite.SQL;
import croissonrouge.darelbeida.competitions.SQLite.SQLSharing;

import static android.view.View.GONE;

public class CookingMain extends AppCompatActivity  implements  CommunicationInterface3 {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_main);

        variables();
        fonts();
        attach_fragment();
        haveialreadysubmitted();
    }

    private void haveialreadysubmitted() {
        sql();
        if(SQLSharing.mycursor.getCount()>=2){
            SQLSharing.mycursor.moveToFirst();
            SQLSharing.mycursor.moveToNext();
            String imagename = SQLSharing.mycursor.getString(1);
            if(!imagename.equals("")){
                enter.setText(getResources().getString(R.string.joinagain));
            }
        }
        close_sql();
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

    private void attach_fragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment first = new CookingCompMainFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentor, first);
        fragmentTransaction.commit();
    }

    private void fonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Tajawal-Regular.ttf");

        checksubmissions.setTypeface(font);
        enter.setTypeface(font);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backtomain = new Intent(this, MainActivity.class);
        startActivity(backtomain);
        finish();
    }

    private Button enter, checksubmissions;
    private void variables() {

        enter = findViewById(R.id.enter);
        checksubmissions = findViewById(R.id.checksubmissions);
    }



    public void checksubmissionsClicked(View view) {
        Intent checkSubmissions = new Intent(this, CookingSubmissions.class);
        startActivity(checkSubmissions);
        finish();
    }

    public void submitClicked(View view) {
        Intent submit = new Intent(this, Submit3.class);
        submit.putExtra("SENDER", "MAIN");
        startActivity(submit);
        finish();
    }

    @Override
    public void removeLoadingScreen() {
        FrameLayout loadingscreen = findViewById(R.id.loadingscreen);
        loadingscreen.setVisibility(GONE);
    }

    @Override
    public void listemptyornot(boolean empty) {

    }

    @Override
    public void exit2(String submittersname, String submittersuid, String imageoncloud, int rating, int ratings, int myvoteonhispost, List<CookingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume) {

    }

}
