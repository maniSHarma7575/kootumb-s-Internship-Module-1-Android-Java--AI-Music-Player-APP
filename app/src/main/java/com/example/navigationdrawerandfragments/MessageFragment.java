package com.example.navigationdrawerandfragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

import static android.media.MediaPlayer.*;

public class MessageFragment extends Fragment {
    private Button playButton,forwardButton,rewindButton;
    private SeekBar positionBar;
    private TextView elapsedTime,remainTime,songname;
    private MediaPlayer mediaPlayer;
    private int totaltime;
    private int positon;
    private ImageView gifImageView;
    private ArrayList<File> mySongs;
    private String msongName;
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper="";
    private static MessageFragment instance=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View RootView=inflater.inflate(R.layout.fragment_message,container,false);
        instance=this;
        //----------------------------------------------------------------------------------
        setRetainInstance(true);
        gifImageView=RootView.findViewById(R.id.mic);
        checkVoiceCommandPermission();
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getActivity());
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        parentRelativeLayout=(RelativeLayout)RootView.findViewById(R.id.parentRelativeLayout);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound=results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                gifImageView.setImageResource(R.drawable.micoff);
                if(matchesFound!=null)
                {


                    keeper=matchesFound.get(0);
                    if(keeper.equalsIgnoreCase("Play the song") || keeper.equalsIgnoreCase("Please Play the song") ||keeper.equalsIgnoreCase("Play song")|| keeper.equalsIgnoreCase("Play"))
                    {
                        playSong();
                    }
                    if(keeper.equalsIgnoreCase("Pause the song") || keeper.equalsIgnoreCase("Please Pause the song") ||keeper.equalsIgnoreCase("Pause song")|| keeper.equalsIgnoreCase("Pause"))
                    {
                        pauseSong();
                    }
                    if(keeper.equalsIgnoreCase("Play the next song") || keeper.equalsIgnoreCase("Please Play the next song") ||keeper.equalsIgnoreCase("Play next song")|| keeper.equalsIgnoreCase("next"))
                    {
                        playnextSong();
                    }
                    if(keeper.equalsIgnoreCase("Play the previous song") || keeper.equalsIgnoreCase("Please Play the previous song") ||keeper.equalsIgnoreCase("Play previous song")|| keeper.equalsIgnoreCase("previous"))
                    {
                        playpreviousSong();
                    }
                    if(keeper.equalsIgnoreCase("stop"))
                    {
                        onstop();
                    }
                    Toast.makeText(getActivity(),"Result ="+keeper,Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        gifImageView.setImageResource(R.drawable.mico);
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:

                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });


        //------------------------------------------------------------------------

        playButton=(Button)RootView.findViewById(R.id.playBtn);


        elapsedTime=(TextView)RootView.findViewById(R.id.elapsedTimeLabel);
        remainTime=(TextView)RootView.findViewById(R.id.remainTimeLabel);
        songname=(TextView)RootView.findViewById(R.id.tv);
        positionBar=(SeekBar)RootView.findViewById(R.id.positionBar);


        songname.setSelected(true);


        if(getArguments()!=null) {
            mySongs = (ArrayList) getArguments().getSerializable("songs");
            msongName = mySongs.get(positon).getName();
            String namesong = getArguments().getString("name");
            songname.setText(namesong);
            songname.setSelected(true);

            positon = getArguments().getInt("position", 0);
            Uri uri = Uri.parse(mySongs.get(positon).toString());
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getActivity(), uri);
            mediaPlayer.setLooping(true);
            mediaPlayer.seekTo(0);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.stop);
            totaltime=mediaPlayer.getDuration();

            positionBar.setMax(totaltime);
        }



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null) {

                    if (!mediaPlayer.isPlaying()) {

                        mediaPlayer.start();
                        playButton.setBackgroundResource(R.drawable.stop);
                    } else {

                        mediaPlayer.pause();
                        playButton.setBackgroundResource(R.drawable.play);
                    }
                }

            }
        });
        forwardButton = (Button) RootView.findViewById(R.id.forwardBtn);
        rewindButton=(Button)RootView.findViewById(R.id.rewindbtn);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null) {
                    if(mediaPlayer.getCurrentPosition()>0)
                    playnextSong();
                }
            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null) {

                    playpreviousSong();
                }
            }
        });
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    if(mediaPlayer!=null) {
                        mediaPlayer.seekTo(progress);

                    }positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar volumeBar = (SeekBar) RootView.findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNumber=progress/100f;
                if(mediaPlayer!=null)mediaPlayer.setVolume(volumeNumber,volumeNumber);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run(){
                while(mediaPlayer!=null)
                {
                    try {
                        Message message=new Message();
                        message.what=mediaPlayer.getCurrentPosition();
                        handler.sendMessage(message);
                        Thread.sleep(1000);
                    }
                    catch (Exception ignored)
                    {

                    }



                }
            }
        }).start();
        return RootView;

    }
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            int currentPosition=msg.what;
            positionBar.setProgress(currentPosition);
            String elapsed=createTimeLabel(currentPosition);
            elapsedTime.setText(elapsed);
            String remaining=createTimeLabel(totaltime-currentPosition);
            remainTime.setText("-"+remaining);
        }
    };
    public String createTimeLabel(int time)
    {
        String timeLabel="";
        int min=time/1000/60;
        int sec=time/1000%60;
        timeLabel=min+":";
        if(sec<10)
        {
            timeLabel+="0";
        }
        timeLabel+=sec;
        return timeLabel;
    }
    private void playnextSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        positon=((positon+1)%mySongs.size());
        Uri uri=Uri.parse(mySongs.get(positon).toString());
        mediaPlayer=MediaPlayer.create(getActivity(),uri);
        msongName=mySongs.get(positon).toString();
        songname.setText(msongName.substring(msongName.lastIndexOf('/')+1));
        totaltime=mediaPlayer.getDuration();
        mediaPlayer.setLooping(true);
        positionBar.setMax(totaltime);
        mediaPlayer.start();

        if (!mediaPlayer.isPlaying()) {


            playButton.setBackgroundResource(R.drawable.play);
        } else {


            playButton.setBackgroundResource(R.drawable.stop);
        }

    }

    private void playpreviousSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        positon=((positon-1)<0?(mySongs.size()-1):(positon-1));
        Uri uri=Uri.parse(mySongs.get(positon).toString());
        mediaPlayer=MediaPlayer.create(getActivity(),uri);
        msongName=mySongs.get(positon).toString();

        songname.setText(msongName.substring(msongName.lastIndexOf('/')+1));
        totaltime=mediaPlayer.getDuration();
        mediaPlayer.setLooping(true);
        positionBar.setMax(totaltime);
        mediaPlayer.start();

        if (!mediaPlayer.isPlaying()) {


            playButton.setBackgroundResource(R.drawable.play);
        } else {


            playButton.setBackgroundResource(R.drawable.stop);
        }

    }

    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(intent);
                getActivity().finish();

            }
        }
    }
    private void playSong()
    {
        if (mediaPlayer!=null && !mediaPlayer.isPlaying()) {

            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.stop);
        }
    }
    private void pauseSong()
    {
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            playButton.setBackgroundResource(R.drawable.play);
        }

    }

    public void onstop()
    {
        if(mediaPlayer!=null)
        mediaPlayer.release();
        mediaPlayer=null;
    }
    public static MessageFragment getInstance(){
        return instance;
    }


}
