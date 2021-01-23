package com.example.projektsklep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private StoreViewModel storeViewModel;
    private RecyclerView recyclerView;
    private MaterialToolbar topAppBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private User currentUser;
    private Product selectedProduct;
    public static final String EXTRA_PRODUCT_DATA = "EXTRA_PRODUCT_DATA";
    public static final int NEW_PRODUCT_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_PRODUCT_ACTIVITY_REQUEST_CODE = 2;
    public static final String IMAGE_URL_BASE = "https://i.imgur.com/";

    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView productNameTextView;
        private TextView productPriceTextView;
        private ImageView productImageView;
        private Product product;

        public ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.product_list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
            productImageView = itemView.findViewById(R.id.product_thumbnail);
        }

        public void bind(Product product) {
            this.product = product;
            productNameTextView.setText(product.getName());
            //Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("pl-PL"));
            String formattedPrice = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pl-PL")).format((product.getPrice()));
            //productPriceTextView.setText(String.valueOf(product.getPrice()));
            productPriceTextView.setText(formattedPrice);
            if (product.getImageUrl() != null) {
                Picasso.get() //with(context) got replaced with get()
                        .load(IMAGE_URL_BASE + product.getImageUrl() + ".jpg")
                        .placeholder(R.drawable.ic_baseline_image_24dp).into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.ic_baseline_image_24dp);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
            //intent.putExtra(KEY_EXTRA_BOOK_ID, book.getId());
            selectedProduct = product;
            if(currentUser.getAdmin() == true) {
                intent.putExtra(EXTRA_PRODUCT_DATA, selectedProduct);
                startActivityForResult(intent, EDIT_PRODUCT_ACTIVITY_REQUEST_CODE);
            }
            else {
                intent.putExtra(EXTRA_PRODUCT_DATA, selectedProduct);
                intent.putExtra(LoginActivity.EXTRA_LOGIN_USER, currentUser);
                startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage(R.string.product_delete_confirmation);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            storeViewModel.delete(product);
                            Snackbar.make(findViewById(R.id.drawer_layout),
                                    getString(R.string.product_deleted),
                                    Snackbar.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<Product> products;

        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProductHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
            if(products != null) {
                Product product = products.get(position);
                holder.bind(product);
            } else {
                Log.d("MainActivity", "No products");
            }
        }

        @Override
        public int getItemCount() {
            if(products != null) {
                return products.size();
            } else {
                return 0;
            }
        }

        void setProducts(List<Product> books) {
            this.products = books;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.topAppBar));

        topAppBar = findViewById(R.id.topAppBar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.close();
                return true;
            }
        });

        storeViewModel = new ViewModelProvider(this).get(StoreViewModel.class);
        if (getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User)getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }

        recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeViewModel = new ViewModelProvider(this).get(StoreViewModel.class);
        storeViewModel.findAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable final List<Product> products) {
                adapter.setProducts(products);
            }
        });


        FloatingActionButton addProductButton = findViewById(R.id.add_product_button);
        if(currentUser.getAdmin() == true) {
            addProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                    startActivityForResult(intent, NEW_PRODUCT_ACTIVITY_REQUEST_CODE);
                }
            });
        }
        else {
            addProductButton.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.item_users).setVisible(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.products_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem clearSearchItem = menu.findItem(R.id.menu_item_clear);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Product> list = storeViewModel.findProductWithNameOrDescription(query);
                ((BookAdapter)recyclerView.getAdapter()).setProducts(list);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        clearSearchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                ((BookAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Product product = (Product) data.getSerializableExtra(EXTRA_PRODUCT_DATA);
            storeViewModel.insert(product);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.product_added),
                    Snackbar.LENGTH_LONG).show();
        } else if(requestCode == EDIT_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Product product = (Product) data.getSerializableExtra(EXTRA_PRODUCT_DATA);
            storeViewModel.update(product);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.product_edited),
                    Snackbar.LENGTH_LONG).show();
        }
    }
}