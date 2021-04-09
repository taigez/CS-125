package edu.illinois.cs.cs125.spring2020.mp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

/** the initial launch activity.
 *
 */
public class LaunchActivity extends AppCompatActivity {

    /** Constant request code for sign in.*/
    private static final int RC_SIGN_IN = 123;

    /** goLogin button.*/
    private Button loginButton;

    /** checking logged in / not logged in onCreate.
     *
     * @param savedInstanceState idk some random bundle
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        loginButton = findViewById(R.id.goLogin);
        loginButton.setOnClickListener(v -> {
                    // Change the label's text
            createSignInIntent();
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) { // see below discussion
            // launch MainActivity
            openMainActivity();
            finish();
        } else {
            // start login activity for result - see below discussion
            createSignInIntent();
        }
    }

    /** Opens MainActivity.
     *
     */
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Creates sign-in Intent.
     *
     */
    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    /**
     * Invoked by the Android system when a request launched by startActivityForResult completes.
     * @param requestCode the request code passed by to startActivityForResult
     * @param resultCode a value indicating how the request finished (e.g. completed or canceled)
     * @param data an Intent containing results (e.g. as a URI or in extras)
     */
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                openMainActivity();
                finish();
            }
        }
    }
    // [END auth_fui_result]
}
