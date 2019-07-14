package com.example.navigationdrawerandfragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private String[] itemsAll;
    private ListView mSongsList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView=inflater.inflate(R.layout.fragment_profile,container,false);
        mSongsList=(ListView)RootView.findViewById(R.id.songsList);
        appExternalStoragePermission();
        return RootView;

    }
    public void appExternalStoragePermission()
    {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                            displayAudioSongName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                 //   @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

    }

    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File> arrayList=new ArrayList<>();
        File[] allfiles=file.listFiles();
        for(File individualFile: allfiles)
        {
            if(individualFile.isDirectory())
            {

                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }
            else
            {

                if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma"))
                {
                    arrayList.add(individualFile);
                }
            }
        }
        return arrayList;
    }

    private void displayAudioSongName()
    {
        final ArrayList<File> audioSongs=readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        itemsAll=new String[audioSongs.size()];
        for(int songCounter=0;songCounter<audioSongs.size();songCounter++)
        {
            itemsAll[songCounter]=audioSongs.get(songCounter).getName();
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MessageFragment.getInstance()!=null)MessageFragment.getInstance().onstop();
                String songName=mSongsList.getItemAtPosition(position).toString();
                Bundle bundle=new Bundle();
                bundle.putSerializable("songs", audioSongs);
                bundle.putString("name",songName);
                bundle.putInt("position",position);
                MessageFragment messageFragment=new MessageFragment();
                messageFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,messageFragment)
                        .commit();

            }
        });
    }
}
