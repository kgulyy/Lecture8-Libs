package ru.mail.park.lecture8;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.List;

import ru.mail.park.lecture8.task.VkGroup;
import ru.mail.park.lecture8.task.VkGroupUser;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;
    private EditText nameInput;
    private TextView userName;
    private TextView sex;
    private ImageView avatar;
    private Button getUserButton;
    private Button getGroupButton;
    private View userContainer;

    private ListenerHandler<Api.OnUserGetListener> userHandler;
    private ListenerHandler<Api.OnGroupGetListener> groupHandler;

    private Api.OnUserGetListener userListener = new Api.OnUserGetListener() {
        @Override
        public void onUserSuccess(final VkUser user) {
            setUser(user);
            stopProgress();
        }

        @Override
        public void onUserError(final Exception error) {
            stopProgress();
            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private Api.OnGroupGetListener groupListener = new Api.OnGroupGetListener() {
        @Override
        public void onGroupSuccess(VkGroup group) {
            sortAndPrintGroupUsers(group);
        }

        @Override
        public void onGroupError(Exception error) {
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
        sex = findViewById(R.id.user_sex);
        avatar = findViewById(R.id.user_avatar);
        getUserButton = findViewById(R.id.user_get);
        getGroupButton = findViewById(R.id.group_get);
        userContainer = findViewById(R.id.user_container);

        getUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startProgress();
                hideKeyboard(nameInput);
                if (userHandler != null) {
                    userHandler.unregister();
                }
                userHandler = Api.getInstance().getUser(nameInput.getText().toString(), userListener);
            }
        });

        getGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupHandler != null) {
                    groupHandler.unregister();
                }
                groupHandler = Api.getInstance().getGroup(777, groupListener);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userHandler != null) {
            userHandler.unregister();
        }
        if (groupHandler != null) {
            groupHandler.unregister();
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
        sex.setText(String.valueOf(user.getSex()));

        avatar.setImageDrawable(null);
        if (!TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(this).load(user.getAvatar()).into(avatar);
        }
    }

    private void sortAndPrintGroupUsers(final VkGroup group) {
        Comparator<VkGroupUser> byWeightDesc = new Comparator<VkGroupUser>() {
            @Override
            public int compare(VkGroupUser o1, VkGroupUser o2) {
                return Double.compare(o2.getWeight(), o1.getWeight());
            }
        };

        Ordering<VkGroupUser> firstFemaleByWeight = Ordering.natural().onResultOf(new Function<VkGroupUser, Comparable>() {
            @Override
            public Comparable apply(@NonNull VkGroupUser input) {
                return input.getGender().ordinal();
            }
        }).compound(byWeightDesc);

        List<VkGroupUser> sortedGroupUsers = firstFemaleByWeight.sortedCopy(group.getUsers());

        System.out.println(sortedGroupUsers);
    }

    private void startProgress() {
        progress.setVisibility(View.VISIBLE);
        userContainer.setVisibility(View.INVISIBLE);
        getUserButton.setEnabled(false);
        getGroupButton.setEnabled(false);
    }

    private void stopProgress() {
        progress.setVisibility(View.INVISIBLE);
        userContainer.setVisibility(View.VISIBLE);
        getUserButton.setEnabled(true);
        getGroupButton.setEnabled(true);
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }
}
