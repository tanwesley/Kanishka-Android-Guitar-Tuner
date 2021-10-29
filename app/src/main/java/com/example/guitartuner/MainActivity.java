package com.example.guitartuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartuner.R;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private static int MIC_PERMISSION_CODE = 200;

    private int backButtonCount = 0;

    private CustomGauge gauge;

    TextView frequencyText, noteText, offsetText;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isMicPresent()) {
            getMicPermission();
        }

        gauge = findViewById(R.id.gauge1);


        frequencyText = findViewById(R.id.frequencyText);
        noteText = findViewById(R.id.noteText);
        offsetText = findViewById(R.id.offsetText);

        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }



    public void processPitch(float pitchInHz) {

        if (pitchInHz < 0) {
            noteText.setText("");
            frequencyText.setText("Frequency: ");
            offsetText.setText("");
            gauge.setValue(25);
        } else {
            note = new Note(pitchInHz);
            note.findNearestNote();

            noteText.setText(note.getNoteName());
            frequencyText.setText(String.format("Frequency:\n%.2f hz", note.getFrequency()));
            if (note.getOffsetCents() >= 0) {
                offsetText.setText(String.format("+%.2f cents", note.getOffsetCents()));
            } else {
                offsetText.setText(String.format("%.2f cents", note.getOffsetCents()));
            }
            gauge.setValue((int) (25 + note.getOffsetCents()));

            if (Math.abs(note.getOffsetCents()) < 6) {
                noteText.setTextColor(Color.parseColor("#00FF00"));
                gauge.setValue(25);
            } else {
                noteText.setTextColor(Color.parseColor("#FF0000"));
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Press back again to close application", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    // Methods to check for mic and get permissions to use mic
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
}