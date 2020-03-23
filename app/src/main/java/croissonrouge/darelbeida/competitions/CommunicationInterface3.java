package croissonrouge.darelbeida.competitions;

import java.util.List;

public interface CommunicationInterface3 {

    public void removeLoadingScreen();

    public void listemptyornot(boolean empty);

    public void exit2(String submittersname, String submittersuid, String imageoncloud, int rating, int ratings, int myvoteonhispost, List<CookingSubmissionsFragment.submission> submissions, int position, String booktitle, String resume);

}
