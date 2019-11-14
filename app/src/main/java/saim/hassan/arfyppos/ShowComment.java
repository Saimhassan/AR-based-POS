package saim.hassan.arfyppos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Model.Rating;
import saim.hassan.arfyppos.ViewHolder.ShowCommentViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ShowComment extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference ratingTbl;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;
    String productId = "";
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
        {
            adapter.stopListening();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("Nabila.ttf")
        .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_show_comment);
        //Database
        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");
        recyclerView = (RecyclerView)findViewById(R.id.recycler_comment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Swipe Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    productId = getIntent().getStringExtra(Common.INTENT_PRODUCT_ID);
                    if (!productId.isEmpty() && productId != null)
                    {
                      //Create Request Query
                        Query query = ratingTbl.orderByChild("productId").equalTo(productId);
                        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();
                        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {

                                holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                                holder.txtComment.setText(model.getComment());
                                holder.txtUserPhone.setText(model.getUserPhone());

                            }

                            @NonNull
                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                                View view = LayoutInflater.from(viewGroup.getContext())
                                        .inflate(R.layout.show_comment_layout,viewGroup,false);

                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadComment(productId);

                    }

            }
        });

        //Thread  to Load Comment at First launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
               mSwipeRefreshLayout.setRefreshing(true);
                if (getIntent() != null)
                    productId = getIntent().getStringExtra(Common.INTENT_PRODUCT_ID);
                if (!productId.isEmpty() && productId != null)
                {
                    //Create Request Query
                    Query query = ratingTbl.orderByChild("productId").equalTo(productId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();
                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {

                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());

                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.show_comment_layout,viewGroup,false);

                            return new ShowCommentViewHolder(view);
                        }
                    };
                    loadComment(productId);
                }
            }
        });

    }

    private void loadComment(String productId) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);

    }
}
