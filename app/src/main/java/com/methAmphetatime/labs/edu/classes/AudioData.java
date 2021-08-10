package com.methAmphetatime.labs.edu.classes;

import java.io.Serializable;
import java.util.Comparator;

public class AudioData implements Serializable {

    private String name;
    private String artist;
    private String path;

    public AudioData(String name, String artist, String path) {
        this.name = name;
        this.artist = artist;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }
    public String getPath() {
        return path;
    }


    /*Comparator for sorting the list by Student Name*/
    public static Comparator<AudioData> TitleComp = new Comparator<AudioData>() {

        public int compare(AudioData n1, AudioData n2) {
            String name1 = n1.getName().toUpperCase();
            String name2 = n2.getName().toUpperCase();

            return name1.compareTo(name2);
        }
    };

    public static Comparator<AudioData> ArtistComp = new Comparator<AudioData>() {
        @Override
        public int compare(AudioData o1, AudioData o2) {
            String artist1 = o1.getName().toUpperCase();
            String artist2 = o2.getName().toUpperCase();

            return artist1.compareTo(artist2);
        }
    };


}
