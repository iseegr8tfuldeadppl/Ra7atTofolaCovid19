package croissonrouge.darelbeida.competitions;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import static croissonrouge.darelbeida.competitions.ReadingSubmissionsFragment.*;


public class ReadingSubmissionsAdapter extends RecyclerView.Adapter<ReadingSubmissionsAdapter.ViewHolder> {


    private List<submission> submissions;
    private LayoutInflater mInflater;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, novotesyet;
        LinearLayout ratingholder;
        LinearLayout main;
        Button rate;
        ImageView starone, startwo, starthree, starfour, starfive;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            rate = itemView.findViewById(R.id.rate);
            main = itemView.findViewById(R.id.main);
            ratingholder = itemView.findViewById(R.id.ratingholder);
            novotesyet = itemView.findViewById(R.id.novotesyet);
            starone = itemView.findViewById(R.id.starone);
            startwo = itemView.findViewById(R.id.startwo);
            starthree = itemView.findViewById(R.id.starthree);
            starfour = itemView.findViewById(R.id.starfour);
            starfive = itemView.findViewById(R.id.starfive);
        }
    }

    ReadingSubmissionsAdapter(Context context, List<submission> givensubmissions) {
        submissions = givensubmissions;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ReadingSubmissionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = mInflater.inflate(R.layout.submissionitem, parent, false);
        return new ViewHolder(contactView); }
    @Override
    public int getItemCount() { return submissions.size(); }


    @Override
    public void onBindViewHolder(@NonNull final ReadingSubmissionsAdapter.ViewHolder viewHolder, int position) {
        ReadingSubmissionsFragment.submission iteme = submissions.get(position);
        Context context = viewHolder.itemView.getContext();

        String[] ggt = iteme.SUBMITTERSNAME.split(" ");
        if(ggt.length>1){
            String gg = ggt[ggt.length-1];
            viewHolder.title.setText(gg);
        } else {
            viewHolder.title.setText(iteme.SUBMITTERSNAME);
        }

        setup_on_click_listeners(context, viewHolder, iteme, submissions, position, iteme.BOOKTITLE, iteme.RESUME);
        fonts(viewHolder, context);
        display_rating(context, context.getResources(), viewHolder, iteme.RATING);
    }

    private void fonts(ViewHolder viewHolder, Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Tajawal-Regular.ttf");

        viewHolder.title.setTypeface(font);
        viewHolder.rate.setTypeface(font);
        viewHolder.novotesyet.setTypeface(font);
    }

    private void setup_on_click_listeners(final Context context, ViewHolder viewHolder, final submission iteme, final List<submission> submissions, final int position, final String BOOKTITLE, final String RESUME) {
        viewHolder.rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.ratingholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.starfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.starfour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.starthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.startwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.starone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.novotesyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommunicationInterface callback= (CommunicationInterface) context;
                callback.exit2(iteme.SUBMITTERSNAME, iteme.SUBMITTERSUID, iteme.IMAGEONCLOUD, iteme.RATING, iteme.RATINGS, iteme.MYVOTEONHISPOST, submissions, position, BOOKTITLE, RESUME);
            }
        });
    }

    private void display_rating(Context context, Resources resources, ViewHolder viewHolder, int RATING) {
        switch(RATING){
            case 5:
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starone);
                } catch (Exception ignored) {
                    viewHolder.starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.startwo);
                } catch (Exception ignored) {
                    viewHolder.startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starthree);
                } catch (Exception ignored) {
                    viewHolder.starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starfour);
                } catch (Exception ignored) {
                    viewHolder.starfour.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starfive);
                } catch (Exception ignored) {
                    viewHolder.starfive.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                break;
            case 4:
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starone);
                } catch (Exception ignored) {
                    viewHolder.starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.startwo);
                } catch (Exception ignored) {
                    viewHolder.startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starthree);
                } catch (Exception ignored) {
                    viewHolder.starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starfour);
                } catch (Exception ignored) {
                    viewHolder.starfour.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfive);
                } catch (Exception ignored) {
                    viewHolder.starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 3:
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starone);
                } catch (Exception ignored) {
                    viewHolder.starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.startwo);
                } catch (Exception ignored) {
                    viewHolder.startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starthree);
                } catch (Exception ignored) {
                    viewHolder.starthree.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfour);
                } catch (Exception ignored) {
                    viewHolder.starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfive);
                } catch (Exception ignored) {
                    viewHolder.starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 2:
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starone);
                } catch (Exception ignored) {
                    viewHolder.starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.startwo);
                } catch (Exception ignored) {
                    viewHolder.startwo.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starthree);
                } catch (Exception ignored) {
                    viewHolder.starthree.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfour);
                } catch (Exception ignored) {
                    viewHolder.starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfive);
                } catch (Exception ignored) {
                    viewHolder.starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            case 1:
                try {
                    Glide.with(context).load(R.drawable.on).into(viewHolder.starone);
                } catch (Exception ignored) {
                    viewHolder.starone.setImageDrawable(resources.getDrawable(R.drawable.on));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.startwo);
                } catch (Exception ignored) {
                    viewHolder.startwo.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starthree);
                } catch (Exception ignored) {
                    viewHolder.starthree.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfour);
                } catch (Exception ignored) {
                    viewHolder.starfour.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                try {
                    Glide.with(context).load(R.drawable.off).into(viewHolder.starfive);
                } catch (Exception ignored) {
                    viewHolder.starfive.setImageDrawable(resources.getDrawable(R.drawable.off));
                }
                break;
            default:
                viewHolder.ratingholder.setVisibility(View.INVISIBLE);
                viewHolder.novotesyet.setVisibility(View.VISIBLE);
                break;
        }
    }


}