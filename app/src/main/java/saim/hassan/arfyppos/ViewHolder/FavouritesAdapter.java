package saim.hassan.arfyppos.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.Model.Favourites;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.Model.Product;
import saim.hassan.arfyppos.ProductDetail;
import saim.hassan.arfyppos.ProductList;
import saim.hassan.arfyppos.R;

import static saim.hassan.arfyppos.Common.Common.currentUser;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesViewHolder> {


    private Context context;
    private List<Favourites> favouritesList;

    public FavouritesAdapter(Context context, List<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favourites_item,viewGroup,false);
        return new FavouritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {

        holder.txtProductName.setText(favouritesList.get(position).getProductName());
        holder.txtProductPrice.setText(String.format("$ %s",favouritesList.get(position).getProductPrie().toString()));
        Picasso.with(context).load(favouritesList.get(position).getProductImage())
                .into(holder.imgProduct);
        //Quick Cart

        holder.quickcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).checkProductExists(favouritesList.get(position).getProductId(), currentUser.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            currentUser.getPhone(),
                            favouritesList.get(position).getProductId(),
                            favouritesList.get(position).getProductName(),
                            "1",
                            favouritesList.get(position).getProductPrie(),
                            favouritesList.get(position).getProductDiscount(),
                            favouritesList.get(position).getProductImage()
                    ));

                } else {
                    new Database(context).increaseCart(currentUser.getPhone(),
                            favouritesList.get(position).getProductId());
                }
                Toast.makeText(context, "ADDED TO CART SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        });




        final Favourites local = favouritesList.get(position);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Start New Activity
                Intent detail = new Intent(context, ProductDetail.class);
                detail.putExtra("ProductId",favouritesList.get(position).getProductId());
                context.startActivity(detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }


    public  void removeItem(int position)
    {
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favourites item,int position)
    {
        favouritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourites getItem(int position){
        return favouritesList.get(position);
    }
}
