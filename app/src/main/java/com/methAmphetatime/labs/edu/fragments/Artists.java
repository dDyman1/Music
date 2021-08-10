package com.methAmphetatime.labs.edu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.methAmphetatime.labs.edu.classes.AudioData;
import com.methAmphetatime.labs.edu.classes.EmbeddedVals;
import com.methAmphetatime.labs.edu.R;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Artists extends Fragment {

    private ListView artistListView;
    private ArrayAdapter<String> artistArrAdapt;
    private String[] artists;
    private ArrayList<String> foundArtist;
    private ArrayList<AudioData> audioList;

    private EmbeddedVals ev;

    private boolean useWhatsApp;

    public Artists() {
        this(false);
    }


    public Artists(boolean useWhatsApp) {
        // Required empty public constructor
        ev = new EmbeddedVals();
        this.useWhatsApp = useWhatsApp;
        foundArtist = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View artistView = inflater.inflate(R.layout.fragment_artists, container, false);
        artistListView = artistView.findViewById(R.id.artistList);

        audioList = ev.getMusic(getContext().getContentResolver(), useWhatsApp);

        for(AudioData audioData : audioList){
            if(!foundArtist.contains(audioData.getArtist())){
                foundArtist.add(audioData.getArtist());
            }
        }
        Collections.sort(foundArtist);

        artists = new String[foundArtist.size()];

        for(int i = 0; i < artists.length; i++){
            artists[i] = foundArtist.get(i);
        }

        artistArrAdapt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, artists);

        artistListView.setAdapter(artistArrAdapt);

        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Artist Item Click Not Initialized", Toast.LENGTH_LONG).show();
            }
        });
        return artistView;

    }
}
