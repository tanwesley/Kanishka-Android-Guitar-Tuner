package com.example.guitartuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    TextView noteText;
    boolean isActive;
    Button startButton, stopButton;
    Button testPlaybackButton;
    double test_freq = 0;
    Note note;

    MediaRecorder recorder;
    MediaPlayer player;

    private static int MIC_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isMicPresent()) {
            getMicPermission();
        }


        noteText = findViewById(R.id.noteView);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        testPlaybackButton = findViewById(R.id.testPlaybackButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isActive = true;

                try {
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(getRecordingFilePath());
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.prepare();
                    recorder.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                tune();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isActive = false;
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        });

        testPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // test if audio record is working

                try {
                    player = new MediaPlayer();
                    player.setDataSource(getRecordingFilePath());
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public void tune() {
        if (isActive) {
            note = new Note(test_freq);
            note.findNearestNote();
            noteText.setText("FREQUENCY: "+ note.getFrequency() +"\nNOTE: " + note.getNoteName() + String.format("\nOFFSET: %.2f", note.getOffset()));
            test_freq++;
            note = null;
            refresh(100);
        } else {
            test_freq = 0;
        }
    }

    private void refresh(int ms) {
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tune();
            }
        };

        handler.postDelayed(runnable, ms);
    }

    private boolean isMicPresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            ==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(directory, "testFile" + ".mp3");
        return file.getPath();
    }

}