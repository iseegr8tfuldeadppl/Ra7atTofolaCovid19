package croissonrouge.darelbeida.competitions;

import java.util.List;

public interface CommunicationInterface {

    public void removeLoadingScreen();

    public void exit(String SUBMITTERSNAME, String SUBMITTERSUID, String IMAGEONCLOUD, int RATING, int RATINGS, int MYVOTEONHISPOST, List<SubmissionsFragment.submission> submissions, int position);

    public void listemptyornot(boolean empty);

    public void exit2(String submittersname, String submittersuid, String imageoncloud, int rating, int ratings, int myvoteonhispost, List<ReadingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume);
}