package saim.hassan.arfyppos;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Helper.RecyclerItemTouchHelper;
import saim.hassan.arfyppos.Interface.RecyclerItemTouchHelperListener;
import saim.hassan.arfyppos.Model.Favourites;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.ViewHolder.FavouritesAdapter;
import saim.hassan.arfyppos.ViewHolder.FavouritesViewHolder;

public class FavouritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavouritesAdapter adapter;
    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        //Swipe to Delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadFavourites();
    }

    private void loadFavourites() {
        adapter = new FavouritesAdapter(this,new Database(this).getAllFavourities(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavouritesViewHolder)
        {
            String name = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(position).getProductName();

            Favourites deleteItem = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removefromFavourites(deleteItem.getProductId(), Common.currentUser.getPhone());

            //Make Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from Favourites!",Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavourites(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
}
