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

public class DocumentActivity extends AppCompatActivity {
    AQuery aQuery;
    ImageView resultSlipImage, cvImage, reccommendationImage, lcLetterImage;
    private static final int RESULT_CODE_SLIP = 1;
    public static final int RESULT_OODE_CV = 2;
    public static final int RESULT_CODE_RECOMMENDATON = 3;
    public static final int RESULT_CODE_LC1_LETTER = 4;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String resultSlipPath, cvPath, recommendationPath, lc1letterPath;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;
    String lcFileName, resultSlipName, recommendationName, cvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        resultSlipImage = (ImageView) findViewById(R.id.file_slip);
        cvImage = (ImageView) findViewById(R.id.file_cv);
        reccommendationImage = (ImageView) findViewById(R.id.file_recommendation);
        lcLetterImage = (ImageView) findViewById(R.id.file_lc1_letter);
        service = RetrofitBuilder.createService(ApiService.class);
        openFileChooser();
        setDocumentUploaded();
        goToNext();
        goToNextFragment();
        goToPreviousFragment();
    }


    private void goToNext() {
        aQuery.id(R.id.btn_next).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aQuery.id(R.id.txt_cv).text() == "") {
                    aQuery.toast(getString(R.string.error_cv));
                    return;
                }
                if (aQuery.id(R.id.txt_recommendation).text() == "") {
                    aQuery.toast(getString(R.string.error_recommendation));
                    return;
                }
                if (aQuery.id(R.id.txt_resultslip).text() == "") {
                    aQuery.toast(getString(R.string.error_result_slip));
                    return;
                }
                if (aQuery.id(R.id.txt_lc1_letter).text() == "") {
                    aQuery.toast(getString(R.string.error_lc1));
                    return;
                }

                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                String pathCv = pref.getString(Constant.PATH_CV, "");
                String pathResultSlip = pref.getString(Constant.PATH_RESULT_SLIP, "");
                String pathReccommedation = pref.getString(Constant.PATH_RECCOMMENDATION, "");
                String pathLc1 = pref.getString(Constant.PATH_LC1_LETTER, "");

                File cvFile = new File(pathCv);
                File resultSlipFile = new File(pathResultSlip);
                File recommendationFile = new File(pathReccommedation);
                File lcLetterFile = new File(pathLc1);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                RequestBody requestBodyCv = RequestBody.create(MediaType.parse("multipart/form-data"), cvFile);
                builder.addFormDataPart("curriculum_vitea", cvFile.getName(), requestBodyCv);

                RequestBody requestBodyRecommendation = RequestBody.create(MediaType.parse("multipart/form-data"), recommendationFile);
                builder.addFormDataPart("recomendation_letter", recommendationFile.getName(), requestBodyRecommendation);

                RequestBody requestBodyResultSlip = RequestBody.create(MediaType.parse("multipart/form-data"), resultSlipFile);
                builder.addFormDataPart("result_slip", resultSlipFile.getName(), requestBodyResultSlip);

                RequestBody requestBodylc1 = RequestBody.create(MediaType.parse("multipart/form-data"), lcLetterFile);
                builder.addFormDataPart("lc_one_letter", lcLetterFile.getName(), requestBodylc1);

                builder.addFormDataPart("telephone", telephone);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
                kProgressHUD = KProgressHUD.create(DocumentActivity.this)
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

    private void goToNextFragment() {
        aQuery.id(R.id.next_fragment_document).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(ConfirmApplicaitonActivity.class);
                finish();
            }
        });
    }

    private void goToPreviousFragment() {
        aQuery.id(R.id.previous_fragment_document).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(NationalityActivity.class);
                finish();
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

        String cv = pref.getString(Constant.CV_URI, "");
        aQuery.id(R.id.txt_cv).text(cv);

        if (cv.endsWith(".doc")) {
            cvImage.setImageResource(R.drawable.word);
        }
        if (cv.endsWith(".pdf")) {
            cvImage.setImageResource(R.drawable.pdf);
        }

        String recommendationLetter = pref.getString(Constant.RECOMMENDATION_LETTER_URI, "");
        aQuery.id(R.id.txt_recommendation).text(recommendationLetter);

        if (recommendationLetter.endsWith(".doc")) {
            reccommendationImage.setImageResource(R.drawable.word);
        }
        if (recommendationLetter.endsWith(".pdf")) {
            reccommendationImage.setImageResource(R.drawable.pdf);
        }

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
        aQuery.id(R.id.layout_add_result_slip).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_CODE_SLIP);
            }
        });
        aQuery.id(R.id.layout_add_cv).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_OODE_CV);
            }
        });

        aQuery.id(R.id.layout_add_recommendation_letter).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(RESULT_CODE_RECOMMENDATON);
            }
        });
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
            if (requestCode == RESULT_CODE_RECOMMENDATON) {
                ArrayList<String> docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                recommendationPath = docPaths.get(0);

                File recommendationFile = new File(recommendationPath);
                recommendationName = recommendationFile.getName();


                if (recommendationName.endsWith(".doc")) {
                    reccommendationImage.setImageResource(R.drawable.word);
                }
                if (recommendationName.endsWith(".pdf")) {
                    reccommendationImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_recommendation).text(recommendationName);
                editor.putString(Constant.RECOMMENDATION_LETTER_URI, recommendationName).commit();
                editor.putString(Constant.PATH_RECCOMMENDATION, recommendationPath).commit();

            }
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
