package com.example.projektsklep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProductActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_PRODUCT_PRODUCT = "EXTRA_EDIT_PRODUCT_PRODUCT";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final String API_IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String API_IMGUR_CLIENT = "33b15e0c63d3a31";
    private String currentPhotoPath;
    private Product selectedProduct;
    private String imageLink;
    private ImageView imageView;
    private Button setImageButton;
    private Button saveImageButton;
    private TextInputLayout nameLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout priceLayout;
    private TextInputEditText priceEditText;
    private TextInputLayout descriptionLayout;
    private TextInputEditText descriptionEditText;
    private Button saveProductButton;
    private boolean imageUploaded = true;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Snackbar.make(findViewById(R.id.edit_product_layout),
                        getString(R.string.file_create_error), Snackbar.LENGTH_LONG);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.projektsklep.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.i("Projekt","I got here");
            }
        }
    }

    private void uploadImageToImgur() {
        File file = new File(currentPhotoPath);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/jpeg")))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + API_IMGUR_CLIENT)
                .method("POST", body)
                .url(API_IMGUR_UPLOAD_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if(response.code() == 200) {
                        JSONObject json = new JSONObject(response.body().string());
                        imageLink = json.getJSONObject("data").getString("id");
                        //selectedProduct.setImageUrl(imageLink);
                        imageUploaded = true;
                        if (file.exists()) {
                            file.delete();
                        }
                        Snackbar.make(findViewById(R.id.edit_product_layout),
                                getString(R.string.image_saved_successfully), Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        Snackbar.make(findViewById(R.id.edit_product_layout),
                                getString(R.string.image_saving_error), Snackbar.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*try {
            Response response = client.newCall(request).execute();
            int responseCode = response.code();
            Log.i("responseCode", Integer.toString(responseCode));
            if(response.code() == 200) {
                JSONObject json = new JSONObject(response.body().string());
                String imageLink = json.getJSONObject("LL").getString("data");
                Log.i("imageLink", imageLink);
                selectedProduct.setImageUrl(imageLink);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        imageView = findViewById(R.id.edit_product_image);
        setImageButton = findViewById(R.id.product_set_image_button);
        saveImageButton = findViewById(R.id.product_save_image_button);
        nameLayout = findViewById(R.id.edit_product_name_layout);
        nameEditText = findViewById(R.id.edit_product_name);
        priceLayout = findViewById(R.id.edit_product_price_layout);
        priceEditText = findViewById(R.id.edit_product_price);
        descriptionLayout = findViewById(R.id.edit_product_description_layout);
        descriptionEditText = findViewById(R.id.edit_product_description);
        saveProductButton = findViewById(R.id.edit_product_save);

        if (getIntent().hasExtra(EXTRA_EDIT_PRODUCT_PRODUCT)) {
            selectedProduct = (Product) getIntent().getSerializableExtra(EXTRA_EDIT_PRODUCT_PRODUCT);
            nameEditText.setText(selectedProduct.getName());
            priceEditText.setText(String.valueOf(selectedProduct.getPrice()));
            descriptionEditText.setText(selectedProduct.getDescription());
            if (selectedProduct.getImageUrl() != null && !selectedProduct.getImageUrl().trim().isEmpty()) {
                Picasso.get()
                        .load(MainActivity.IMAGE_URL_BASE + selectedProduct.getImageUrl() + ".jpg")
                        .placeholder(R.drawable.ic_baseline_image_24dp).into(imageView);
            }
            else {
                imageView.setImageResource(R.drawable.ic_baseline_image_24dp);
            }
        }
        else {
            imageView.setImageResource(R.drawable.ic_baseline_image_24dp);
        }

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(nameEditText.getText().toString().length() <= 0) {
                    nameLayout.setErrorEnabled(true);
                    nameEditText.setError(getString(R.string.required_field));
                }
                else {
                    nameLayout.setErrorEnabled(false);
                }
            }
        });

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("pl_PL"));
                //NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pl_PL"));
                /*if(!s.toString().equals(current)){
                    priceEditText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[ ,.zł]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    priceEditText.setText(formatted);
                    priceEditText.setSelection(formatted.length());

                    priceEditText.addTextChangedListener(this);
                }*/
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(priceEditText.getText().toString().length() <= 0) {
                    priceLayout.setErrorEnabled(true);
                    priceEditText.setError(getString(R.string.required_field));
                }
                else {
                    priceLayout.setErrorEnabled(false);
                }
            }
        });

        setImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUploaded == false) {
                    imageLink = null;
                    uploadImageToImgur();
                }
            }
        });

        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if(nameLayout.getError() == null && priceLayout.getError() == null) {
                    if(TextUtils.isEmpty(nameEditText.getText())
                            || TextUtils.isEmpty(priceEditText.getText())) {
                        setResult(RESULT_CANCELED, replyIntent);
                    }
                    else {
                        if (selectedProduct != null) {
                            selectedProduct.setName(nameEditText.getText().toString());
                            selectedProduct.setDescription(descriptionEditText.getText().toString());
                            selectedProduct.setPrice(Float.parseFloat(priceEditText.getText().toString()));
                        }
                        else {
                            selectedProduct = new Product(nameEditText.getText().toString(),
                                    descriptionEditText.getText().toString(),
                                    Float.parseFloat(priceEditText.getText().toString()));
                        }
                        if(imageLink != null && !imageLink.isEmpty()) {
                            selectedProduct.setImageUrl(imageLink);
                        }
                        replyIntent.putExtra(EXTRA_EDIT_PRODUCT_PRODUCT, selectedProduct);
                        setResult(RESULT_OK, replyIntent);

                    /*if(currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                        //currentPhotoPath = null;
                    }*/
                    }
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");*/
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(imageBitmap);
            imageUploaded = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(R.id.edit_product_layout), R.string.camera_permission_denied, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}