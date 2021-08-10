package com.methAmphetatime.labs.edu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.methAmphetatime.labs.edu.classes.AudioData;
import com.methAmphetatime.labs.edu.classes.EmbeddedVals;
import com.methAmphetatime.labs.edu.activities.MainActivity;
import com.methAmphetatime.labs.edu.activities.PlayerActivity;
import com.methAmphetatime.labs.edu.R;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Songs extends Fragment {
    private ListView songsListView;

    private ArrayAdapter<String> songArrAdapt;
    private String[] songs;
    private ArrayList<AudioData> foundSongs;

    private EmbeddedVals embeddedVals;
    private boolean useWhatsApp;

    public Songs() {
        this(false);
    }

    public Songs(boolean useWhatsApp) {
        // Required empty public constructor
        this.useWhatsApp = useWhatsApp;
        embeddedVals = new EmbeddedVals();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View songsView = inflater.inflate(R.layout.fragment_songs, container, false);

        songsListView = songsView.findViewById(R.id.songsList);
        //display songs
        foundSongs = embeddedVals.getMusic(getContext().getContentResolver(), useWhatsApp);
        Collections.sort(foundSongs, AudioData.TitleComp);

        songs = new String[foundSongs.size()];

        for (int i = 0; i < foundSongs.size(); i++) {
            songs[i] = (foundSongs.get(i).getName() + "\n" + foundSongs.get(i).getArtist());
        }
        songArrAdapt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, songs);
        //after songlist change ---  songArrAdapt.notifyDataSetChanged();
        songsListView.setAdapter(songArrAdapt);

        //song click
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent audioPlay = new Intent(getActivity(), PlayerActivity.class);
                audioPlay.putExtra("SongFileList", foundSongs);
                audioPlay.putExtra("position", position);
                // audioPlay.putExtra("TrackPos", -1);
                if (((MainActivity) getActivity()).mp != null) {
                    ((MainActivity) getActivity()).mp.release();
                }
                startActivityForResult(audioPlay, 420);
            }
        });


        return songsView;
    }

    public ArrayList<AudioData> getFoundSongs() {
        return foundSongs;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 420 && resultCode == RESULT_OK) {
            ((MainActivity) getActivity()).bundle = data.getExtras();
            ((MainActivity) getActivity()).pos =
                    ((MainActivity) getActivity()).bundle.getInt("Position");
            ((MainActivity) getActivity()).mediaInitialize(((MainActivity) getActivity()).pos, true);

        }
    }
}
