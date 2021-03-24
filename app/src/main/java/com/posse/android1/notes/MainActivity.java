package com.posse.android1.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.posse.android1.notes.ui.notes.NoteListFragment;

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Add a new note Action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem switchView = menu.findItem(R.id.action_switch_view);
        switchView.setOnMenuItemClickListener(item -> {
            if (switchView.getIcon().getConstantState().equals(getDrawable(android.R.drawable.ic_menu_sort_by_size).getConstantState())) {
                switchView.setIcon(android.R.drawable.ic_dialog_dialer);
            } else switchView.setIcon(android.R.drawable.ic_menu_sort_by_size);
            Snackbar.make(findViewById(R.id.action_switch_view), "change view", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!isLandscape) {
            NoteListFragment noteListFragment = (NoteListFragment) fragmentManager.findFragmentByTag("ListOfNotes");
            if (noteListFragment != null && noteListFragment.isVisible()) {
                checkExit();
            } else {
                super.onBackPressed();
                mIsBackShown = false;
            }
        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
            mIsBackShown = false;
        } else {
            checkExit();
        }
        mLastTimePressed = System.currentTimeMillis();
    }

    private void checkExit() {
        Snackbar.make(findViewById(R.id.note_list_container), "Press \"BACK\" again to exit", Snackbar.LENGTH_LONG).show();
        if (System.currentTimeMillis() - mLastTimePressed < BACK_BUTTON_EXIT_DELAY
                && System.currentTimeMillis() - mLastTimePressed > 800 && mIsBackShown) {
            System.exit(0);
        }
        mIsBackShown = true;
    }
}