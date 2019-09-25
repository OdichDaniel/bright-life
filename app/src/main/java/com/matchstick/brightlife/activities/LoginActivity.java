package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aquery.AQuery;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Toolbar tb;
    AQuery aQuery;
    ApiService service;
    Call<ResponseBody> call;
    String phoneNumber, pin;
    Call<ResponseBody> loginCall;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tb = (Toolbar) findViewById(R.id.toolbar);
        aQuery = new AQuery(this);
        toolbarSetUp();
        service = RetrofitBuilder.createService(ApiService.class);
       login();
        getPinResetCode();
        createPassword();
    }

    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Login");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void login() {
        aQuery.id(R.id.btn_login).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPhoneNumber = aQuery.id(R.id.et_registered_phone_number).text();
                String userPassword = aQuery.id(R.id.et_registered_password).text();
                if (userPhoneNumber.isEmpty() || userPassword.isEmpty()){
                    aQuery.toast("Phone number or pin is missing");
                    return;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("telephone", "256774614935");
                jsonObject.addProperty("pin", "1993");

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                loginCall = service.login(requestBody);
                kProgressHUD = KProgressHUD.create(LoginActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Logging in ...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                loginCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            kProgressHUD.dismiss();
                            aQuery.openFromRight(MainActivity.class);
                        }else {
                            // error case
                            switch (response.code()) {
                                case 400:
                                    kProgressHUD.dismiss();
                                    aQuery.toast(getString(R.string.network_error_400));
                                    break;
                                case 401:
                                    kProgressHUD.dismiss();;
                                    aQuery.toast(getString(R.string.network_error_401));
                                    break;
                                case 404:
                                    kProgressHUD.dismiss();
                                    aQuery.toast(getString(R.string.network_error_404));
                                    break;
                                case 500:
                                    kProgressHUD.dismiss();
                                    aQuery.toast(getString(R.string.network_error_500));
                                    break;
                                default:
                                    kProgressHUD.dismiss();
                                    aQuery.toast(getString(R.string.network_error_401));
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void getPinResetCode() {
        aQuery.id(R.id.layout_forgot_password).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.dialog_pin_reset_request);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText email = (EditText) dialog.findViewById(R.id.et_email);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
                Button btnCancle = (Button) dialog.findViewById(R.id.btn_cancel);
                ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
                dialog.show();
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        successDialog("Password reset code has been sent to your email");
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void resetPinWithResetCode() {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_pin_reset);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText username = (EditText) dialog.findViewById(R.id.et_phone_number);
        EditText resetCode = (EditText) dialog.findViewById(R.id.et_reset_code);
        EditText newPin = (EditText) dialog.findViewById(R.id.et_new_password);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancle = (Button) dialog.findViewById(R.id.btn_cancel);
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
        dialog.show();
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog("Your password has been successfully reset");
                dialog.dismiss();
            }
        });
    }

    private void createPassword(){
        aQuery.id(R.id.layout_create_pin).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.dialog_pin_reset_request);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText etPhoneNumber = (EditText) dialog.findViewById(R.id.et_phone_number);
                EditText resetCode = (EditText) dialog.findViewById(R.id.et_reset_code);
                EditText newPin = (EditText) dialog.findViewById(R.id.et_new_password);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
                Button btnCancle = (Button) dialog.findViewById(R.id.btn_cancel);
                ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
                TextView textView = (TextView) dialog.findViewById(R.id.password_title);
                textView.setText("Create your password");
                dialog.show();
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phoneNumber = etPhoneNumber.getText().toString();
                        pin = newPin.getText().toString();
                        if (phoneNumber.isEmpty() || pin.isEmpty()){
                            aQuery.toast("Please fill in the missing fields");
                            return;
                        }
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("phone_number", phoneNumber);
                        jsonObject.addProperty("pin", pin);

                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                        call = service.createPin(requestBody);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()){
                                    successDialogPinCreated("Your password has been successfully created, please login");
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                aQuery.toast(getString(R.string.network_error));
                            }
                        });

                    }
                });
            }
        });
    }

    public void successDialog(String message) {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_message);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView successText = (TextView) dialog.findViewById(R.id.txt_message);
        Button btnAction = (Button) dialog.findViewById(R.id.btn_action);
        btnAction.setText("Ok");
        successText.setText(message);
        dialog.show();
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                resetPinWithResetCode();
            }
        });

        dialog.setCancelable(false);
    }

    public void successDialogPinCreated(String message) {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_message);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView successText = (TextView) dialog.findViewById(R.id.txt_message);
        Button btnAction = (Button) dialog.findViewById(R.id.btn_action);
        btnAction.setText("Ok");
        successText.setText(message);
        dialog.show();
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
    }
}