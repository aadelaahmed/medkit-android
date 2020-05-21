package com.example.medkit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medkit.databinding.ActivityDoctorLicenseBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DoctorLicenseActivity extends AppCompatActivity {
    final String TAG = "license";
    private static final int REQUESCODE = 5;
    Bitmap bitmap = null;
    String encodedImage = null;
    String apiPath = "https://id-detect2.herokuapp.com/predict/";
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

            }
        });

        binding.btnContinueLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encodedImage == null) {
                    showMessage("please enter your license");
                } else {
                    String tempJson = "{ image : " + encodedImage + "}";
//                    new CallAPI().execute(apiPath, tempJson);
//                    makePostRequest(encodedImage);
                }

            }
        });
    }
//
//    public URL createUrl(String urlString) {
//        URL url = null;
//        if (urlString != null) {
//            try {
//                url = new URL(urlString);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        }
//        return url;
//    }


    //ahmed

    //ahmed

   /*public String makeHttpRequest (URL url) {
        String jsonResponse = "";
        HttpURLConnection httpURLConnection = null;
        InputStream in = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                in = httpURLConnection.getInputStream();
                jsonResponse = postLicense(in);
            } else {
                Log.e("MainActivity", "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonResponse;
    } */

/*
    public String postLicense (InputStream in) {



        BufferedReader bufferedReader;
        String data;
        StringBuffer buffer = new StringBuffer();
        if (in != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            try {
                while ((data = bufferedReader.readLine()) != null) {
                    buffer.append(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return buffer.toString();
    }*/

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

//    public class CallAPI extends AsyncTask<String, Void, Void> {
//
//        public CallAPI() {
//            //set context variables if required
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            String urlString = params[0]; // URL to call
//            String data = params[1]; //data to post
//            OutputStream out = null;
//            Log.d(TAG, "doInBackground: 1");

//            try {
//                URL url = createUrl(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();
//                Log.d(TAG, "doInBackground: "+urlString+data);
//                urlConnection.connect();
//            } catch (Exception e) {
//                Log.d("TAG", "EROOR WITH URL CONNECTION");
//            }
//            Log.d(TAG, "doInBackground: 2");
//
//            } catch (ClientProtocolException e) {
//                // Log exception
//                e.printStackTrace();
//            } catch (IOException e) {
//                // Log exception
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

}
