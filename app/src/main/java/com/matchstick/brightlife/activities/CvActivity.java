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

public class CvActivity extends AppCompatActivity {
    AQuery aQuery;
    ImageView  cvImage;
    public static final int RESULT_OODE_CV = 2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String cvPath;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;
    String cvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        cvImage = (ImageView) findViewById(R.id.file_cv);
        service = RetrofitBuilder.createService(ApiService.class);
        openFileChooser();
        setDocumentUploaded();
        goToNext();
    }


    private void goToNext() {
        aQuery.id(R.id.btn_upload_cv_file).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aQuery.id(R.id.txt_cv).text() == "") {
                    aQuery.toast(getString(R.string.error_cv));
                    return;
                }


                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                String pathCv = pref.getString(Constant.PATH_CV, "");


                File cvFile = new File(pathCv);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                RequestBody requestBodyCv = RequestBody.create(MediaType.parse("multipart/form-data"), cvFile);
                builder.addFormDataPart("curriculum_vitea", cvFile.getName(), requestBodyCv);

                builder.addFormDataPart("telephone", telephone);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
                kProgressHUD = KProgressHUD.create(CvActivity.this)
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
        String cv = pref.getString(Constant.CV_URI, "");
        aQuery.id(R.id.txt_cv).text(cv);

        if (cv.endsWith(".doc")) {
            cvImage.setImageResource(R.drawable.word);
        }
        if (cv.endsWith(".pdf")) {
            cvImage.setImageResource(R.drawable.pdf);
        }
    }

    private void openFileChooser() {
        aQuery.id(R.id.layout_add_cv).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_OODE_CV);
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
            if (requestCode == RESULT_OODE_CV) {
                ArrayList<String> docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                cvPath = docPaths.get(0);
                File cvFile = new File(cvPath);
                cvName = cvFile.getName();


                if (cvName.endsWith(".doc")) {
                    cvImage.setImageResource(R.drawable.word);
                }
                if (cvName.endsWith(".pdf")) {
                    cvImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_cv).text(cvName);
                editor.putString(Constant.CV_URI, cvName).commit();
                editor.putString(Constant.PATH_CV, cvPath).commit();


            }
        }
    }

}