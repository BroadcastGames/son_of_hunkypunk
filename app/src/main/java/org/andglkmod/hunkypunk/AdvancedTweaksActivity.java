package org.andglkmod.hunkypunk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.andglkmod.hunkypunk.R;
import org.andglkmod.hunkypunk.events.GameListEmptyEvent;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class AdvancedTweaksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_tweaks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Close the Activity", Snackbar.LENGTH_LONG)
                        .setAction("Close", null).show();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 320L);
            }
        });

        buildFileReport();
    }

    public void buildFileReport() {
        TextView outputFileInfo = (TextView) findViewById(R.id.outputFileInfo0);
        outputFileInfo.setText("");
        outputInformationAboutDirectory("Cover images",  Paths.coverDirectory(), outputFileInfo);
        outputInformationAboutDirectory("StoryFiles",    Paths.ifDirectory(), outputFileInfo);
    }

    public void outputInformationAboutDirectory(String lable, File targetDirectory, TextView outputView) {
        outputView.append(lable + ": " + targetDirectory.getPath() + "\n");
        File[] filesInTargetDir = targetDirectory.listFiles();
        outputView.append("Files: " + filesInTargetDir.length + "\n");
    }

    public void downloadStoriesSet1(View view) {
        // Yes, set 1 is the name to user, set 0 the name internally.
        // EventBus.getDefault().post(new GameListEmptyEvent(GameListEmptyEvent.DOWNLOAD_PRESELECT_GAMES_SET0));
        BackgroundOperationsA.getGameListHelper(this).downloadPreselected(this);
    }
}
