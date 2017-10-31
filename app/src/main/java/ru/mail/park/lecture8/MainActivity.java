package ru.mail.park.lecture8;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;
    private EditText nameInput;
    private TextView userName;
    private ImageView avatar;
    private Button getButton;

    private ListenerHandler<Api.OnUserGetListener> userHandler;

    private Api.OnUserGetListener userListener = new Api.OnUserGetListener() {
        @Override
        public void onUserSuccess(final VkUser user) {
            stopProgress();
            setUser(user);
        }

        @Override
        public void onUserError(final Exception error) {
            stopProgress();
            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectActivityLeaks().build());

        progress = findViewById(R.id.progress);
        nameInput = findViewById(R.id.user_name_input);
        userName = findViewById(R.id.user_name);
        avatar = findViewById(R.id.user_avatar);
        getButton = findViewById(R.id.user_get);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startProgress();
                if (userHandler != null) {
                    userHandler.unregister();
                }
                userHandler = Api.getInstance().getUser(nameInput.getText().toString(), userListener);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userHandler != null) {
            userHandler.unregister();
        }
        stopProgress();
    }

    private void setUser(final VkUser user) {
        if (!TextUtils.isEmpty(user.getScreenName())) {
            userName.setText(String.format("%s %s %s",
                    user.getFirstName(), user.getScreenName(), user.getLastName()));
        } else {
            userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        }

        avatar.setImageDrawable(null);
        if (!TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(this).load(user.getAvatar()).into(avatar);
        }
    }

    private void startProgress() {
        progress.setVisibility(View.VISIBLE);
        getButton.setEnabled(false);
    }

    private void stopProgress() {
        progress.setVisibility(View.INVISIBLE);
        getButton.setEnabled(true);
    }
}
