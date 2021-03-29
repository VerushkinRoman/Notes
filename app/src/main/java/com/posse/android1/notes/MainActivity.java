package com.posse.android1.notes;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.posse.android1.notes.ui.editor.EditorFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int NOTE_LIST_VIEW = 1;
    public static final int NOTE_VIEW = 2;
    public static final int EDITOR_VIEW = 3;
    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private static final int BACK_BUTTON_ACCIDENT_DELAY = 500;
    private static final String KEY_VIEW = MainActivity.class.getCanonicalName() + "mIsGridView";
    private boolean mIsGridView = false;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;
    private MenuItem.OnMenuItemClickListener mEditClickListener;
    private MenuItem mSwitchView;
    private MenuItem mEditBar;
    private boolean mIsLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) mIsGridView = savedInstanceState.getBoolean(KEY_VIEW);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        mEditBar = menu.findItem(R.id.edit_bar);
        if (mIsLandscape) {
            showSwitchView(false);
        } else showEditBar(false);
        Drawable gridView = Objects.requireNonNull(ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_dialer));
        Drawable lineView = Objects.requireNonNull(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size));
        Drawable menuIcon = isGridView() ? gridView : lineView;
        mSwitchView.setIcon(menuIcon);
        mSwitchView.setOnMenuItemClickListener(item -> {
            Drawable icon = mSwitchView.getIcon().getConstantState().equals(lineView.getConstantState()) ? gridView : lineView;
            mSwitchView.setIcon(icon);
            mIsGridView = icon.equals(gridView);
            refreshFragment();
            return false;
        });
        mEditBar.setOnMenuItemClickListener(mEditClickListener);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment noteListFragment = fragmentManager.findFragmentByTag("ListOfNotes");
        Fragment noteFragment = fragmentManager.findFragmentByTag("Note");
        EditorFragment editorFragment = (EditorFragment) fragmentManager.findFragmentByTag("Editor");
        if (editorFragment != null && editorFragment.isVisible()) {
            showHideButtons(NOTE_VIEW);
            editorFragment.saveNote();
            mIsBackShown = false;
            if (mIsLandscape) super.onBackPressed();
        }
        if (noteListFragment != null && noteListFragment.isVisible()) {
            int view = mIsLandscape ? NOTE_VIEW : NOTE_LIST_VIEW;
            showHideButtons(view);
            checkExit();
        } else {
            super.onBackPressed();
            if (noteListFragment != null && noteListFragment.isVisible()) {
                showHideButtons(NOTE_LIST_VIEW);
            }
            mIsBackShown = false;
        }
        if (noteFragment != null && noteFragment.isVisible()) {
            showHideButtons(NOTE_VIEW);
            mIsBackShown = false;
        }
        mLastTimePressed = System.currentTimeMillis();
    }

    public void showHideButtons(int view) {
        switch (view) {
            case NOTE_VIEW:
                showFloatingButton(mIsLandscape);
                showSwitchView(false);
                showEditBar(true);
                break;
            case NOTE_LIST_VIEW:
                showFloatingButton(true);
                showSwitchView(true);
                showEditBar(false);
                break;
            case EDITOR_VIEW:
                showFloatingButton(false);
                showEditBar(false);
                showSwitchView(false);
                break;
            default:
                throw new RuntimeException("Unexpected view: " + view);
        }

    }

    private void checkExit() {
        Toast.makeText(this, "Press \"BACK\" again to exit", Toast.LENGTH_SHORT).show();
        if (System.currentTimeMillis() - mLastTimePressed < BACK_BUTTON_EXIT_DELAY
                && System.currentTimeMillis() - mLastTimePressed > BACK_BUTTON_ACCIDENT_DELAY
                && mIsBackShown) {
            System.exit(0);
        }
        mIsBackShown = true;
    }

    public void setMenuEditClickListener(MenuItem.OnMenuItemClickListener listener) {
        mEditClickListener = listener;
    }

    public void showFloatingButton(boolean isVisible) {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            if (isVisible) fab.show();
            else fab.hide();
        }
    }

    private void showSwitchView(boolean isVisible) {
        if (mSwitchView != null) mSwitchView.setVisible(isVisible);
    }

    private void showEditBar(boolean isVisible) {
        if (mEditBar != null) mEditBar.setVisible(isVisible);
    }

    public boolean isGridView() {
        return mIsGridView;
    }
}