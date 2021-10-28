package com.example.guitartuner;

public class Note {
    private float frequency;
    private float nearestFrequency;
    float hzPerCent;
    private float offsetHz;
    private float offsetCents;
    private String noteName;


    private final float[] frequenciesArray = new float[]{5587.65f, 5274.04f, 4978.03f, 4698.64f, 4434.92f,
            4186.01f, 3951.07f, 3729.31f, 3520.00f, 3322.44f, 3135.96f, 2959.96f, 2793.83f, 2637.02f, 2489.02f,
            2349.32f, 2217.46f, 2093.00f, 1975.53f, 1864.66f, 1760.00f, 1661.22f, 1567.98f, 1479.98f, 1396.91f,
            1318.51f, 1244.51f, 1174.66f, 1108.73f, 1046.50f, 987.767f, 932.328f, 880.000f, 830.609f, 783.991f,
            739.989f, 698.456f, 659.255f, 622.254f, 587.330f, 554.365f, 523.251f, 493.883f, 466.164f, 440.000f,
            415.305f, 391.995f, 369.994f, 349.228f, 329.628f, 311.127f, 293.665f, 277.183f, 261.626f, 246.942f,
            233.082f, 220.000f, 207.652f, 195.998f, 184.997f, 174.614f, 164.814f, 155.563f, 146.832f, 138.591f,
            130.813f, 123.471f, 116.541f, 110.000f, 103.826f, 97.9989f, 92.4986f, 87.3071f, 82.4069f, 77.7817f,
            73.4162f, 69.2957f, 65.4064f, 61.7354f, 58.2705f, 55.0000f, 51.9131f, 48.9994f, 46.2493f, 43.6535f,
            41.2034f, 38.8909f, 36.7081f, 34.6478f, 32.7032f, 30.8677f, 29.1352f, 27.5000f, 25.9565f, 24.4997f,
            23.1247f, 21.8268f, 20.6017f, 19.4454f, 18.3540f, 17.3239f, 16.3516f};

    private final String[] notesArray = new String[]{"F", "E", "D#", "D", "C#", "C", "B", "A#", "A", "G#", "G", "F#"};



    public Note(float f) {
        this.frequency = f;
    }

    public void findNearestNote() {
        int length = frequenciesArray.length;
        int frequencyIndex = 0;

        for (int i = 0, j = 1; i < length && j < length; i++, j++ ) {
            if (i==0 && frequency > frequenciesArray[i]) {
                frequencyIndex = 0;
                //nearestIndex = 0;
                break;
            } else if (frequenciesArray[i] >= frequency && frequency > frequenciesArray[j]) {
                frequencyIndex = (frequenciesArray[i] - frequency) < (frequency - frequenciesArray[j]) ? i : j;
                break;
            } else if (j == length - 1) {
                frequencyIndex = length - 1;
            }
        }

        nearestFrequency = frequenciesArray[frequencyIndex];
        noteName = notesArray[frequencyIndex % notesArray.length];

        // calculate ratio of hz/cent in between semitones based on adjacent notes
        // there are 100 cents in between each semitone/half-step
        if (frequency > nearestFrequency) {
            hzPerCent = (nearestFrequency - frequenciesArray[frequencyIndex+1])/100;
        } else if (frequency < nearestFrequency) {
            hzPerCent = (frequenciesArray[frequencyIndex-1] - nearestFrequency)/100;
        }

    }

    public float getOffsetHz() {
        offsetHz = frequency - nearestFrequency;
        return offsetHz;
    }

    public float getOffsetCents() {
        float offsetHz = getOffsetHz();
        offsetCents = offsetHz / hzPerCent;
        return offsetCents;
    }



    public float getFrequency() {
        return this.frequency;
    }



    public String getNoteName() {
        return this.noteName;
    }
}
