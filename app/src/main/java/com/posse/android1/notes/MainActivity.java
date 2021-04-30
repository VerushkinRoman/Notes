package com.posse.android1.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.customview.widget.Openable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.posse.android1.notes.firebase.NoteFromFirestore;
import com.posse.android1.notes.firebase.NoteSourceFirestoreImpl;
import com.posse.android1.notes.ui.notes.MainNoteFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int BACK_BUTTON_EXIT_DELAY = 3000;
    private long mLastTimePressed;
    private boolean mIsBackShown = false;
    private AppBarConfiguration mAppBarConfiguration;
    private Openable mDrawerLayout;
    private NavController mNavController;
    private MaterialButton mLoginButton;
    private MaterialButton mLogoutButton;
    private FirebaseUser mUser;
    private TextView mLoginText;
    private ImageView mUserImage;
    private FirebaseAuth mAuth;
    //    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleAccount;
    private final ActivityResultLauncher<Intent> mStartLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::setAuthResult);

    private void setAuthResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                mGoogleAccount = task.getResult(ApiException.class);
            } catch (ApiException e) {
                Log.w("login", "signInResult:failed code=" + e.getStatusCode());
            }
            authToFirestore();
        } else {
            mLoginButton.setVisibility(View.VISIBLE);
            mLogoutButton.setVisibility(View.GONE);
        }
    }

    private void authToFirestore() {
        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleAccount.getIdToken(), null);
        mUser.linkWithCredential(credential).addOnCompleteListener(this, task -> {
            String oldUser = mUser.getUid();
            if (task.isSuccessful()) {
                Log.d("login", "linkWithCredential:success");
                mergeAccounts(task, oldUser);
            } else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    if (mUser.isAnonymous()) mUser.delete();
                    mAuth.signInWithCredential(credential).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("login", "signInWithCredential:success");
                            mergeAccounts(task1, oldUser);
                        }
                    });
                    return;
                }
                Log.w("login", "linkWithCredential:failure", task.getException());
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mergeAccounts(Task<AuthResult> task, String oldUser) {
//        transferAnonymousNotesToUserAccount(mUser.getUid(), Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getEmail()));
        mUser = Objects.requireNonNull(task.getResult()).getUser();
        NoteSourceFirestoreImpl.getInstance().setAuthor(oldUser, mUser.getEmail());
        setLoggedUserData(mGoogleAccount);
    }

    private void transferAnonymousNotesToUserAccount(String anonymousUser, String loggedUser) {
        CollectionReference collection = FirebaseFirestore.getInstance().collection(NoteSourceFirestoreImpl.COLLECTION);
        collection.whereEqualTo(NoteFromFirestore.FIELD_AUTHOR, anonymousUser).get().addOnCompleteListener(
                task -> onFetchComplete(task, loggedUser)).addOnFailureListener(this::onFetchFailed);
    }

    private void onFetchComplete(Task<QuerySnapshot> task, String newAuthor) {
        CollectionReference collection = FirebaseFirestore.getInstance().collection(NoteSourceFirestoreImpl.COLLECTION);
        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
            collection.document(document.getId()).update(NoteFromFirestore.FIELD_AUTHOR, newAuthor);
        }
//        NoteSourceFirestoreImpl.getInstance().setAuthor(newAuthor);
    }

    private void onFetchFailed(Exception exception) {
        Log.e("TAG", "Fetch failed", exception);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mGoogleAccount = GoogleSignIn.getLastSignedInAccount(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_settings)
                .setOpenableLayout(mDrawerLayout)
                .build();
        NavigationView navigationView = setNavigation();
        View headerView = navigationView.getHeaderView(0);
        mUserImage = headerView.findViewById(R.id.imageView);
        mLoginText = headerView.findViewById(R.id.textView);
        setLoginButton(headerView);
        setLogoutButton(headerView);
        if (mUser != null && mGoogleAccount != null) {
            setLoggedUserData(mGoogleAccount);
            NoteSourceFirestoreImpl.getInstance().setAuthor(null, mGoogleAccount.getEmail());
        } else signInAnonymously();

    }

    @NotNull
    private NavigationView setNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);
        return navigationView;
    }

    private void setLoginButton(View headerView) {
        mLoginButton = headerView.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(v -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mStartLogin.launch(mGoogleSignInClient.getSignInIntent());
        });
    }

    private void setLogoutButton(View headerView) {
        mLogoutButton = headerView.findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(v -> AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(task -> {
                    mLoginButton.setVisibility(View.VISIBLE);
                    mLogoutButton.setVisibility(View.GONE);
                    mUserImage.setImageResource(R.mipmap.ic_launcher_round);
                    mLoginText.setText(R.string.nav_header_subtitle);
                    signInAnonymously();
                }));
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInAnonymously:success");
                mUser = mAuth.getCurrentUser();
                NoteSourceFirestoreImpl.getInstance().setAuthor(null, Objects.requireNonNull(mUser).getUid());
            } else {
                Log.w("TAG", "signInAnonymously:failure", task.getException());
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoggedUserData(GoogleSignInAccount account) {
        mLoginButton.setVisibility(View.GONE);
        mLogoutButton.setVisibility(View.VISIBLE);
        mLoginText.setText(account.getEmail());
        Drawable defaultPicture = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round);
        Picasso.get().load(account.getPhotoUrl()).resize(Objects.requireNonNull(defaultPicture)
                .getIntrinsicWidth(), defaultPicture.getIntrinsicHeight()).into(mUserImage);
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