package com.example.projektsklep;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ProductsFragment extends Fragment {
    public static final String EXTRA_PRODUCT_DATA = "EXTRA_PRODUCT_DATA";
    public static final int NEW_PRODUCT_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_PRODUCT_ACTIVITY_REQUEST_CODE = 2;
    public static final String IMAGE_URL_BASE = "https://i.imgur.com/";
    private RecyclerView recyclerView;
    private Product selectedProduct;
    private User currentUser;
    private StoreViewModel storeViewModel;
    private MainActivity parentActivity;
    private List<Product> productList;
    private SearchView searchView;
    private Bundle resumedInstanceState;

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
            Intent intent;
            //intent.putExtra(KEY_EXTRA_BOOK_ID, book.getId());
            selectedProduct = product;
            if(currentUser.getAdmin() == true) {
                intent = new Intent(parentActivity, EditProductActivity.class);
                intent.putExtra(EXTRA_PRODUCT_DATA, selectedProduct);
                startActivityForResult(intent, EDIT_PRODUCT_ACTIVITY_REQUEST_CODE);
            }
            else {
                intent = new Intent(parentActivity, ProductDetailsActivity.class);
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
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            storeViewModel.delete(product);
                            Snackbar.make(parentActivity.findViewById(R.id.drawer_layout),
                                    getString(R.string.product_deleted),
                                    Snackbar.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    R.string.no,
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

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
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
                Log.d("ProductsFragment", "No products");
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

        void setProducts(List<Product> products) {
            this.products = products;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        parentActivity = (MainActivity)getActivity();
        storeViewModel = parentActivity.storeViewModel;
        if (parentActivity.getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User)parentActivity.getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }

        recyclerView = view.findViewById(R.id.products_recyclerview);
        final ProductAdapter adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));

        storeViewModel.findAllProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable final List<Product> products) {
                adapter.setProducts(products);
                productList = products;
            }
        });

        FloatingActionButton addProductButton = parentActivity.findViewById(R.id.add_item_button);
        if(currentUser.getAdmin() == true) {
            addProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(parentActivity, EditProductActivity.class);
                    startActivityForResult(intent, NEW_PRODUCT_ACTIVITY_REQUEST_CODE);
                }
            });
        }
        else {
            addProductButton.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            Product product = (Product) data.getSerializableExtra(EXTRA_PRODUCT_DATA);
            storeViewModel.insert(product);
            Snackbar.make(parentActivity.findViewById(R.id.coordinator_layout),
                    getString(R.string.product_added),
                    Snackbar.LENGTH_LONG).show();
        } else if(requestCode == EDIT_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            Product product = (Product) data.getSerializableExtra(EXTRA_PRODUCT_DATA);
            storeViewModel.update(product);
            Snackbar.make(parentActivity.findViewById(R.id.coordinator_layout),
                    getString(R.string.product_edited),
                    Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem clearSearchItem = menu.findItem(R.id.menu_item_clear);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Product> list = storeViewModel.findProductWithNameOrDescription(query);
                ((ProductAdapter)recyclerView.getAdapter()).setProducts(list);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if(resumedInstanceState != null) {
            searchView.setQuery(resumedInstanceState.getString(MainActivity.KEY_SEARCH_TERM), true);
            searchView.setIconified(false);
            searchView.requestFocus();
            resumedInstanceState = null;
        }
        clearSearchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                ((ProductAdapter)recyclerView.getAdapter()).setProducts(productList);
                //((ProductAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            resumedInstanceState = savedInstanceState;
        }
        /*if(savedInstanceState != null) {
            searchView.setQuery(savedInstanceState.getString(MainActivity.KEY_SEARCH_TERM), true);
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(searchView.getQuery() != null && !searchView.getQuery().toString().isEmpty()) {
            if(searchView.getQuery() != null && !searchView.getQuery().toString().isEmpty()) {
                outState.putString(MainActivity.KEY_SEARCH_TERM, searchView.getQuery().toString());
            }
        }
    }
}