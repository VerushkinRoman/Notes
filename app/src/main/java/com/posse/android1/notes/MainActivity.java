package com.posse.android1.notes;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.posse.android1.notes.ui.notes.NoteListFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private static final int BACK_BUTTON_ACCIDENT_DELAY = 500;
    private static final String KEY_VIEW = MainActivity.class.getCanonicalName() + "mIsGridView";
    private static boolean mIsGridView = false;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;

    public static boolean isGridView() {
        return mIsGridView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsGridView = savedInstanceState.getBoolean(KEY_VIEW);
        }
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
        Drawable menuIcon;
        if (mIsGridView) {
            menuIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_dialer);
        } else {
            menuIcon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size);
        }
        switchView.setIcon(menuIcon);
        switchView.setOnMenuItemClickListener(item -> {
            Drawable icon;
            if (switchView.getIcon().getConstantState()
                    .equals(Objects.requireNonNull(ContextCompat.getDrawable
                            (this, android.R.drawable.ic_menu_sort_by_size))
                            .getConstantState())) {
                icon = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_dialer);
                mIsGridView = true;
            } else {
                icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size);
                mIsGridView = false;
            }
            switchView.setIcon(icon);
            refreshFragment();
            return false;
        });
        return true;
    }

    private void refreshFragment() {
        Fragment noteListFragment = getSupportFragmentManager().findFragmentByTag("ListOfNotes");
        if (noteListFragment != null && noteListFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(noteListFragment);
            fragmentTransaction.attach(noteListFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(KEY_VIEW, mIsGridView);
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
                && System.currentTimeMillis() - mLastTimePressed > BACK_BUTTON_ACCIDENT_DELAY
                && mIsBackShown) {
            System.exit(0);
        }
        mIsBackShown = true;
    }
}