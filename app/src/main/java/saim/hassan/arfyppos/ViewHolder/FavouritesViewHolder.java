package saim.hassan.arfyppos.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.R;

public class FavouritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice;
    public ImageView imgProduct, img_fav,img_share,quickcart;

    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FavouritesViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = (TextView)itemView.findViewById(R.id.product_text);
        txtProductPrice = (TextView)itemView.findViewById(R.id.product_price);
        imgProduct = (ImageView)itemView.findViewById(R.id.product_image);
        img_fav = (ImageView)itemView.findViewById(R.id.fav);
        img_share = (ImageView)itemView.findViewById(R.id.btn_share);
        quickcart = (ImageView)itemView.findViewById(R.id.quick_cart);
        view_background= (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground = (LinearLayout) itemView.findViewById(R.id.view_foreground);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
