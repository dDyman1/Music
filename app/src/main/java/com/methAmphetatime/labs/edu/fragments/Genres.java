package com.methAmphetatime.labs.edu.fragments;

import android.net.Uri;
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
public class Genres extends Fragment {
    private ListView genresListView;

    private ArrayAdapter<String> genreArrAdapt;
    private String[] genres;
    private ArrayList<String> foundGenres;

    private EmbeddedVals ev;

    private ArrayList<AudioData> audioList;
    private boolean useWhatsApp;

    public Genres() {
        this(false);
    }


    public Genres(boolean useWhatsapp) {
        foundGenres = new ArrayList<>();
        this.useWhatsApp = useWhatsapp;
        ev = new EmbeddedVals();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View genresView = inflater.inflate(R.layout.fragment_genres, container, false);

        genresListView = genresView.findViewById(R.id.genresList);

        audioList = ev.getMusic(getContext().getContentResolver(), useWhatsApp);

        for(AudioData audioData : audioList){
            Uri uri = Uri.parse(audioData.getPath());
            String genre = ev.getEmbeddedAudioTag(uri, 2);
            if(!foundGenres.contains(genre)){
                foundGenres.add(genre);
            }
        }

        Collections.sort(foundGenres);
        genres = new String[foundGenres.size()];
        for(int i = 0; i < genres.length; i++){
            genres[i] = foundGenres.get(i);
        }

        genreArrAdapt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, genres);

        genresListView.setAdapter(genreArrAdapt);

        genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Genre Item Click Not Initialized", Toast.LENGTH_LONG).show();
            }
        });
        return genresView;
    }
}
