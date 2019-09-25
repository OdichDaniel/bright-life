package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.aquery.AQuery;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.AppUtils;
import com.matchstick.brightlife.entities.Constant;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultSlipActivity extends AppCompatActivity {
    AQuery aQuery;
    ImageView resultSlipImage;
    private static final int RESULT_CODE_SLIP = 1;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String resultSlipPath;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;
    String resultSlipName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_slip);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        resultSlipImage = (ImageView) findViewById(R.id.file_slip);
        service = RetrofitBuilder.createService(ApiService.class);
        openFileChooser();
        setDocumentUploaded();
        goToNext();
    }


    private void goToNext() {
        aQuery.id(R.id.btn_upload_result_slip_file).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aQuery.id(R.id.txt_resultslip).text() == "") {
                    aQuery.toast(getString(R.string.error_result_slip));
                    return;
                }

                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                String pathResultSlip = pref.getString(Constant.PATH_RESULT_SLIP, "");

                File resultSlipFile = new File(pathResultSlip);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);


                RequestBody requestBodyResultSlip = RequestBody.create(MediaType.parse("multipart/form-data"), resultSlipFile);
                builder.addFormDataPart("result_slip", resultSlipFile.getName(), requestBodyResultSlip);


                builder.addFormDataPart("telephone", telephone);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
                kProgressHUD = KProgressHUD.create(ResultSlipActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Uploading ...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            aQuery.openFromRight(ApplicationSuccessActivity.class);
                            kProgressHUD.dismiss();
                        } else {
                            switch (response.code()) {
                                case 500:
                                    aQuery.toast(getString(R.string.network_error));
                                    kProgressHUD.dismiss();
                                    break;
                                case 400:
                                    aQuery.toast(getString(R.string.network_error));
                                    kProgressHUD.dismiss();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (t instanceof SocketTimeoutException) {
                            // "Connection Timeout";
                            aQuery.toast("Request time out, please upload less than 500kb and try again");
                            kProgressHUD.dismiss();
                        } else if (t instanceof IOException) {
                            // "Timeout";
                            aQuery.toast("Upload time out, please try again");
                            kProgressHUD.dismiss();
                        } else {
                            //Call was cancelled by user
                            if (call.isCanceled()) {
                                aQuery.toast("Upload time out, please try again");
                                kProgressHUD.dismiss();
                            } else {
                                //Generic error handling
                                aQuery.toast("Upload time out, please try again");
                                kProgressHUD.dismiss();
                            }
                        }
                    }
                });

            }
        });
    }

    private void setDocumentUploaded() {
        String slip = pref.getString(Constant.RESULT_SLIP_URI, "");
        aQuery.id(R.id.txt_resultslip).text(slip);

        if (slip.endsWith(".doc")) {
            resultSlipImage.setImageResource(R.drawable.word);
        }
        if (slip.endsWith(".pdf")) {
            resultSlipImage.setImageResource(R.drawable.pdf);
        }
    }

    private void openFileChooser() {
        aQuery.id(R.id.layout_add_result_slip).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_CODE_SLIP);
            }
        });
    }

    private void showFileChooser(int requestCodeReturn) {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .pickFile(this, requestCodeReturn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE_SLIP) {
                ArrayList<String> docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                resultSlipPath = docPaths.get(0);
                File resultSlipFile = new File(resultSlipPath);
                resultSlipName = resultSlipFile.getName();

                if (resultSlipName.endsWith(".doc")) {
                    resultSlipImage.setImageResource(R.drawable.word);
                }
                if (resultSlipName.endsWith(".pdf")) {
                    resultSlipImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_resultslip).text(resultSlipName);
                editor.putString(Constant.RESULT_SLIP_URI, resultSlipName).commit();
                editor.putString(Constant.PATH_RESULT_SLIP, resultSlipPath).commit();

            }
        }
    }

}
