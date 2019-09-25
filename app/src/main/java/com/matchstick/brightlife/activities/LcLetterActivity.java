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

public class LcLetterActivity extends AppCompatActivity {
    AQuery aQuery;
    ImageView lcLetterImage;
    public static final int RESULT_CODE_LC1_LETTER = 4;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String lc1letterPath;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;
    String lcFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_letter);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        lcLetterImage = (ImageView) findViewById(R.id.file_lc1_letter);
        service = RetrofitBuilder.createService(ApiService.class);
        openFileChooser();
        setDocumentUploaded();
        goToNext();
    }


    private void goToNext() {
        aQuery.id(R.id.btn_upload_lc1_letter_file).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aQuery.id(R.id.txt_lc1_letter).text() == "") {
                    aQuery.toast(getString(R.string.error_lc1));
                    return;
                }

                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                String pathCv = pref.getString(Constant.PATH_CV, "");
                String pathResultSlip = pref.getString(Constant.PATH_RESULT_SLIP, "");
                String pathReccommedation = pref.getString(Constant.PATH_RECCOMMENDATION, "");
                String pathLc1 = pref.getString(Constant.PATH_LC1_LETTER, "");

                File lcLetterFile = new File(pathLc1);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);


                RequestBody requestBodylc1 = RequestBody.create(MediaType.parse("multipart/form-data"), lcLetterFile);
                builder.addFormDataPart("lc_one_letter", lcLetterFile.getName(), requestBodylc1);

                builder.addFormDataPart("telephone", telephone);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
                kProgressHUD = KProgressHUD.create(LcLetterActivity.this)
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
        String lc1Letter = pref.getString(Constant.LC1_LETTER_URI, "");
        aQuery.id(R.id.txt_lc1_letter).text(lc1Letter);
        if (lc1Letter.endsWith(".doc")) {
            lcLetterImage.setImageResource(R.drawable.word);
        }
        if (lc1Letter.endsWith(".pdf")) {
            lcLetterImage.setImageResource(R.drawable.pdf);
        }

    }

    private void openFileChooser() {
        aQuery.id(R.id.layout_add_lc_leter).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_CODE_LC1_LETTER);
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
            if (requestCode == RESULT_CODE_LC1_LETTER) {
                ArrayList<String> docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                lc1letterPath = docPaths.get(0);

                File lcLetterFile = new File(lc1letterPath);
                lcFileName = lcLetterFile.getName();
                if (lcFileName.endsWith(".docs")) {
                    lcLetterImage.setImageResource(R.drawable.word);
                }
                if (lcFileName.endsWith(".pdf")) {
                    lcLetterImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_lc1_letter).text(lcFileName);
                editor.putString(Constant.LC1_LETTER_URI, lcFileName).commit();
                editor.putString(Constant.PATH_LC1_LETTER, lc1letterPath).commit();
            }
        }
    }

}