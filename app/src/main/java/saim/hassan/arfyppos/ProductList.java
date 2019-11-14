package saim.hassan.arfyppos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.Model.Favourites;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.Model.Product;
import saim.hassan.arfyppos.ViewHolder.ProductViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static saim.hassan.arfyppos.Common.Common.currentUser;

public class ProductList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference productList;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    String categoryId = "";

    //Search Functionality
    FirebaseRecyclerAdapter<Product, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favourites
    Database localDb;

    //Share Photo
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //Create Target From Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create Photo From Bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);

            }


        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/pos_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        ;
        setContentView(R.layout.activity_product_list);

        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
     //   productList = database.getReference("POS").child(Common.posSelected).child("detail").child("Products");
        productList = database.getReference("Products");

        //Local DB
        localDb = new Database(this);



        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Get Intent Here
                if (getIntent()!=null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null)
                {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListProduct(categoryId);
                    else {
                        Toast.makeText(ProductList.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                //Get Intent Here
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListProduct(categoryId);
                    else {
                        Toast.makeText(ProductList.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

                //Search
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter Your Product");
                //Write Function to Load Suggestion List from Firebase
                loadSuggest();

                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        //When User Type their text ...Then Suggestion List Will be changed

                        List<String> suggest = new ArrayList<String>();
                        //Loop in The Suggest List
                        for (String search:suggestList)
                        {
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //When Search Bar is Closed
                        //Restore orignal Suggestion List Adapter
                        if (!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        //When Search Finishes
                        //Show Result of The Search Adapter
                        startSearch(text);

                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        //Search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Your Product");
        //Write Function to Load Suggestion List from Firebase
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //When User Type their text ...Then Suggestion List Will be changed

                List<String> suggest = new ArrayList<String>();
                //Loop in The Suggest List
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When Search Bar is Closed
                //Restore orignal Suggestion List Adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //When Search Finishes
                //Show Result of The Search Adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
        {
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {

        //Create Querey by Name
        Query searchByname =  productList.orderByChild("name").equalTo(text.toString());
        //Create Options with Query
        FirebaseRecyclerOptions<Product> productOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(searchByname,Product.class)
                .build();


        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                holder.txtProductName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imgProduct);

                final Product local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start New Activity
                        Intent detail = new Intent(ProductList.this,ProductDetail.class);
                        detail.putExtra("ProductId",searchAdapter.getRef(position).getKey());
                        startActivity(detail);
                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.product_item,viewGroup,false);
                return new ProductViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);

    }

    private void loadSuggest() {
        productList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Product item = postSnapshot.getValue(Product.class);
                            //Add name of product to do Suggest List
                            suggestList.add(item.getName());

                        }
                        materialSearchBar.setLastSuggestions(suggestList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void loadListProduct(String categoryId) {

        //Create Options with Query
        FirebaseRecyclerOptions<Product> producto = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productList.orderByChild("menuId").equalTo(categoryId),Product.class).setLifecycleOwner(this)
                .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(producto) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.product_item,viewGroup,false);
                return new ProductViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position, @NonNull final Product model) {

                holder.txtProductName.setText(model.getName());
                holder.txtProductPrice.setText(String.format("$ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imgProduct);
                //Quick Cart

                    holder.quickcart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isExists = new Database(getBaseContext()).checkProductExists(adapter.getRef(position).getKey(), currentUser.getPhone());
                            if (!isExists) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        currentUser.getPhone(),
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));

                            } else {
                                new Database(getBaseContext()).increaseCart(currentUser.getPhone(), adapter.getRef(position).getKey());
                            }
                            Toast.makeText(ProductList.this, "ADDED TO CART SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                        }
                    });




                //Add Favourities
                if (localDb.isFavourities(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                {
                    holder.img_fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //Click to Share
                holder.img_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getBaseContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });


                //Click to Change Status of Favourites
                holder.img_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourites favourites = new Favourites();
                        favourites.setProductId(adapter.getRef(position).getKey());
                        favourites.setProductName(model.getName());
                        favourites.setProductDescription(model.getDescription());
                        favourites.setProductDiscount(model.getDiscount());
                        favourites.setProductImage(model.getImage());
                        favourites.setProductMenuId(model.getMenuId());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        favourites.setProductPrie(model.getPrice());


                        if (!localDb.isFavourities(adapter.getRef(position).getKey(), currentUser.getPhone()))
                        {
                            localDb.addToFavourites(favourites);
                            holder.img_fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductList.this, ""+model.getName()+"Added Successfully to Favourites", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            localDb.removefromFavourites(adapter.getRef(position).getKey(), currentUser.getPhone());
                            holder.img_fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductList.this, ""+model.getName()+"Removed from Favourites", Toast.LENGTH_SHORT).show();

                        }
                    }
                });




                final Product local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start New Activity
                        Intent detail = new Intent(ProductList.this,ProductDetail.class);
                        detail.putExtra("ProductId",adapter.getRef(position).getKey());
                        startActivity(detail);
                    }
                });

            }
        };
        //Set Adapter
        Log.d("TAG",""+adapter.getItemCount());
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
//        searchAdapter.stopListening();
    }

}
