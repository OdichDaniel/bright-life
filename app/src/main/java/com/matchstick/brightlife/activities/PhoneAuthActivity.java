package com.matchstick.brightlife.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.aquery.AQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.Constant;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    String formatedPhoneNumber, phoneNumber;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    Toolbar tb;
    AQuery aQuery;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String smsVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        mAuth = FirebaseAuth.getInstance();
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        tb = (Toolbar) findViewById(R.id.toolbar);

        toolbarSetUp();
        getVerificationCode();
        aQuery.id(R.id.btn_verify_code).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredCode = aQuery.id(R.id.et_verification_code).text();
                if (enteredCode.isEmpty() || enteredCode.length() < 6){
                    aQuery.toast("Enter the verification code");
                    return;
                }
                verifyCode(smsVerificationCode);
            }
        });
    }

    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Verify phone number");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void getVerificationCode() {
        aQuery.id(R.id.btn_send_number).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //aQuery.openFromRight(RequirementActivity.class);
                aQuery.id(R.id.indicator_loader).show();
                aQuery.id(R.id.btn_send_number).text("Please wait ...");
                aQuery.id(R.id.btn_send_number).disable();
                phoneNumber = aQuery.id(R.id.et_phone_number).text();
                if (phoneNumber.startsWith("0")){
                    formatedPhoneNumber = phoneNumber.replaceFirst("0", "+256");
                }else if (phoneNumber.startsWith("+256")){
                    formatedPhoneNumber = phoneNumber;
                }
                startPhoneNumberVerification(formatedPhoneNumber);
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            aQuery.id(R.id.indicator_loader).hide();
            aQuery.id(R.id.success_layout).show();
            aQuery.id(R.id.verification_code_layout).show();
            aQuery.id(R.id.phone_verification_layout).hide();

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            smsVerificationCode = phoneAuthCredential.getSmsCode();
            if (smsVerificationCode != null) {
                verifyCode(smsVerificationCode);
                aQuery.id(R.id.et_verification_code).text(smsVerificationCode);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            aQuery.id(R.id.indicator_loader).hide();
            aQuery.id(R.id.btn_send_number).text("Verify phone number");
            aQuery.id(R.id.btn_send_number).enable();
            aQuery.toast("Phone number verification failed");
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("User", "signInWithCredential:success");
                            editor.putString(Constant.REGISTERED_TELEPHONE, phoneNumber).commit();
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(PhoneAuthActivity.this, RequirementActivity.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                aQuery.toast("Invalid code.");
                                // [END_EXCLUDE]
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(PhoneAuthActivity.this, RequirementActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
