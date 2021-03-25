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

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private static final int BACK_BUTTON_ACCIDENT_DELAY = 500;
    private static final String KEY_VIEW = MainActivity.class.getCanonicalName() + "mIsGridView";
    private static boolean sIsGridView = false;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;
    private MenuItem mSwitchView;
    private boolean mIsLandscape;

    public static boolean isGridView() {
        return sIsGridView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) sIsGridView = savedInstanceState.getBoolean(KEY_VIEW);
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
        mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mSwitchView = menu.findItem(R.id.action_switch_view);
        if (mIsLandscape) mSwitchView.setVisible(false);
        Drawable gridView = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_dialer);
        Drawable lineView = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size);
        Drawable menuIcon = isGridView() ? gridView : lineView;
        mSwitchView.setIcon(menuIcon);
        mSwitchView.setOnMenuItemClickListener(item -> {
            Drawable icon = mSwitchView.getIcon().getConstantState().equals(lineView.getConstantState()) ? gridView : lineView;
            mSwitchView.setIcon(icon);
            sIsGridView = icon.equals(gridView);
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
        bundle.putBoolean(KEY_VIEW, sIsGridView);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!mIsLandscape) {
            NoteListFragment noteListFragment = (NoteListFragment) fragmentManager.findFragmentByTag("ListOfNotes");
            mSwitchView.setVisible(true);
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

    public MenuItem getSwitchView() {
        return mSwitchView;
    }
}