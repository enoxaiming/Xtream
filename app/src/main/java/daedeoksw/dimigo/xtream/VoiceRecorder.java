package daedeoksw.dimigo.xtream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;


/**
 * Created by Chan_Woo_Kim on 2016-11-17.
 */
public class VoiceRecorder extends AppCompatActivity implements View.OnClickListener {
    private final String LOG = "VoiceRecorder";
    private String URL = "http://esplay.xyz/xser/upload";
    private static String mFileName = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    private Button mPlayButton;
    private Button mRecordButton;
    private Button mSendButton;
    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;

    public VoiceRecorder() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/XStreamFiles/test1.mp3";
        Log.w(LOG,mFileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


        File xStreams = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/XStreamFiles");
        if(!xStreams.exists()){
            xStreams.mkdirs();
        }



        mRecordButton = (Button) findViewById(R.id.btn_record);
        mPlayButton = (Button) findViewById(R.id.btn_play);
        mSendButton = (Button) findViewById(R.id.sendbtn);
        mRecordButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        Log.e("name",mFileName);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.w(LOG, "prepare() Error");
            e.printStackTrace();
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.w(LOG, "prepare() Error");
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_record) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                mRecordButton.setText("StopRecording");
            } else {
                mRecordButton.setText("StartRecording");
            }
            mStartRecording = !mStartRecording;

        } else if (view.getId() == R.id.btn_play) {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                mPlayButton.setText("StopPlaying");
            } else {
                mPlayButton.setText("StartPlaying");
            }
            mStartPlaying = !mStartPlaying;
        }

        if(view.getId() == R.id.sendbtn) {
            uploadFile();
        }
    }


    private void uploadFile() {
        SendFile sendFile = ServiceGenerator.createService(SendFile.class);

        File file = new File(mFileName);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("data",file.getName(),requestFile);

        String descriptionString = "Hi";

        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        Call<ResponseBody> call = sendFile.upload(description,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload","success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("error",t.getMessage());
            }
        });

    }

}
