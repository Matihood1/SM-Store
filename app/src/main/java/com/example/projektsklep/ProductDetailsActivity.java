package com.example.projektsklep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {
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

        if (getIntent().hasExtra(ProductsFragment.EXTRA_PRODUCT_DATA)) {
            selectedProduct = (Product) getIntent().getSerializableExtra(ProductsFragment.EXTRA_PRODUCT_DATA);
            nameTextView.setText(selectedProduct.getName());
            priceTextView.setText(String.valueOf(selectedProduct.getPrice()));
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