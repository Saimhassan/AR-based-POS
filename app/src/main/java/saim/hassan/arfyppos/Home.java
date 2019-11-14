package saim.hassan.arfyppos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.Model.Banner;
import saim.hassan.arfyppos.Model.Category;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.Service.ListenOrder;
import saim.hassan.arfyppos.ViewHolder.MenuViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recycler_home;
    RecyclerView.LayoutManager layoutManager;
    CounterFab fab;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    //Swipe Refresh Layout
    SwipeRefreshLayout swipeRefreshLayout;

    //Slider
    HashMap<String,String> image_list;
    SliderLayout mSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/pos_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        ;
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    LoadCategories();
                else{
                    Toast.makeText(Home.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default , Load for the First Time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    LoadCategories();
                else {
                    Toast.makeText(Home.this, "Please Check your Connection", Toast.LENGTH_SHORT).show();
                }
                
                
            }
        });


        //Init Firebase

        database = FirebaseDatabase.getInstance();
       // category = database.getReference("POS").child(Common.posSelected).child("detail").child("Category");
        category = database.getReference("Category");

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.textcategory.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imagecategory);

                final Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get category Id and Send to new Activity
                        Intent pl = new Intent(Home.this,ProductList.class);
                        pl.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(pl);

                    }
                });



            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.category_item,viewGroup,false);
                return new MenuViewHolder(itemView);
            }
        };


       fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent cartIntent = new Intent(Home.this,Cart.class);
               startActivity(cartIntent);
            }
        });
//        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //LoadCategories

        recycler_home = (RecyclerView)findViewById(R.id.home_recycler);
       // layoutManager = new LinearLayoutManager(this);
      //  recycler_home.setLayoutManager(layoutManager);
        recycler_home.setLayoutManager(new GridLayoutManager(this,2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_home.getContext(),
                R.anim.layout_fall_down
                );
        recycler_home.setLayoutAnimation(controller);

        //Need call the slider function after you initialize firebase database
        //Setup Slider
        setupSlider();

        if (Common.isConnectedToInternet(this))
        {
            LoadCategories();
        }
        else
        {
            Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }


        //Register Services
        Intent services = new Intent(Home.this, ListenOrder.class);
        startService(services);


    }

    private void setupSlider() {
        mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list = new HashMap<>();

      //  final DatabaseReference banner = database.getReference("POS").child(Common.posSelected).child("detail").child("Banner");
        final DatabaseReference banner = database.getReference("Banner");
        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Banner banner = postSnapShot.getValue(Banner.class);
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for(String key:image_list.keySet())
                {
                    String[] keySplit = key.split("@@@");
                    String nameOfProduct = keySplit[0];
                    String idOfproduct = keySplit[1];

                    //Create Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.
                            description(nameOfProduct)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                         Intent intent = new Intent(Home.this,ProductDetail.class);
                                         //We will send product Id to Product Detail
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });

                    //Add Extra Bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("ProductId",idOfproduct);
                    mSlider.addSlider(textSliderView);

                    //Remove Event After Finish
                    banner.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);

    }

    private void LoadCategories() {

        adapter.startListening();
        recycler_home.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recycler_home.getAdapter().notifyDataSetChanged();
        recycler_home.scheduleLayoutAnimation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        //Fix to Click back Here
        if (adapter!= null)

            adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mSlider.stopAutoCycle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search)
            startActivity(new Intent(Home.this,SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_order) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_update_name) {
            showUpdateNameDialog();

        }  else if (id == R.id.nav_models) {
            Intent sceneform = new Intent(Home.this,ARlist.class);
            startActivity(sceneform);
        }else if (id == R.id.nav_offer){

        }else if (id == R.id.nav_sign_out){

            //Log out
            Intent signout = new Intent(Home.this,MainScreen.class);
            signout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signout);

        }else if (id == R.id.nav_fav){
            Intent fav = new Intent(Home.this,FavouritesActivity.class);
            startActivity(fav);

        }else if (id == R.id.nav_animation){
            Intent anim = new Intent(Home.this, ModelAnimation.class);
            startActivity(anim);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showUpdateNameDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Name");
        alertDialog.setMessage("Please Fill all Informations");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_updt = inflater.inflate(R.layout.update_name_layout,null);

        final MaterialEditText edtName = (MaterialEditText)layout_updt.findViewById(R.id.edtNamee);
        alertDialog.setView(layout_updt);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //For use SpotDialog , Please use Alert Dialog from android.app, not from v7 like above Alert Dialog
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                //UPDATE Name
                Map<String,Object> update_name = new HashMap<>();
                update_name.put("name",edtName.getText().toString());

                FirebaseDatabase.getInstance().getReference("User").child(Common.currentUser.getPhone())
                        .updateChildren(update_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                 //Dismiss Dialog
                                waitingDialog.dismiss();
                                if (task.isSuccessful())
                                    Toast.makeText(Home.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
            }
        });
    }

}
