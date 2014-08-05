package com.demo.jdesousa.androidAudioCapture;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.demo.jdesousa.androidAudioCapture.ffmpeg.Videokit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    Button launchRecordBt;
    Button stopRecordBt;
    Button playBt;

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_DEFAULT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format

    String pathName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchRecordBt = (Button) findViewById(R.id.button_record);
        launchRecordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                }
            }
        });
        stopRecordBt = (Button) findViewById(R.id.button_stop);
        stopRecordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRecording() {

//        recorder = findAudioRecord();
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);


        if (recorder == null) {
            Log.i(getClass().getCanonicalName(), "Erreur pour la creation du recorder");
            return;
        }

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }


    private void writeAudioDataToFile() {
        // Write the output audio in byte

        pathName = getSdPath() + "/";
        String filePath = pathName + "testMono.pcm";
//        File file = new File(filePath);
        Log.i(getClass().getCanonicalName(), "filepath : " + filePath);
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                for (int i = 0; i < sData.length; i++) {
                    if (sData[i] != 0)
                        Log.i(getClass().getCanonicalName(), sData[i] + "");
                }

                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArraySize = sData.length;
        byte[] bytes = new byte[shortArraySize * 2];
        for (int i = 0; i < shortArraySize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void stopRecording() {
        // stops the recording activity
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;

            Videokit vk = Videokit.getInstance();
            vk.run(new String[]{"ffmpeg", "-i", pathName + "testMono.pcm", "-acodec", "mp3", "-ab", "128k", "-ar", "48000", pathName + "testMono.mp3"});

        }
    }

    //    public AudioRecord findAudioRecord() {
//        for (int rate : mSampleRates) {
//            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
//                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
//                    try {
//                       int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
//
//                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
//                            // check if we can instantiate and have a success
//                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);
//
//                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
//                                return recorder;
//                        }
//                    } catch (Exception e) {
//                        Log.e(getClass().getCanonicalName(), rate + "Exception, keep trying.",e);
//                    }
//                }
//            }
//        }
//        return null;
//    }


    public String getSdPath() {
        final String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        if (secondaryStorage != null && secondaryStorage.contains(":")) {
            for (String path : secondaryStorage.split(":")) {
                if (new File(path).canRead())
                    return path;
            }
        } else if (secondaryStorage != null) {
            return secondaryStorage;
        }

        Log.w(getClass().getCanonicalName(), "No SD path found, returning /");
        return "/";
    }
}
