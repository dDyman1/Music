package com.methAmphetatime.labs.edu.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.methAmphetatime.labs.edu.R;
import com.methAmphetatime.labs.edu.classes.AudioData;
import com.methAmphetatime.labs.edu.classes.EmbeddedVals;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Bundle fileExtraData;
    ArrayList<? extends AudioData> mediaList;

    SeekBar seekBar;
    Toolbar songTitleBar;
    TextView songTitle;
    TextView artistName;
    TextView elapsed;
    TextView remaining;
    ImageView toolbarImg;
    ImageView albumArt;
    ImageButton playButton;
    ImageButton prevButton;
    ImageButton nextButton;
    MediaPlayer mediaPlayer;
    int position;
    int currTrackPos;
    boolean playing;
    boolean withExpandedBundle;
    EmbeddedVals embeddedVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        embeddedVals = new EmbeddedVals();

        //setting resources
        seekBar = findViewById(R.id.seekBar);
        songTitleBar = findViewById(R.id.songTitle);
        songTitle = findViewById(R.id.toolbar_song_title);
        artistName = findViewById(R.id.toolbar_song_artist);
        toolbarImg = findViewById(R.id.toolbar_img);
        albumArt = findViewById(R.id.albumArt);
        playButton = findViewById(R.id.play_pause);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);
        elapsed = findViewById(R.id.timeElapsed);
        remaining = findViewById(R.id.timeRemaining);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Intent fileData = getIntent();
        fileExtraData = fileData.getExtras();

        assert fileExtraData != null;
        mediaList = (ArrayList<AudioData>) fileExtraData.getSerializable("SongFileList");
        position = fileExtraData.getInt("position", 0);
        currTrackPos = fileExtraData.getInt("Elapsed", -1);
        playing = fileExtraData.getBoolean("Playing");
        withExpandedBundle = (!(currTrackPos == -1));

        playerInitialize(position, withExpandedBundle); //start player

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < (mediaList.size() - 1)) {
                    position++;
                } else {
                    position = 0;
                }
                playerInitialize(position, false);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= 0) {
                    position = (mediaList.size() - 1);
                } else {
                    position--;
                }
                playerInitialize(position, false);
            }
        });


    }

    private void playerInitialize(int pos, boolean withExpandedBundle) {

        if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
            mediaPlayer.reset();
        }

        //get path from memory
        Uri songDataURI = Uri.parse(mediaList.get(pos).getPath());

        Bitmap bmImg = embeddedVals.getEmbeddedImage(songDataURI);
        String title = embeddedVals.getEmbeddedAudioTag(songDataURI, 0);
        String artist = embeddedVals.getEmbeddedAudioTag(songDataURI, 1);

        albumArt.setImageBitmap(bmImg);
        toolbarImg.setImageBitmap(bmImg);
        songTitle.setText(title);
        artistName.setText(artist);

        //create player
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songDataURI);
        if(withExpandedBundle){
            mediaPlayer.seekTo(currTrackPos);
            if(playing){
                mediaPlayer.start();
            }
            else {
                mediaPlayer.pause();
            }
        }


        //setup seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //seekBar val
                seekBar.setMax(mediaPlayer.getDuration());
                remaining.setText(createTimerLabel(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));

                //start playing
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause_button);

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position < (mediaList.size() - 1)) {
                    position++;
                } else {
                    position = 0;
                }
                playerInitialize(position, false);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //make seekbar position change with song play
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(90);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //handler to set change
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            elapsed.setText(createTimerLabel(msg.what));
            remaining.setText(createTimerLabel(mediaPlayer.getDuration() - msg.what));
            seekBar.setProgress(msg.what);
        }
    };

    public String createTimerLabel(int length) {
        String val = "";

        int min = ((length / 1000) / 60);
        int sec = ((length / 1000) % 60);

        val = min + ":";
        if (sec < 10) {
            val += "0" + sec;
        } else {
            val += sec;
        }

        return val;
    }


    private void play() {
        if ((mediaPlayer != null) && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.play_button);
        } else {
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause_button);
        }
    }

    @Override
    public void onBackPressed() {
        // MainActivity.setMediaPlayer(mediaPlayer, mediaPlayer.isPlaying());
        ArrayList<AudioData> list = new ArrayList<>(mediaList);

        Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
        intent.putExtra("SongList", list);
        intent.putExtra("Position", position);
        intent.putExtra("Elapsed", mediaPlayer.getCurrentPosition());
        intent.putExtra("Playing", mediaPlayer.isPlaying());

        setResult(Activity.RESULT_OK, intent);

        mediaPlayer.stop();

        finish();
        //mediaPlayer.pause();
    }
}
