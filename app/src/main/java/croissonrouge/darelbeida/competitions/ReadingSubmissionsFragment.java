package croissonrouge.darelbeida.competitions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ReadingSubmissionsFragment extends Fragment {

    private CommunicationInterface callback;

    private List<submission> Submissions;
    private RecyclerView lister;
    private ReadingSubmissionsAdapter submissionsAdapter;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.submissions_fragment, container, false);
        Submissions = new ArrayList<>();
        get_on_firebase();
        return view;
    }


    private void get_on_firebase() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            uid = mAuth.getCurrentUser().getUid();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Submissions = new ArrayList<>();
                    load_submissions_from_firebase(dataSnapshot);
                    fill_recyclerview();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void load_submissions_from_firebase(@NonNull DataSnapshot dataSnapshot) {
        for(DataSnapshot CHILDER: dataSnapshot.getChildren()){
            final Object titletext = CHILDER.child("mysubmissions").child("readingcomp").child("titletext").getValue();
            final Object resumetext = CHILDER.child("mysubmissions").child("readingcomp").child("resumetext").getValue();
            final Object rating = CHILDER.child("mysubmissions").child("readingcomp").child("rating").getValue();
            if(CHILDER.child("mysubmissions").child("readingcomp").child("rating").exists()){
                final Object submittersid = CHILDER.getKey();
                final Object ratings = CHILDER.child("mysubmissions").child("readingcomp").child("ratings").getValue();
                final Object name = CHILDER.child("name").getValue();
                final Object imageoncloud = CHILDER.child("mysubmissions").child("readingcomp").child("imageoncloud").getValue();
                //Object description = achild.child("description").getValue();
                if(rating!=null && name!=null && submittersid!=null && ratings!=null && resumetext!=null && titletext!=null) {
                    String myvoteonhispostman = "-1";
                    if (submittersid.toString().equals(uid)) {
                        for (DataSnapshot avote : CHILDER.child("myvotes").getChildren()) {
                            Object asubmitter = avote.getKey();
                            if (asubmitter != null) {
                                if (asubmitter.toString() == submittersid) {
                                    Object myvoteonhispost = avote.getValue();
                                    if (myvoteonhispost != null) {
                                        myvoteonhispostman = myvoteonhispost.toString();
                                    }
                                }
                            }
                        }
                    }

                    final String finalMyvoteonhispostman = myvoteonhispostman;
                    String gg = "";
                    if(imageoncloud!=null){
                        gg = imageoncloud.toString();
                    }
                    final String finalGg = gg;
                    Submissions.add(new submission() {{
                        MYVOTEONHISPOST = Integer.parseInt(finalMyvoteonhispostman);
                        RATINGS = Integer.parseInt(ratings.toString());
                        RATING = Integer.parseInt(rating.toString());
                        IMAGEONCLOUD = finalGg;
                        SUBMITTERSNAME = name.toString();
                        SUBMITTERSUID = submittersid.toString();
                        RESUME = resumetext.toString();
                        BOOKTITLE = titletext.toString();
                    }});
                }
            }
        }

    }
    public static class submission{
        public int RATING; // 1 to 5
        public String IMAGEONCLOUD;
        public String SUBMITTERSNAME;
        public String SUBMITTERSUID; // most likely their firebase id?
        public int MYVOTEONHISPOST; // most likely their firebase id?
        public int RATINGS; // most likely their firebase id?
        public String BOOKTITLE; // most likely their firebase id?
        public String RESUME; // most likely their firebase id?
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback= (CommunicationInterface) context;
    }

    private void fill_recyclerview() {
        if(getView()!=null) {
            callback.listemptyornot(Submissions.size()==0);

            lister = getView().findViewById(R.id.lister);

            Submissions = reorderlist();

            submissionsAdapter = new ReadingSubmissionsAdapter(getContext(), Submissions);
            lister.setAdapter(submissionsAdapter);
            lister.setLayoutManager(new MyLinearLayoutManager(getContext(), 1, false));
            callback.removeLoadingScreen();
        }
    }

    private List<submission> reorderlist() {
        List<submission> temp = new ArrayList<>();
        while(Submissions.size()>0){
            int rating = Submissions.get(0).RATING;
            int largest = 0;
            for(int i=1; i<Submissions.size(); i++){
                if(Submissions.get(i).RATING>=rating){
                    rating = Submissions.get(i).RATING;
                    largest = i;
                }
            }
            temp.add(Submissions.get(largest));
            Submissions.remove(largest);
        }
        return temp;
    }
}