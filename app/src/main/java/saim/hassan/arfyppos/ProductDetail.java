package saim.hassan.arfyppos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;


import java.util.Arrays;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.Model.Product;
import saim.hassan.arfyppos.Model.Rating;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ProductDetail extends AppCompatActivity  implements RatingDialogListener {


    TextView productName,productPrice,productDescription;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnrating;
    CounterFab btnCarrt;
    FloatingActionButton btnAr;

    ElegantNumberButton numberButton;

    RatingBar ratingBar;

    String productId = "";

    FirebaseDatabase database;
    DatabaseReference products;
    DatabaseReference ratingTbl;


    Button btnshowComments;


    Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/pos_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        ;

        setContentView(R.layout.activity_product_detail);


        btnshowComments = (Button)findViewById(R.id.btnShowComment);
        btnshowComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent comment = new Intent(ProductDetail.this,ShowComment.class);
                comment.putExtra(Common.INTENT_PRODUCT_ID,productId);
                startActivity(comment);
            }
        });


        //Firebase
        database = FirebaseDatabase.getInstance();
      //  products = database.getReference("POS").child(Common.posSelected).child("detail").child("Products");
        products = database.getReference("Products");
        ratingTbl = database.getReference("Rating");

        btnAr = (FloatingActionButton)findViewById(R.id.btnAR);
        btnAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent ar = new Intent(ProductDetail.this,ButtonARActivity.class);
                 startActivity(ar);
            }
        });

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCarrt = (CounterFab)findViewById(R.id.btnCart);
        btnrating = (FloatingActionButton)findViewById(R.id.btnRating);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        btnrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCarrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        productId,
                        currentProduct.getName(),
                        numberButton.getNumber(),
                        currentProduct.getPrice(),
                        currentProduct.getDiscount(),
                        currentProduct.getImage()
                ));
                Toast.makeText(ProductDetail.this, "ADDED TO CART SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        });

        btnCarrt.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        productDescription = (TextView)findViewById(R.id.product_description);
        productName = (TextView)findViewById(R.id.product_namee);
        productPrice = (TextView)findViewById(R.id.product_price);
        product_image = (ImageView)findViewById(R.id.img_product);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get Product Id  From Intent
        if (getIntent() != null)
        productId = getIntent().getStringExtra("ProductId");
        if(!productId.isEmpty())
        {
            if (Common.isConnectedToInternet(getBaseContext()))
            {
                getDetailProduct();
                getRatingProduct(productId);
            }
            else
            {
                Toast.makeText(this, "Please Check your internet Connection", Toast.LENGTH_SHORT).show();
            }

            getRatingProduct(productId);
        }


    }

    private void getRatingProduct(String productId) {
        Query productRating = ratingTbl.orderByChild("productId").equalTo(productId);

        productRating.addValueEventListener(new ValueEventListener() {
            int count = 0,sum = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+= Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0)
                {
                    float average = sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quiet Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Product")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please Write your Comment Here....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ProductDetail.this)
                .show();
    }

    private void getDetailProduct() {
        products.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentProduct = dataSnapshot.getValue(Product.class);


                //Set Image
                Picasso.with(getBaseContext()).load(currentProduct.getImage()).into(product_image);
                collapsingToolbarLayout.setTitle(currentProduct.getName());
                productPrice.setText(currentProduct.getPrice());
                productName.setText(currentProduct.getName());
                productDescription.setText(currentProduct.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {
        
    }

    @Override
    public void onPositiveButtonClicked(int value,  String comments) {
        //Get Rating and Upload to Firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                productId,
                String.valueOf(value),
                comments
                );

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(ProductDetail.this, "Thanks for rating product !!!", Toast.LENGTH_SHORT).show();
                    }
                });


        /*
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    //Remove Old Value  Delete or Let It to be Useless Function
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Update new Value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                else
                {
                     ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(ProductDetail.this, "Thanks for rating product !!!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })
        ;
        */

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
