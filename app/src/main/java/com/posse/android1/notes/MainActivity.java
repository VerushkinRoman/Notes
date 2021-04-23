package com.posse.android1.notes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.customview.widget.Openable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.posse.android1.notes.ui.notes.MainNoteFragment;

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;
    private Openable mDrawerLayout;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_settings)
                .setOpenableLayout(mDrawerLayout)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isOpen()) {
            mDrawerLayout.close();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment noteListFragment = fragmentManager.findFragmentByTag(MainNoteFragment.TAG_NOTES_LIST);
            if (noteListFragment != null && noteListFragment.isVisible()) {
                checkExit();
            } else {
                mIsBackShown = false;
                super.onBackPressed();
            }
            fragmentManager.setFragmentResult(MainNoteFragment.KEY_REQUEST_BACK_PRESSED, new Bundle());
            mLastTimePressed = System.currentTimeMillis();
        }
    }

    private void checkExit() {
        Toast.makeText(this, R.string.back_exit_confirmation, Toast.LENGTH_SHORT).show();
        if (System.currentTimeMillis() - mLastTimePressed < BACK_BUTTON_EXIT_DELAY && mIsBackShown) {
            System.exit(0);
        }
        mIsBackShown = true;
    }
}