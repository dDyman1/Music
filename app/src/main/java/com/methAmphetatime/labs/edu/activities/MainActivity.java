package com.methAmphetatime.labs.edu.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.methAmphetatime.labs.edu.R;
import com.methAmphetatime.labs.edu.classes.AudioData;
import com.methAmphetatime.labs.edu.classes.EmbeddedVals;
import com.methAmphetatime.labs.edu.classes.PagerAdapter;
import com.methAmphetatime.labs.edu.classes.Utils;
import com.methAmphetatime.labs.edu.fragments.Artists;
import com.methAmphetatime.labs.edu.fragments.Genres;
import com.methAmphetatime.labs.edu.fragments.Songs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    EmbeddedVals ev;

    Toolbar mediaBar;
    BottomAppBar playBar;

    FloatingActionButton fab;

    TabLayout mediaTabLayout;

    TabItem songTab;
    TabItem artistTab;
    TabItem genreTab;

    ViewPager musicPager;
    PagerAdapter musicPagerAdapter;

    Songs songFrag;
    Artists artistFrag;
    Genres genreFrag;

    ImageView imageView;
    TextView titleTV;
    TextView artistTV;

    public MediaPlayer mp;
    boolean playing;
    public Bundle bundle;
    ArrayList<? extends AudioData> mediaList;
    public int pos;
    int elapsed;

    private boolean useDarkMode, useWhatsApp;

    @Override protected void onSaveInstanceState (@NonNull Bundle outState)
    {
        super.onSaveInstanceState (outState);
       // outState.putString (TipCalculator.getJSONStringFromObject (mCurrentTipCalculator));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ev = new EmbeddedVals();
        setDefaultValuesForPreferences ();
        restorePreferences ();
        setupDarkMode ();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaBar = findViewById(R.id.toolbar);
        setSupportActionBar(mediaBar);
        getSupportActionBar().setTitle("Music");

        playBar = findViewById(R.id.playBar);
        fab = findViewById(R.id.mediaFab);

        imageView = findViewById(R.id.bottomBarImg);
        titleTV = findViewById(R.id.bottomBarTitle);
        artistTV = findViewById(R.id.bottomBarArtist);

        mediaTabLayout = findViewById(R.id.musictabLayout);
        songTab = findViewById(R.id.songs);
        artistTab = findViewById(R.id.artists);
        genreTab = findViewById(R.id.genres);
        musicPager = findViewById(R.id.mediaPager);

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                songFrag = new Songs(useWhatsApp);
                artistFrag = new Artists(useWhatsApp);
                genreFrag = new Genres(useWhatsApp);

                musicPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 1);
                musicPagerAdapter.addFragment(songFrag);
                musicPagerAdapter.addFragment(artistFrag);
                musicPagerAdapter.addFragment(genreFrag);
                musicPager.setAdapter(musicPagerAdapter);
                musicPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mediaTabLayout));
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getApplicationContext(), "Will not function without storage access permissions", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                //ask on each startup till allowed
                permissionToken.continuePermissionRequest();
            }
        }).check();


        mediaTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                musicPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                musicPager.removeView(tab.view);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                musicPager.setCurrentItem(tab.getPosition());
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp == null || !mp.isPlaying()){
                    Toast.makeText(getApplicationContext(), "Song shuffle play started", Toast.LENGTH_SHORT).show();
                    mediaList = new ArrayList<>(songFrag.getFoundSongs());
                    Collections.shuffle(mediaList);
                    mediaInitialize(0, false);
                    titleTV.setText(mediaList.get(pos).getName() + "\n"+ mediaList.get(pos).getArtist());
                    imageView.setImageBitmap(ev.getEmbeddedImage(Uri.parse(mediaList.get(pos).getPath())));

                }
                if (mp.isPlaying()) {
                    mp.pause();
                    fab.setImageResource(R.drawable.play_button);
                } else {
                    mp.start();
                    fab.setImageResource(R.drawable.pause_button);
                }
            }
        });
        playBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp == null) {
                    Toast.makeText(getApplicationContext(), "No song selected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent temp = setIntentFromMain(false);
                    mp.release();
                    startActivityForResult(temp, 422);
                }
            }
        });
    }

    private void setupDarkMode() {

        if(useDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    public void mediaInitialize(int position, boolean withBundle) {
        if (withBundle) {
            mediaList = new ArrayList<AudioData>((ArrayList<AudioData>) Objects.requireNonNull(bundle.getSerializable("SongList")));
            elapsed = bundle.getInt("Elapsed");
            playing = bundle.getBoolean("Playing");
        }
        Uri songData = Uri.parse(mediaList.get(position).getPath());
        mp = MediaPlayer.create(this, songData);
        if (withBundle) {
            mp.seekTo(elapsed);
        }

        if (playing) {
            fab.setImageResource(R.drawable.pause_button);
            mp.start();
        } else {
            fab.setImageResource(R.drawable.play_button);
        }

        //setOnCompletedListener
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlay) {
                if (pos < (mediaList.size() - 1)) {
                    pos++;
                } else {
                    pos = 0;
                }
                mediaInitialize(pos, false);
                titleTV.setText(mediaList.get(pos).getName() + "\n"+ mediaList.get(pos).getArtist());
                imageView.setImageBitmap(ev.getEmbeddedImage(Uri.parse(mediaList.get(pos).getPath())));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println(requestCode);
        if (requestCode == 422 && resultCode == RESULT_OK) {
            bundle = data.getExtras();
            pos = bundle.getInt("Position");
            mediaInitialize(pos, true);
            titleTV.setText(mediaList.get(pos).getName() + "\n"+ mediaList.get(pos).getArtist());
            imageView.setImageBitmap(ev.getEmbeddedImage(Uri.parse(mediaList.get(pos).getPath())));
        }
        if (requestCode == 699) {
            restorePreferences();
            setupDarkMode();
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settingsKey){
            showSettings();
            return true;
        }
        else if(id == R.id.shuffleKey){
            Intent temp = setIntentFromMain(true);
            if(mp != null) {
                mp.release();
            }
            startActivityForResult(temp, 422);
            return true;
        }
        else{
            if(id == R.id.aboutData){
                showAbout(item);
            }
        }
        return super.onOptionsItemSelected (item);
    }

    private Intent setIntentFromMain(boolean shuffled) {
        Intent intent = new Intent(this, PlayerActivity.class);
        if(mediaList == null){
            mediaList = new ArrayList<>(songFrag.getFoundSongs());
        }
        if(shuffled){
            Collections.shuffle(mediaList);
            intent.putExtra("SongFileList", mediaList);
        }
        else {
            intent.putExtra("SongFileList", mediaList);
            intent.putExtra("Position", pos);
            intent.putExtra("Elapsed", mp.getCurrentPosition());
            intent.putExtra("Playing", mp.isPlaying());
        }
        return intent;
    }

    private void showSettings ()
    {
        // Here, we open up our settings activity
        Intent intent = new Intent (getApplicationContext (), SettingsActivity.class);
        startActivityForResult (intent, 699);
    }

    private void setDefaultValuesForPreferences ()
    {
        PreferenceManager.setDefaultValues (this, R.xml.root_preferences, false);
    }

    private void restorePreferences ()
    {
        String currentKey;
        String currentDefaultValue;

        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences (getApplicationContext ());

        // Use Night Mode Preference
        currentKey = getString (R.string.darkModeKey);
        useDarkMode = defaultSharedPreferences.getBoolean (currentKey, false);

        // Use WhatsApp Preference
        currentKey = getString (R.string.useWhatsAppKey);
        useWhatsApp = defaultSharedPreferences.getBoolean (currentKey, false);

    }

    public void showAbout(MenuItem item) {
        Utils.showInfoDialog(MainActivity.this, R.string.about_dialog_title,
                R.string.about_dialog_banner);
    }

    @Override protected void onStart ()
    {
        super.onStart ();
        restorePreferences ();
        applyPreferences ();
    }

    private void applyPreferences ()
    {
        setupDarkMode();
    }

}
