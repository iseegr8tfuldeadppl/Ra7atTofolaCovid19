package croissonrouge.darelbeida.competitions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


@SuppressLint("ValidFragment")
public class ResumeFragment extends Fragment {

    private CommunicationInterface2 callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resumefragmentooo, container, false);
        String resume = callback.gimmidatresume();
        TextView resumee = view.findViewById(R.id.resumee);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Tajawal-Regular.ttf");

        resumee.setTypeface(font);
        resumee.setText(getContext().getResources().getString(R.string.nasss) + resume);
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback= (CommunicationInterface2) context;
    }


}