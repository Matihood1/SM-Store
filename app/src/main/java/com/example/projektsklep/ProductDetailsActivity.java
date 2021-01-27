package com.example.projektsklep;

import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import ru.discode.mailbackgroundlibrary.BackgroundMail;

public class ProductDetailsActivity extends AppCompatActivity {
    private User currentUser;
    private ImageView imageView;
    private TextView nameTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private Button purchaseButton;
    private Product selectedProduct;
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imageView = findViewById(R.id.product_details_image);
        nameTextView = findViewById(R.id.product_details_name);
        priceTextView = findViewById(R.id.product_details_price);
        descriptionTextView = findViewById(R.id.product_details_description);
        purchaseButton = findViewById(R.id.product_purchase_button);

        lightSensor = LoginActivity.lightSensor;

        if (getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User) getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }

        if (getIntent().hasExtra(ProductsFragment.EXTRA_PRODUCT_DATA)) {
            selectedProduct = (Product) getIntent().getSerializableExtra(ProductsFragment.EXTRA_PRODUCT_DATA);
            nameTextView.setText(selectedProduct.getName());
            String formattedPrice = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pl-PL")).format((selectedProduct.getPrice()));
            priceTextView.setText(formattedPrice);
            descriptionTextView.setText(selectedProduct.getDescription());
            if (selectedProduct.getImageUrl() != null && !selectedProduct.getImageUrl().trim().isEmpty()) {
                Picasso.get()
                        .load(ProductsFragment.IMAGE_URL_BASE + selectedProduct.getImageUrl() + ".jpg")
                        .placeholder(R.drawable.ic_baseline_image_24dp).into(imageView);
            }
            else {
                imageView.setImageResource(R.drawable.ic_baseline_image_24dp);
            }
        }
        else {
            imageView.setImageResource(R.drawable.ic_baseline_image_24dp);
        }

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundMail.newBuilder(ProductDetailsActivity.this)
                        .withMailBox("smtp.gmail.com", 465, true)
                        .withFrom("SMkontoprojekt@gmail.com")
                        .withUsername("SMkontoprojekt@gmail.com")
                        .withPassword("1qaz@wsx3edC")
                        .withSenderName("SMProj sp. z.o.o.")
                        .withMailTo(currentUser.getEmail())
                        .withType(BackgroundMail.TYPE_PLAIN)
                        .withSubject(getString(R.string.email_subject))
                        .withBody(String.format(getString(R.string.email_body),
                                currentUser.getFirstName() + " " + currentUser.getLastName(),
                                selectedProduct.getName()))
                        .withOnSuccessCallback(new BackgroundMail.OnSendingCallback() {
                            @Override
                            public void onSuccess() {
                                Snackbar.make(findViewById(R.id.product_details_layout),
                                        getString(R.string.email_sent),
                                        Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                Snackbar.make(findViewById(R.id.product_details_layout),
                                        getString(R.string.email_failed),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .send();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(lightSensor != null) {
            lightSensor.onStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(lightSensor != null) {
            lightSensor.onPause();
        }
    }
}