package com.matchstick.brightlife.entities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.aquery.AQuery;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ToCheckoutLaterLogic extends AppCompatActivity {
    AQuery aQuery;
    ImageView resultSlipImage, cvImage, reccommendationImage, lcLetterImage;
    private static final int RESULT_CODE_SLIP = 1;
    public static final int RESULT_OODE_CV = 2;
    public static final int RESULT_CODE_RECOMMENDATON = 3;
    public static final int RESULT_CODE_LC1_LETTER = 4;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String resultSlipPath;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;
    Uri cvUri, resultSlipUri, recommendationUri, lcLetterUri;
    String lcFileName, resultSlipName, recommendationName, cvName;
    HashMap<String, String> files = new HashMap<>();
    ArrayList<Map.Entry<String, String>> entries;

    int currentUploadIndex = 0;

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

        files.put("slip", "/data/data/file.pdf");
        files.put("cv", "/data/data/file.pdf");
        files.put("amother", "/data/data/file.pdf");
    }

    private void processFiles(){
        currentUploadIndex = 0;

        entries = new ArrayList<>();
        for(Map.Entry<String, String> entry: files.entrySet()){
            entries.add(entry);
        }

        postNextFile();
    }

    private void postNextFile(){
        Map.Entry<String, String> entry = entries.get(currentUploadIndex);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        File f = new File(entry.getValue());

        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        builder.addFormDataPart(entry.getKey(), f.getName(), body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //aQuery.openFromRight(this);
                    kProgressHUD.dismiss();

                    currentUploadIndex++;
                    if(currentUploadIndex < entries.size()){
                        postNextFile();
                    }
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
                    postNextFile();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                postNextFile();
            }
        });
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

                //File cvFile = new File(cvUri.getPath());
                File resultSlipFile = new File(resultSlipPath);
//                File recommendationFile = new File( recommendationUri.getPath());
//                File lcLetterFile = new File(lcLetterUri.getPath());

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
//
//                RequestBody requestBodyCv = RequestBody.create(MediaType.parse("multipart/form-data"), cvFile);
//                builder.addFormDataPart("curriculum_vitea", cvFile.getName(), requestBodyCv);

//                RequestBody requestBodyRecommendation = RequestBody.create(MediaType.parse("multipart/form-data"), recommendationFile);
//                builder.addFormDataPart("recomendation_letter", recommendationFile.getName(), requestBodyRecommendation);
//
                RequestBody requestBodyResultSlip = RequestBody.create(MediaType.parse("multipart/form-data"), resultSlipFile);
                builder.addFormDataPart("result_slip", resultSlipFile.getName(), requestBodyResultSlip);
//
//                RequestBody requestBodylc1 = RequestBody.create(MediaType.parse("multipart/form-data"), lcLetterFile);
//                builder.addFormDataPart("lc_one_letter", lcLetterFile.getName(), requestBodylc1);

                builder.addFormDataPart("telephone", telephone);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
//                kProgressHUD = KProgressHUD.create(DocumentActivity.this)
//                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                        .setLabel("Please wait...")
//                        .setCancellable(true)
//                        .setAnimationSpeed(2)
//                        .setDimAmount(0.5f)
//                        .show();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            //aQuery.openFromRight(DocumentActivity.class);
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

                    }
                });

            }
        });
    }

    private void goToNextFragment() {
        aQuery.id(R.id.next_fragment_document).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // aQuery.openFromRight(ConfirmApplicaitonActivity.class);
                finish();
            }
        });
    }

    private void goToPreviousFragment() {
        aQuery.id(R.id.previous_fragment_document).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //aQuery.openFromRight(NationalityActivity.class);
                finish();
            }
        });
    }

    private void setDocumentUploaded() {
        String slip = pref.getString(Constant.RESULT_SLIP_URI, "");
        aQuery.id(R.id.txt_resultslip).text(slip);

        String slipExt = pref.getString(Constant.SLIP_EXTENSION, "");
        if (slipExt.startsWith("d")) {
            resultSlipImage.setImageResource(R.drawable.word);
        }
        if (slipExt.startsWith("p")) {
            resultSlipImage.setImageResource(R.drawable.pdf);
        }

        String cv = pref.getString(Constant.CV_URI, "");
        aQuery.id(R.id.txt_cv).text(cv);
        String cvExt = pref.getString(Constant.CV_EXTENSION, "");
        if (cvExt.startsWith("d")) {
            cvImage.setImageResource(R.drawable.word);
        }
        if (cvExt.startsWith("p")) {
            cvImage.setImageResource(R.drawable.pdf);
        }

        String recommendationLetter = pref.getString(Constant.RECOMMENDATION_LETTER_URI, "");
        aQuery.id(R.id.txt_recommendation).text(recommendationLetter);
        String recommendationExt = pref.getString(Constant.RECOMMENDATION_EXTENSION, "");
        if (recommendationExt.startsWith("d")) {
            reccommendationImage.setImageResource(R.drawable.word);
        }
        if (recommendationExt.startsWith("p")) {
            reccommendationImage.setImageResource(R.drawable.pdf);
        }

        String lc1Letter = pref.getString(Constant.LC1_LETTER_URI, "");
        aQuery.id(R.id.txt_lc1_letter).text(lc1Letter);
        String lcExt = pref.getString(Constant.LC_EXTENSION, "");
        if (lcExt.startsWith("d")) {
            lcLetterImage.setImageResource(R.drawable.word);
        }
        if (lcExt.startsWith("p")) {
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
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    requestCodeReturn);
        } catch (android.content.ActivityNotFoundException ex) {
            aQuery.toast("Please install a File Manager.");
        }*/

        FilePickerBuilder.getInstance().setMaxCount(1)
                .pickFile(this, requestCodeReturn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE_SLIP) {
                /*resultSlipUri = data.getData();
                AppUtils.log(resultSlipUri.toString());
                resultSlipName = getFileName(resultSlipUri);
                File myFile = new File(resultSlipUri.toString());
                resultSlipPath = myFile.getPath();

                AppUtils.log("RESULT SLIP PATH =>" + getRealPathFromURI(DocumentActivity.this, resultSlipUri));

                String fileExtension = getMimeType(this, resultSlipUri);
                if (fileExtension.startsWith("d")) {
                    resultSlipImage.setImageResource(R.drawable.word);
                }
                if (fileExtension.startsWith("p")) {
                    resultSlipImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_resultslip).text(resultSlipName);
                editor.putString(Constant.RESULT_SLIP_PATH, resultSlipPath).commit();
                editor.putString(Constant.RESULT_SLIP_URI, resultSlipName).commit();
                editor.putString(Constant.SLIP_EXTENSION, fileExtension).commit();*/

                ArrayList<String> docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                resultSlipPath = docPaths.get(0);
            }
            if (requestCode == RESULT_OODE_CV) {
                cvUri = data.getData();
                AppUtils.log(cvUri.toString());
                cvName = getFileName(cvUri);
                String cvExtension = getMimeType(this, cvUri);
                if (cvExtension.startsWith("d")) {
                    cvImage.setImageResource(R.drawable.word);
                }
                if (cvExtension.startsWith("p")) {
                    cvImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_cv).text(cvName);
                editor.putString(Constant.CV_URI, cvName).commit();
                editor.putString(Constant.CV_EXTENSION, cvExtension).commit();

            }
            if (requestCode == RESULT_CODE_RECOMMENDATON) {
                recommendationUri = data.getData();
                AppUtils.log(recommendationUri.toString());
                recommendationName = getFileName(recommendationUri);
                String recommendationExtension = getMimeType(this, recommendationUri);
                if (recommendationExtension.startsWith("d")) {
                    reccommendationImage.setImageResource(R.drawable.word);
                }
                if (recommendationExtension.startsWith("p")) {
                    reccommendationImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_recommendation).text(recommendationName);
                editor.putString(Constant.RECOMMENDATION_LETTER_URI, recommendationName).commit();
                editor.putString(Constant.RECOMMENDATION_EXTENSION, recommendationExtension).commit();
            }
            if (requestCode == RESULT_CODE_LC1_LETTER) {
                lcLetterUri = data.getData();
                AppUtils.log(lcLetterUri.toString());
                lcFileName = getFileName(lcLetterUri);
                String lcLetterExtension = getMimeType(this, lcLetterUri);
                if (lcLetterExtension.startsWith("d")) {
                    lcLetterImage.setImageResource(R.drawable.word);
                }
                if (lcLetterExtension.startsWith("p")) {
                    lcLetterImage.setImageResource(R.drawable.pdf);
                }
                aQuery.id(R.id.txt_lc1_letter).text(lcFileName);
                editor.putString(Constant.LC1_LETTER_URI, lcFileName).commit();
                AppUtils.log(lcLetterExtension);
                editor.putString(Constant.LC_EXTENSION, lcLetterExtension).commit();

            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    for(int i = 0; i < cursor.getColumnCount(); i++){
                        AppUtils.log("Column -> " + i + " : " + cursor.getString(i));
                    }

                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

}
