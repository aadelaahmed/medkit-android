package com.example.medkit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.databinding.ActivityDoctorLicenseBinding;
import com.example.medkit.model.Prediction;
import com.example.medkit.utils.APIInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DoctorLicenseActivity extends AppCompatActivity {
    final String TAG = "license";
    private static final int REQUESCODE = 5;
    Bitmap bitmap = null;
    String encodedImage = null;
    String apiPath = "https://id-detect.herokuapp.com/";
    private ActivityDoctorLicenseBinding binding;
    private Uri imageLicense = null;
    private int PReqCode = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorLicenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.activityClickLicese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23)
                    checkAndRequestForPermission();
                else
                    openGallery();
                binding.btnContinueLicense.setText("Send");
                binding.testTv.setVisibility(View.GONE);
                binding.testTv.setText("Please wait...");
                binding.testTv.setTextColor(Color.parseColor("#595754"));
            }
        });

        binding.btnContinueLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encodedImage == null) {
                    showMessage("please enter your license");
                } else {
                    binding.testTv.setVisibility(View.VISIBLE);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("image", encodedImage);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(apiPath).addConverterFactory(GsonConverterFactory.create()).build();
                    APIInterface apiInterface = retrofit.create(APIInterface.class);
                    Call<Prediction> call = apiInterface.getPrediction(map);
                    call.enqueue(new Callback<Prediction>() {
                        @Override
                        public void onResponse(Call<Prediction> call, Response<Prediction> response) {
                            Log.d(TAG, "onResponse: " + response.body().getPrediction());
                            if (response.body().getPrediction() == 1) {
                                binding.testTv.setText("License Detected");
                                binding.testTv.setTextColor(Color.parseColor("#00B900"));
                                binding.btnContinueLicense.setText("Contiue");
                                //TODO proceed to next step
                            } else {
                                binding.testTv.setText("No License Detected");
                                binding.testTv.setTextColor(Color.parseColor("#CC0000"));
                                binding.btnContinueLicense.setText("Send");
                            }
                        }
                        @Override
                        public void onFailure(Call<Prediction> call, Throwable t) {

                        }
                    });
                }

            }
        });
    }
  /*  private void updateUI() {
        //go to next activity
    }*/

    private void showMessage(String message) {
        Toast.makeText(DoctorLicenseActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(DoctorLicenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DoctorLicenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                showMessage("Please accept for required permission");

            } else {
                ActivityCompat.requestPermissions(DoctorLicenseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            imageLicense = data.getData();
            binding.selectedImg.setImageURI(imageLicense);
            Log.d(TAG, "onActivityResult: " + imageLicense);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageLicense);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Log.d(TAG, "onActivityResult: " + encodedImage);
            } catch (IOException e) {
                showMessage(e.getMessage());
            }
            //popupPostImage.setImageURI(pickedImgUri);

        }


    }

}
