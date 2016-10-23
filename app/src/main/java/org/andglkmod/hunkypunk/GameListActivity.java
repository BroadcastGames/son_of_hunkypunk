package org.andglkmod.hunkypunk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.andglkmod.hunkypunk.dummy.DummyContent;

public class GameListActivity extends AppCompatActivity implements GameListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // changeMainFragment(2);
            }
        });

        changeMainFragment(3);
    }


    public void changeMainFragment(int fragmentSelector)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentSelector) {
            case 1:
                // fragmentTransaction.replace(R.id.mainActivityContainer,     new MainActivityFragment());
                break;
            case 2:
                // fragmentTransaction.replace(R.id.mainActivityContainer,     BlankFragment.newInstance("hi", "there"));
                break;
            case 3:
                fragmentTransaction.replace(R.id.mainActivityContainer,     GameListFragment.newInstance(1 /* Column */));
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i("GameListActivity", "onListFragmentInteraction");
    }
}
