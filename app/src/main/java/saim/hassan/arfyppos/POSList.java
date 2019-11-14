package saim.hassan.arfyppos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.Model.Category;
import saim.hassan.arfyppos.Model.POS;
import saim.hassan.arfyppos.ViewHolder.MenuViewHolder;
import saim.hassan.arfyppos.ViewHolder.POSViewHolder;

public class POSList extends AppCompatActivity {

    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerOptions<POS> options = new FirebaseRecyclerOptions.Builder<POS>()
            .setQuery(FirebaseDatabase.getInstance().getReference()
                    .child("POS"),POS.class)
            .build();

   FirebaseRecyclerAdapter<POS,POSViewHolder>  adapter = new FirebaseRecyclerAdapter<POS, POSViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull POSViewHolder holder, int position, @NonNull POS model) {
            holder.textpos.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage())
                    .into(holder.imagepos);

            final POS clickItem = model;
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Get category Id and Send to new Activity
                    Intent pl = new Intent(POSList.this,Home.class);

                    //When user select Point of Sale we will save pos id to select category id of this point of sale
                    Common.posSelected = adapter.getRef(position).getKey();
                    startActivity(pl);
                }
            });
        }

        @NonNull
        @Override
        public POSViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.pos_item,viewGroup,false);
            return new POSViewHolder(itemView);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poslist);

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
                    LoadPOS();
                else{
                    Toast.makeText(POSList.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default , Load for the First Time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    LoadPOS();
                else {
                    Toast.makeText(POSList.this, "Please Check your Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.pos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void LoadPOS() {

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
