package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.aquery.AQuery;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.AppUtils;
import com.matchstick.brightlife.entities.Constant;
import com.matchstick.brightlife.models.Branch;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BioDataActivity extends AppCompatActivity {
    AQuery aQuery;
    RadioButton mr, mrs, miss;
    CheckBox agree;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    KProgressHUD kProgressHUD;
    AwesomeValidation validator;
    Toolbar tb;
    Call<ResponseBody> call;
    Call<List<Branch>> branchCall;
    ApiService service;
    Spinner locationSpinner;
    int branchId;
    String selectedBranch;
    String firstName, middleName, lastName, email, formatedPhoneNumber,
            registeredTelephone, location, dob, alternativePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_data);
        aQuery = new AQuery(this);
        mr = (RadioButton) findViewById(R.id.radio_title_mr);
        mrs = (RadioButton) findViewById(R.id.radio_title_mrs);
        miss = (RadioButton) findViewById(R.id.radio_title_miss);
        agree = (CheckBox) findViewById(R.id.agreement_checkbox);
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        editor = preferences.edit();
        service = RetrofitBuilder.createService(ApiService.class);
        locationSpinner = (Spinner) findViewById(R.id.et_location);

        tb = (Toolbar) findViewById(R.id.toolbar);

        setBioData();
        getDateOfBirth();
        postBioData();
        toolbarSetUp();
        setUpRules();
        goToNextFragment();
        populateLocationSpinner();
    }

    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Applicant Bio");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aQuery.openFromRight(RequirementActivity.class);
                    finish();
                }
            });
        }
    }

    private void populateLocationSpinner() {
        branchCall = service.getBranches();
        branchCall.enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(Call<List<Branch>> call, Response<List<Branch>> response) {
                if (response.isSuccessful()){
                    List<Branch> branchList = response.body();
                    final ArrayList<String> branchArrayList = new ArrayList<>();
                    final ArrayList<Integer> idArrayList = new ArrayList<>();
                    for (Branch branch: branchList){
                        if (!branchArrayList.contains(branch.getName())){
                            branchArrayList.add(branch.getName());
                            idArrayList.add(branch.getId());
                        }
                    }


                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(BioDataActivity.this, R.layout.custom_location_spinner, branchArrayList);
                    locationSpinner.setAdapter(arrayAdapter);
                    location = locationSpinner.getSelectedItem().toString();
                    locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            branchId = idArrayList.get(i);
                            selectedBranch = branchArrayList.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {

            }
        });
    }

    private void goToNextFragment() {
        aQuery.id(R.id.next_fragment).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = aQuery.id(R.id.et_first_name).text();
                String dob = aQuery.id(R.id.et_dob).text();
                if (firstName.isEmpty() || dob.isEmpty()){
                    aQuery.toast("Missing fields, please fill in all the fields");
                    return;
                }else{
                    aQuery.openFromRight(NOKActivity.class);
                    finish();
                }
            }
        });
    }

    //This stores the bio data entered in the shared preference
    private void storeBioData() {
        editor.putString(Constant.FIRST_NAME, aQuery.id(R.id.et_first_name).text()).commit();
        editor.putString(Constant.MIDDLE_NAME, aQuery.id(R.id.et_middle_name).text()).commit();
        editor.putString(Constant.LAST_NAME, aQuery.id(R.id.et_last_name).text()).commit();
        editor.putString(Constant.EMAIL, aQuery.id(R.id.et_email).text()).commit();
        editor.putString(Constant.REGISTERED_TELEPHONE, aQuery.id(R.id.et_phone_number).text()).commit();
        editor.putString(Constant.LOCATION, aQuery.id(R.id.et_location).text()).commit();
        editor.putString(Constant.ALTERNATIVE_TELEPHONE, aQuery.id(R.id.et_other_phone_number).text()).commit();
        editor.putString(Constant.DATE_OF_BIRTH, aQuery.id(R.id.et_dob).text()).commit();
        editor.putString(Constant.LOCATION, selectedBranch).commit();
        if (mr.isChecked()) {
            editor.putBoolean(Constant.TITLE_MR, true).commit();
        } else {
            editor.putBoolean(Constant.TITLE_MR, false).commit();
        }

        if (mrs.isChecked()) {

            editor.putBoolean(Constant.TITLE_MRS, true).commit();
        } else {
            editor.putBoolean(Constant.TITLE_MRS, false).commit();
        }

        if (miss.isChecked()) {
            editor.putBoolean(Constant.TITLE_MISS, true).commit();
        } else {
            editor.putBoolean(Constant.TITLE_MISS, false).commit();
        }

//        if (agree.isChecked()) {
//            editor.putBoolean(Constant.AGREE, true).commit();
//        } else {
//            editor.putBoolean(Constant.AGREE, false).commit();
//        }
    }

    //Picks the data from the shared preference and sets it on the respective views
    private void setBioData() {
        firstName = preferences.getString(Constant.FIRST_NAME, "");
        aQuery.id(R.id.et_first_name).text(firstName);

        middleName = preferences.getString(Constant.MIDDLE_NAME, "");
        aQuery.id(R.id.et_middle_name).text(middleName);

        lastName = preferences.getString(Constant.LAST_NAME, "");
        aQuery.id(R.id.et_last_name).text(lastName);

        email = preferences.getString(Constant.EMAIL, "");
        aQuery.id(R.id.et_email).text(email);

        location = preferences.getString(Constant.LOCATION, "");
        aQuery.id(R.id.et_location).text(location);

        registeredTelephone = preferences.getString(Constant.REGISTERED_TELEPHONE, "");
//        AppUtils.log("PHONE NUMBER => " + registeredTelephone);
        if (registeredTelephone.startsWith("0")){
            formatedPhoneNumber = registeredTelephone.replaceFirst("0", "256");
        }else if (registeredTelephone.startsWith("+")){
            formatedPhoneNumber = registeredTelephone.replace("+", "");
        }
        aQuery.id(R.id.et_phone_number).text(registeredTelephone);
        AppUtils.log("PHONE NUMBER =>" + registeredTelephone);

        alternativePhone = preferences.getString(Constant.ALTERNATIVE_TELEPHONE, "");
        aQuery.id(R.id.et_other_phone_number).text(alternativePhone);

        dob = preferences.getString(Constant.DATE_OF_BIRTH, "");
        aQuery.id(R.id.et_dob).text(dob);

        boolean radioMr = preferences.getBoolean(Constant.TITLE_MR, false);
        mr.setChecked(radioMr);

        boolean radioMrs = preferences.getBoolean(Constant.TITLE_MRS, false);
        mrs.setChecked(radioMrs);

        boolean radioMiss = preferences.getBoolean(Constant.TITLE_MISS, false);
        miss.setChecked(radioMiss);

    }


    private void getDateOfBirth() {
        aQuery.id(R.id.et_dob).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(BioDataActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, onDateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        aQuery.id(R.id.et_dob).text(month + "/" + day + "/" + year);
                    }
                };
            }
        });
    }

    private void postBioData() {
        aQuery.id(R.id.btn_save_biodata).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validator.validate()) {
                    storeBioData();
                    String firstName = aQuery.id(R.id.et_first_name).text();
                    String lastName  = aQuery.id(R.id.et_last_name).text();
                    String middleName = aQuery.id(R.id.et_middle_name).text();
                    String email = aQuery.id(R.id.et_email).text();
                    String dob = aQuery.id(R.id.et_dob).text();
                    String alternativePhone =  aQuery.id(R.id.et_other_phone_number).text();
                    String registeredTelephone = aQuery.id(R.id.et_phone_number).text();

                    if (registeredTelephone.startsWith("0")){
                        registeredTelephone = registeredTelephone.replaceFirst("0", "256");
                    }else if (registeredTelephone.startsWith("+")){
                        registeredTelephone = registeredTelephone.replace("+", "");
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("first_name", firstName);
                    jsonObject.addProperty("last_name", lastName);
                    jsonObject.addProperty("middle_name", middleName);
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("location", location);
                    jsonObject.addProperty("date_of_birth", dob);
                    jsonObject.addProperty("alternative_number", alternativePhone);
                    jsonObject.addProperty("telephone", registeredTelephone);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    call = service.postApplication(requestBody);
                    kProgressHUD = KProgressHUD.create(BioDataActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Saving data...")
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                aQuery.openFromRight(NOKActivity.class);
                                finish();
                                kProgressHUD.dismiss();
                            }else{
                                switch (response.code()){
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
                            kProgressHUD.dismiss();
                            aQuery.toast(getString(R.string.network_error));
                        }
                    });
                }
            }
        });
    }

    public void setUpRules() {
        validator.addValidation(this, R.id.et_first_name, RegexTemplate.NOT_EMPTY, R.string.error_first_name);
        validator.addValidation(this, R.id.et_last_name, RegexTemplate.NOT_EMPTY, R.string.error_last_name);
        validator.addValidation(this, R.id.et_email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        validator.addValidation(this, R.id.et_phone_number, RegexTemplate.NOT_EMPTY, R.string.error_phone_number);
        validator.addValidation(this, R.id.et_dob, RegexTemplate.NOT_EMPTY, R.string.error_dob);
        validator.addValidation(this, R.id.et_location, RegexTemplate.NOT_EMPTY, R.string.error_location);
    }

    //+971562185708  //0774110345 JAMES DUBAI
}
