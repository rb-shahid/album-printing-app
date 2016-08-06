package com.byteshaft.albumsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.byteshaft.albumsapp.R;
import com.byteshaft.albumsapp.utils.Constants;
import com.byteshaft.albumsapp.utils.network.HttpRequest;
import com.byteshaft.albumsapp.utils.network.HttpRequestStateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.byteshaft.albumsapp.utils.ui.Helpers.showToast;

public class SignUp extends AppCompatActivity implements HttpRequestStateListener,
        View.OnClickListener {

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    private EditText mPasswordRepeatEntry;
    private EditText mFullNameEntry;
    private EditText mMobileNumberEntry;

    private HttpRequest mRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEmailEntry = (EditText) findViewById(R.id.entry_signup_email);
        mPasswordEntry = (EditText) findViewById(R.id.entry_signup_password);
        mPasswordRepeatEntry = (EditText) findViewById(R.id.entry_signup_password_repeat);
        mFullNameEntry = (EditText) findViewById(R.id.entry_signup_fullname);
        mMobileNumberEntry = (EditText) findViewById(R.id.entry_signup_mobile);
    }

    @Override
    public void onReadyStateChanged(HttpURLConnection connection, int readyState) {
        System.out.println(connection);
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                try {
                    switch (connection.getResponseCode()) {
                        case HttpRequest.RESPONSE_CREATED:
                            showToast(mRequest.getResponseText());
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signup_execute:
                validateSignUpData();
                break;
        }
    }

    private void raiseFieldMandatory(EditText editText) {
        if (isEditTextEmpty(editText)) {
            editText.setError("Required field.");
        }
    }

    private boolean isEditTextEmpty(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty();
    }

    private void validateSignUpData() {
        String email = mEmailEntry.getText().toString();
        String password = mPasswordEntry.getText().toString();
        String repeatPassword = mPasswordRepeatEntry.getText().toString();
        String fullName = mFullNameEntry.getText().toString();
        String mobileNumber = mMobileNumberEntry.getText().toString();

        EditText[] inputFields = {
                mEmailEntry,
                mPasswordEntry,
                mPasswordRepeatEntry,
                mFullNameEntry,
                mMobileNumberEntry
        };
        for (EditText editText: inputFields) {
            raiseFieldMandatory(editText);
        }


        if (!isEmailValid(email)) {
            mEmailEntry.setError("Invalid Email.");
        }
        if (!password.equals(repeatPassword)) {
            mPasswordRepeatEntry.setError("Passwords should be same.");
        }

        signUp(email, password, fullName, mobileNumber);
    }

    private void signUp(String email, String password, String fullName, String mobile) {
        mRequest = new HttpRequest(getApplicationContext());
        mRequest.setOnReadyStateChangedListener(this);
        mRequest.open("POST", Constants.ENDPOINT_REGISTER);
        mRequest.send(getSignUpData(email, password, fullName, mobile));
    }

    private String getSignUpData(
            String email,
            String password,
            String fullName,
            String mobile
    ) {
        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("password", password);
            object.put("full_name", fullName);
            object.put("mobile_number", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}