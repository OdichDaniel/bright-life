package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aquery.AQuery;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.AppUtils;
import com.matchstick.brightlife.entities.Constant;
import com.matchstick.brightlife.models.VerificationStatus;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmApplicaitonActivity extends AppCompatActivity {
    AQuery aQuery;
    Call<VerificationStatus> call;
    ApiService service;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ImageView imageViewNationalIdFrontImage, imageViewNatinonalIdBackImage, imageViewPassportImage, selfieImageView;
    Uri nationalIdFrontImageUri, nationalIdBackImageUri, passportImageUri, selfieUri;
    Bitmap bitmap, bitmapBack, passportBitmap, bitmapSelfie;
    ImageView resultSlipImage, cvImage, reccommendationImage, lcLetterImage;

    String resultSlipPath;

    String firstName, lastName, middleName, email, dob, location, registerdPhoneNumber, alternativePhoneNumber,
            NOKFirstName, NOKLastName, NOKEmail, NOKPhoneNumber, nationalIdNumber, passportNumber, slip, cv,
            recommendationLetter, lc1Letter;
    KProgressHUD kProgressHUD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_applicaiton);
        aQuery = new AQuery(this);
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        imageViewNatinonalIdBackImage = (ImageView) findViewById(R.id.pref_image_back);
        imageViewNationalIdFrontImage = (ImageView) findViewById(R.id.pref_image_front);
        imageViewPassportImage = (ImageView) findViewById(R.id.pref_image_passport);
        selfieImageView = (ImageView) findViewById(R.id.pref_image_selfie);
        resultSlipImage = (ImageView) findViewById(R.id.file_slip);
        cvImage = (ImageView) findViewById(R.id.file_cv);
        reccommendationImage = (ImageView)findViewById(R.id.file_recommendation);
        lcLetterImage = (ImageView) findViewById(R.id.file_lc1_letter);
        service = RetrofitBuilder.createService(ApiService.class);

        setBioData();
        setNextOfKinData();
        setNationalityData();
        setDocumentUploaded();
        checkApplicationStatus();
        goToPreviousFragment();
    }

    private void setBioData() {
        firstName = preferences.getString(Constant.FIRST_NAME, "");
        if (firstName.isEmpty()) {
            aQuery.id(R.id.missing_first_name).text("First name missing").show();
        }
        aQuery.id(R.id.txt_first_name).text(firstName);


        middleName = preferences.getString(Constant.MIDDLE_NAME, "");
        aQuery.id(R.id.txt_middle_name).text(middleName);

        lastName = preferences.getString(Constant.LAST_NAME, "");
        if (lastName.isEmpty()) {
            aQuery.id(R.id.missing_last_name).text("Last name missing").show();
        }
        aQuery.id(R.id.txt_last_name).text(lastName);

        email = preferences.getString(Constant.EMAIL, "");
        if (email.isEmpty()) {
            aQuery.id(R.id.missing_email).text("Email missing").show();
        }
        aQuery.id(R.id.txt_email).text(email);

        registerdPhoneNumber = preferences.getString(Constant.REGISTERED_TELEPHONE, "");
        if (registerdPhoneNumber.isEmpty()) {
            aQuery.id(R.id.missing_phone_number).text("Phone number missing").show();
        }
        aQuery.id(R.id.txt_phone_number).text(registerdPhoneNumber);

        alternativePhoneNumber = preferences.getString(Constant.ALTERNATIVE_TELEPHONE, "");
        aQuery.id(R.id.txt_alternative_number).text(alternativePhoneNumber);

        dob = preferences.getString(Constant.DATE_OF_BIRTH, "");
        if (dob.isEmpty()) {
            aQuery.id(R.id.missing_dob).text("Date of birth missing").show();
        }
        aQuery.id(R.id.txt_d_o_b).text(dob);

        location = preferences.getString(Constant.LOCATION, "");
        if (location.isEmpty()) {
            aQuery.id(R.id.missing_location).text("Location missing").show();
        }
        aQuery.id(R.id.txt_location).text(location);

        boolean radioMr = preferences.getBoolean(Constant.TITLE_MR, false);
        if (radioMr)
            aQuery.id(R.id.txt_title).text("Mr");


        boolean radioMrs = preferences.getBoolean(Constant.TITLE_MRS, false);
        if (radioMrs)
            aQuery.id(R.id.txt_title).text("Mrs");

        boolean radioMiss = preferences.getBoolean(Constant.TITLE_MISS, false);
        if (radioMiss)
            aQuery.id(R.id.txt_title).text("Miss");

    }

    //Picks the data from the shared preference and sets it on the respective views
    private void setNextOfKinData() {
        NOKFirstName = preferences.getString(Constant.NOK_FIST_NAME, "");
        if (NOKFirstName.isEmpty()) {
            aQuery.id(R.id.missing_nok_first_name).text("First name missing").show();
        }
        aQuery.id(R.id.txt_nok_first_name).text(NOKFirstName);

        NOKLastName = preferences.getString(Constant.NOK_LAST_NAME, "");
        if (NOKLastName.isEmpty()) {
            aQuery.id(R.id.missing_nok_last_name).text("Last name missing").show();
        }
        aQuery.id(R.id.txt_nok_last_name).text(NOKLastName);

        NOKEmail = preferences.getString(Constant.NOK_EMAIL, "");
        aQuery.id(R.id.txt_nok_email).text(NOKEmail);

        NOKPhoneNumber = preferences.getString(Constant.NOK_TELEPHONE, "");
        if (NOKPhoneNumber.isEmpty()) {
            aQuery.id(R.id.missing_nok_phone).text("Telephone number missing").show();
        }
        aQuery.id(R.id.txt_nok_phone_number).text(NOKPhoneNumber);
    }

    private void setNationalityData() {
        boolean passportRadio = preferences.getBoolean(Constant.IDENTITY_PASSPORT, false);
        if (passportRadio) {
            aQuery.id(R.id.txt_national_id_number).hide();
            passportNumber = preferences.getString(Constant.PASSPORT_NUMBER, "");
            aQuery.id(R.id.txt_passport_number).text(passportNumber);
            aQuery.id(R.id.card_front_image).hide();
            aQuery.id(R.id.card_back_image).hide();
            aQuery.id(R.id.card_selfie).hide();
            aQuery.id(R.id.txt_national_id_number).hide();
            aQuery.id(R.id.nin_holder).hide();
            if (passportNumber.isEmpty()) {

                aQuery.id(R.id.missing_NIN).text("Passport number missing").show();
            }
        }

        boolean radioNationalId = preferences.getBoolean(Constant.IDENTITY_ID, false);
        if (radioNationalId) {
            nationalIdNumber = preferences.getString(Constant.NATIONAL_ID_CARD_NUMBER, "");
            aQuery.id(R.id.txt_national_id_number).text(nationalIdNumber);
            aQuery.id(R.id.card_passport).hide();
            aQuery.id(R.id.passport_holder).hide();
            aQuery.id(R.id.txt_passport_number).hide();
            if (nationalIdNumber.isEmpty()) {

                aQuery.id(R.id.missing_NIN).text("National Identity number missing").show();
            }
        }


        nationalIdFrontImageUri = Uri.parse(preferences.getString(Constant.FRONT_ID_PHOTO_URI, ""));
        if (nationalIdFrontImageUri == null) {
            aQuery.id(R.id.missing_front_image).text("National ID front image missing").show();
        }

        nationalIdBackImageUri = Uri.parse(preferences.getString(Constant.BACK_ID_PHOTO_URI, ""));
        if (nationalIdBackImageUri == null) {
            aQuery.id(R.id.missing_back_image).text("National ID back image missing").show();
        }
        passportImageUri = Uri.parse(preferences.getString(Constant.PASSPORT_PHOTO_URI, ""));
        if (passportImageUri == null) {
            aQuery.id(R.id.missing_passport_image).text("Passport image missing").show();
        }

        selfieUri = Uri.parse(preferences.getString(Constant.SELFIE_URI, ""));
        if (selfieUri == null) {
            aQuery.id(R.id.missing_selfie).text("Your selfie is missing");
        }
        try {
            //editor.putString(Constant.IMAGE_URI, imageUri);
            bitmap = AppUtils.scaleImage(this, nationalIdFrontImageUri);
            imageViewNationalIdFrontImage.setImageBitmap(bitmap);

            bitmapBack = AppUtils.scaleImage(this, nationalIdBackImageUri);
            imageViewNatinonalIdBackImage.setImageBitmap(bitmapBack);

            passportBitmap = AppUtils.scaleImage(this, passportImageUri);
            imageViewPassportImage.setImageBitmap(passportBitmap);

            bitmapSelfie = AppUtils.scaleImage(this, selfieUri);
            selfieImageView.setImageBitmap(bitmapSelfie);

        } catch (Exception e) {
            e.printStackTrace();
            //aQuery.toast("Cannot open image");
        }
    }

    private void setDocumentUploaded() {
        resultSlipPath = preferences.getString(Constant.RESULT_SLIP_PATH, "");
        AppUtils.log("RESULT_SLIP_PATH => " + resultSlipPath );

        AppUtils.log("SELFIE-IMAGE => " + preferences.getString(Constant.CV_URI, ""));
        slip = preferences.getString(Constant.RESULT_SLIP_URI, "");
        AppUtils.log("SLIP PREF => " + slip);
        if (slip.isEmpty()) {
            aQuery.id(R.id.missing_result_slip).text("Result slip missing").show();
            return;
        }
        aQuery.id(R.id.pref_resultslip).text(slip);

        String slipExt = preferences.getString(Constant.SLIP_EXTENSION, "");
        AppUtils.log("SLIP EXTENSION => " + slipExt);
        if (slipExt.startsWith("d")) {
            resultSlipImage.setImageResource(R.drawable.word);
        }
        if (slip.startsWith("p")) {
            resultSlipImage.setImageResource(R.drawable.pdf);
        }

        cv = preferences.getString(Constant.CV_URI, "");
        AppUtils.log("CV PREF => " + cv);
        if (cv.isEmpty()) {
            aQuery.id(R.id.missing_cv).text("CV missing").show();
            return;
        }
        aQuery.id(R.id.pref_cv).text(cv);
        String cvExt = preferences.getString(Constant.CV_EXTENSION, "");
        if (cvExt.startsWith("d")) {
            cvImage.setImageResource(R.drawable.word);
        }
        if (cvExt.startsWith("p")) {
            cvImage.setImageResource(R.drawable.pdf);
        }

        recommendationLetter = preferences.getString(Constant.RECOMMENDATION_LETTER_URI, "");
        AppUtils.log("RECOMMENDATION LETTER PREF => " + recommendationLetter);
        if (recommendationLetter.isEmpty()) {
            aQuery.id(R.id.missing_recommendation_letter).text("Recommendation letter missing").show();
            return;
        }
        aQuery.id(R.id.pref_recommendation).text(recommendationLetter);
        String recommendationExt = preferences.getString(Constant.RECOMMENDATION_EXTENSION, "");
        if (recommendationExt.startsWith("d")) {
            reccommendationImage.setImageResource(R.drawable.word);
        }
        if (recommendationExt.startsWith("p")) {
            reccommendationImage.setImageResource(R.drawable.pdf);
        }

        lc1Letter = preferences.getString(Constant.LC1_LETTER_URI, "");
        AppUtils.log("LC1 LETTER PREF => " + lc1Letter);
        if (lc1Letter.isEmpty()) {
            aQuery.id(R.id.missing_lc1).text("LC1 letter missing").show();
            return;
        }
        aQuery.id(R.id.pref_lc1_letter).text(lc1Letter);
        String lcExt = preferences.getString(Constant.LC_EXTENSION, "");
        if (lcExt.startsWith("d")) {
            lcLetterImage.setImageResource(R.drawable.word);
        }
        if (lcExt.startsWith("p")) {
            lcLetterImage.setImageResource(R.drawable.pdf);
        }

    }

    private void goToPreviousFragment() {
        aQuery.id(R.id.previous_fragment_confirmation).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(DocumentActivity.class);
            }
        });
    }

    private void checkApplicationStatus(){
        String firstName = preferences.getString(Constant.FIRST_NAME, "");
        String lastName = preferences.getString(Constant.LAST_NAME, "");
        String email = preferences.getString(Constant.EMAIL, "");
        String  dob = preferences.getString(Constant.DATE_OF_BIRTH, "");
        String location = preferences.getString(Constant.LOCATION, "");
        String  NOKFirstName = preferences.getString(Constant.NOK_FIST_NAME, "");
        String NOKLastName = preferences.getString(Constant.NOK_LAST_NAME, "");
        String NOKPhoneNumber = preferences.getString(Constant.NOK_TELEPHONE, "");
        String  slip = preferences.getString(Constant.RESULT_SLIP_URI, "");
        String cv = preferences.getString(Constant.CV_URI, "");
        String recommendationLetter = preferences.getString(Constant.RECOMMENDATION_LETTER_URI, "");
        String lc1Letter = preferences.getString(Constant.LC1_LETTER_URI, "");

        if(firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty() ||
                email.isEmpty() || dob.isEmpty() || location.isEmpty() ||
         alternativePhoneNumber.isEmpty() || NOKFirstName.isEmpty() || NOKLastName.isEmpty() ||
                NOKEmail.isEmpty() || NOKPhoneNumber.isEmpty() || slip.isEmpty() || cv.isEmpty() ||
                recommendationLetter.isEmpty() || lc1Letter.isEmpty()){
            aQuery.id(R.id.confirm_data_holder).show();
            return;
        }else{
            call = service.checkVerificationStatus(registerdPhoneNumber);
            kProgressHUD = KProgressHUD.create(ConfirmApplicaitonActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Checking status ...")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            call.enqueue(new Callback<VerificationStatus>() {
                @Override
                public void onResponse(Call<VerificationStatus> call, Response<VerificationStatus> response) {
                    if (response.isSuccessful()){
                        if (response.body().getVerified() != null) {
                            if (response.body().getVerified()) {
                                aQuery.id(R.id.image_tick).show();
                                aQuery.id(R.id.txt_verified).show();
                                aQuery.id(R.id.btn_go_to_login).show();
                                aQuery.id(R.id.btn_go_to_login).click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        aQuery.open(LoginActivity.class);
                                    }
                                });
                                kProgressHUD.dismiss();
                            } else {
                                aQuery.id(R.id.txt_underverification).show();
                                kProgressHUD.dismiss();
                            }
                        }else {
                            aQuery.id(R.id.txt_underverification).show();
                            kProgressHUD.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerificationStatus> call, Throwable t) {
                    aQuery.toast(getString(R.string.network_error));
                    kProgressHUD.dismiss();
                }
            });
        }
    }
}
