package com.methAmphetatime.labs.edu.classes;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;



public class EmbeddedVals {

    public EmbeddedVals(){

    }

    public Bitmap getEmbeddedImage(Uri songUri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songUri.getPath());
        byte[] img = mmr.getEmbeddedPicture();
        return BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    public String getEmbeddedAudioTag(Uri songUri, int type) {
        String title = "";
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songUri.getPath());
        if (type == 0) {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        } else if (type == 1) {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }
        else if(type == 2){
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        }

        return title;
    }

    public ArrayList<AudioData> getMusic(ContentResolver contentResolver, boolean useWhatsApp) {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ArrayList<AudioData> songsList = new ArrayList<>();
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                //add settings option?
                if(!((songCursor.getString(songPath).contains("WhatsApp") && !useWhatsApp)
                        || songCursor.getString(songPath).contains("TextNow"))) {
                    songsList.add(new AudioData(songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getString(songPath)));
                }
            } while (songCursor.moveToNext());
            songCursor.close();
        }
        return songsList;
    }


}
