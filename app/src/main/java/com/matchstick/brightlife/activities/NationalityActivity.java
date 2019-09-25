package com.matchstick.brightlife.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.aquery.AQuery;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.BuildConfig;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.AppUtils;
import com.matchstick.brightlife.entities.Constant;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;
import com.matchstick.brightlife.utils.FileHelper;
import com.matchstick.brightlife.utils.ImageFilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class NationalityActivity extends AppCompatActivity {
    AQuery aQuery;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RadioButton nationalId, passport;
    File photoFile, frontImageFIle, backImageFIle;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    public static final int REQUEST_CODE_FRONT = 2;
    public static final int REQUEST_CODE_BACK_IMAGE = 5;
    public static final int REQUEST_CODE_PASSPORT_IMAGE = 10;
    private static final int PERMISSION_CODE = 1001;
    private static final int PERMISSION_CODE_CAMMERA = 1000;
    ImageView imageViewNationalIdFrontImage, imageViewNatinonalIdBackImage, imageViewPassportImage, selfieImageView;
    Uri nationalIdFrontImageUri, nationalIdBackImageUri, passportImageUri, selfieUri;
    Bitmap bitmap, bitmapBack, passportBitmap, selfieBitmap;
    String frontImageName, backImageName, selfieImageName, passportImageName;
    private String mCurrentPhotoPath = "";
    AwesomeValidation validator;
    String nationalIdNumber, passportNumber;
    Call<ResponseBody> call;
    ApiService service;
    boolean radioNationalId;
    String identifcationType, pathToPhotoTaken, selfiePath;
    KProgressHUD kProgressHUD;
    File storageDir;
    private Uri takenPhotoUri;
    private String PROFILE_ID_IMAGE_NAME;
    private String DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brightlife/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationality);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        imageViewNatinonalIdBackImage = (ImageView) findViewById(R.id.id_image_back);
        imageViewNationalIdFrontImage = (ImageView) findViewById(R.id.image_id_front);
        imageViewPassportImage = (ImageView) findViewById(R.id.image_passport);
        selfieImageView = (ImageView) findViewById(R.id.image_selfie);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        nationalId = (RadioButton) findViewById(R.id.radio_national_id);
        passport = (RadioButton) findViewById(R.id.radio_passport);
        service = RetrofitBuilder.createService(ApiService.class);

        AppUtils.log("SELFIE-IMAGE => " + pref.getString(Constant.SELFIE_URI, ""));

        setNationalityData();
        setInitialCheckRadio();
        pickImageFromGallery();
        goToNext();
        goToNextFragment();
        goToPreviousFragment();
        startCamera();

        ///set images if any
        imageViewNationalIdFrontImage.setImageBitmap(BitmapFactory.decodeFile(pref.getString(Constant.FRONT_ID_PHOTO_URI, "")));
        imageViewNatinonalIdBackImage.setImageBitmap(BitmapFactory.decodeFile(pref.getString(Constant.BACK_ID_PHOTO_URI, "")));
        imageViewPassportImage.setImageBitmap(BitmapFactory.decodeFile(pref.getString(Constant.PASSPORT_PHOTO_URI, "")));
        selfieImageView.setImageBitmap(BitmapFactory.decodeFile(pref.getString(Constant.SELFIE_URI, "")));



    }

    private void startCamera() {
        aQuery.id(R.id.image_camera).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
    }

    private void captureImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if (photoFile != null) {
                pathToPhotoTaken = photoFile.getAbsolutePath();
                File file = new File(pathToPhotoTaken);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                AppUtils.log("FILE SIZE => " + file_size);
                AppUtils.log("PATH TO PHOTO => " + pathToPhotoTaken);
                takenPhotoUri = FileProvider.getUriForFile(NationalityActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
                startActivityForResult(takePicture, CAPTURE_IMAGE_REQUEST);
            }

        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            AppUtils.log(e.toString());
        }
        return image;
    }


    private void setInitialCheckRadio() {
        nationalId.setChecked(true);
        aQuery.id(R.id.layout_national_id).show();
        aQuery.id(R.id.layout_passport).hide();
        nationalId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                aQuery.id(R.id.layout_national_id).hide();
                aQuery.id(R.id.layout_passport).show();
            }
        });
        passport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                aQuery.id(R.id.layout_national_id).show();
                aQuery.id(R.id.layout_passport).hide();
            }
        });
    }

    private void setNationalityData() {
        nationalIdNumber = pref.getString(Constant.NATIONAL_ID_CARD_NUMBER, "");
        aQuery.id(R.id.et_national_id_number).text(nationalIdNumber);

        passportNumber = pref.getString(Constant.PASSPORT_NUMBER, "");
        aQuery.id(R.id.et_passport_number).text(passportNumber);

        boolean passportRadio = pref.getBoolean(Constant.IDENTITY_PASSPORT, false);
        passport.setChecked(passportRadio);

        boolean radioNationalId = pref.getBoolean(Constant.IDENTITY_ID, false);
        passport.setChecked(radioNationalId);

        nationalIdFrontImageUri = Uri.parse(pref.getString(Constant.FRONT_ID_PHOTO_URI, ""));
        nationalIdBackImageUri = Uri.parse(pref.getString(Constant.BACK_ID_PHOTO_URI, ""));
        passportImageUri = Uri.parse(pref.getString(Constant.PASSPORT_PHOTO_URI, ""));
        selfiePath = pref.getString(Constant.SELFIE_URI, "");
        try {
            bitmap = AppUtils.scaleImage(this, nationalIdFrontImageUri);
            imageViewNationalIdFrontImage.setImageBitmap(bitmap);

            bitmapBack = AppUtils.scaleImage(this, nationalIdBackImageUri);
            imageViewNatinonalIdBackImage.setImageBitmap(bitmapBack);

            passportBitmap = AppUtils.scaleImage(this, passportImageUri);
            imageViewPassportImage.setImageBitmap(passportBitmap);

            Bitmap myBitmap = BitmapFactory.decodeFile(selfiePath);
            selfieImageView.setImageBitmap(myBitmap);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToNext() {
        aQuery.id(R.id.btn_confirm_nationality_data).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String frontImageName = pref.getString(Constant.FRONT_IMAGE_NAME, "");
                String backImageName = pref.getString(Constant.BACK_IMAGE_NAME, "");
                String passportImageName = pref.getString(Constant.PASSPORT_IMAGE_NAME, "");
                String passportNumber = aQuery.id(R.id.et_passport_number).text();

                if (nationalId.isChecked()) {
                    if (frontImageName.isEmpty()) {
                        aQuery.toast("upload a National ID front image");
                        return;
                    }
                    if (backImageName.isEmpty()) {
                        aQuery.toast("upload a National ID back image");
                        return;
                    }
                } else {
                    if (passportNumber.isEmpty()) {
                        aQuery.toast(getString(R.string.error_passport_number));
                    }
                    if (passportImageName.isEmpty()) {
                        aQuery.toast("upload a Passport image");
                        return;
                    }
                }

                if (nationalId.isChecked()) {

                    editor.putBoolean(Constant.IDENTITY_ID, true).commit();
                } else {
                    editor.putBoolean(Constant.IDENTITY_ID, false).commit();
                }

                if (passport.isChecked()) {
                    editor.putBoolean(Constant.IDENTITY_PASSPORT, true).commit();
                } else {
                    editor.putBoolean(Constant.IDENTITY_PASSPORT, false).commit();
                }
                String passport_number = aQuery.id(R.id.et_national_id_number).text();
                String national_id_no = aQuery.id(R.id.et_passport_number).text();

                editor.putString(Constant.NATIONAL_ID_CARD_NUMBER, passport_number).commit();
                editor.putString(Constant.PASSPORT_NUMBER, national_id_no).commit();

                if (nationalId.isChecked()) {
                    identifcationType = "NationalID";
                } else {
                    identifcationType = "Passport";
                }
                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                File frontFile = saveBitmapToFile(new File(getRealPathFromURI(NationalityActivity.this, nationalIdFrontImageUri)));
                File backFile = saveBitmapToFile(new File(getRealPathFromURI(NationalityActivity.this, nationalIdBackImageUri)));
                File selfieFile = new File(selfiePath);
                if (passport.isChecked()) {
                    File passportFile = saveBitmapToFile(new File(getRealPathFromURI(NationalityActivity.this, passportImageUri)));
                    RequestBody requestBodyPassport = RequestBody.create(MediaType.parse("multipart/form-data"), passportFile);
                    builder.addFormDataPart("passport_photo", passportFile.getName(), requestBodyPassport);
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), frontFile);
                builder.addFormDataPart("national_id_card_front", frontFile.getName(), requestBody);

//                    RequestBody requestBodySelfie = RequestBody.create(MediaType.parse("multipart/form-data"), selfieFile);
//                    builder.addFormDataPart("passport_photo", selfieFile.getName(), requestBodySelfie);

                RequestBody requestBodyBack = RequestBody.create(MediaType.parse("multipart/form-data"), backFile);
                builder.addFormDataPart("national_id_card_back", backFile.getName(), requestBodyBack);


                builder.addFormDataPart("telephone", telephone);
                builder.addFormDataPart("national_id_number", national_id_no);
                builder.addFormDataPart("passport_number", passport_number);
                builder.addFormDataPart("identification_type", identifcationType);
                RequestBody finalRequestBody = builder.build();


                call = service.uploadImages(finalRequestBody);
                AppUtils.log("API REQUEST => " + call.toString());
                kProgressHUD = KProgressHUD.create(NationalityActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            aQuery.openFromRight(ResultSlipActivity.class);
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
        aQuery.id(R.id.next_fragment_nationality).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(DocumentActivity.class);
                finish();
            }
        });
    }

    private void goToPreviousFragment() {
        aQuery.id(R.id.previous_fragment_nationality).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(NOKActivity.class);
                finish();
            }
        });
    }

    private void pickImageFromGallery() {
        aQuery.id(R.id.card_front_image_holder).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGalleryPermission(REQUEST_CODE_FRONT);

            }
        });
        aQuery.id(R.id.card_back_image_holder).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGalleryPermission(REQUEST_CODE_BACK_IMAGE);

            }
        });
        aQuery.id(R.id.card_passport_natioanlity).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGalleryPermission(REQUEST_CODE_PASSPORT_IMAGE);

            }
        });
    }

    private void checkGalleryPermission(int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission is not granted, so request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show the pop up for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);

            } else {
                //permission has already been granted
                openGallery(code);
            }
        } else {
            //the system os is not supported
            openGallery(code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // the permission was granted then open the gallery app
                    openGallery(REQUEST_CODE_FRONT);
                    openGallery(REQUEST_CODE_PASSPORT_IMAGE);
                } else {
                    // the permission was denied
                    aQuery.toast("Gallery Permission Denied");
                }
                break;

            case PERMISSION_CODE_CAMMERA:
                if (requestCode == 0) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        captureImage();
                    }
                } else {
                    aQuery.toast("Camera Permission Denied");
                }
        }
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        File externalStorage = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String photoPath = externalStorage.getPath();
        Uri parse = Uri.parse(photoPath);
        intent.setDataAndType(parse, "image/*");
        startActivityForResult(intent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FRONT) {


                //images not from camera
                Uri uri = data.getData();
                String path = ImageFilePath.getPath(NationalityActivity.this, uri);

                editor.putString(Constant.FRONT_ID_PHOTO_URI, path).commit();
                frontImageName = getFileName(uri);
                editor.putString(Constant.FRONT_IMAGE_NAME, frontImageName).commit();
                try {
                    bitmap = AppUtils.scaleImage(this, uri);
                    imageViewNationalIdFrontImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    //aQuery.toast("Cannot open image");
                }
            }
            if (requestCode == REQUEST_CODE_BACK_IMAGE) {


                //images not from camera
                Uri uri = data.getData();
                String path = ImageFilePath.getPath(NationalityActivity.this, uri);

                editor.putString(Constant.BACK_ID_PHOTO_URI, path).commit();
                backImageName = getFileName(uri);
                editor.putString(Constant.BACK_IMAGE_NAME, backImageName).commit();
                try {
                    bitmapBack = AppUtils.scaleImage(this, uri);
                    imageViewNatinonalIdBackImage.setImageBitmap(bitmapBack);
                } catch (Exception e) {
                    e.printStackTrace();
                    //aQuery.toast("Cannot open image");
                }
            }

            if (requestCode == REQUEST_CODE_PASSPORT_IMAGE) {


                Uri uri = data.getData();

                // this are images not from camera
                String path = ImageFilePath.getPath(NationalityActivity.this, uri);

                editor.putString(Constant.PASSPORT_PHOTO_URI, path).commit();
                passportImageName = getFileName(uri);
                editor.putString(Constant.PASSPORT_IMAGE_NAME, passportImageName).commit();
                try {
                    passportBitmap = AppUtils.scaleImage(this, uri);
                    imageViewPassportImage.setImageBitmap(passportBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    //aQuery.toast("Cannot open image");
                }
            }

            if (requestCode == CAPTURE_IMAGE_REQUEST) {

                //if is image from camera, do this
                String path = new FileHelper().compressImage(new File(pathToPhotoTaken).getAbsolutePath(), "selfie_image.png");

                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                editor.putString(Constant.SELFIE_URI, path).commit();
                selfieImageView.setImageBitmap(myBitmap);
            }
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
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
}
