package saim.hassan.arfyppos.ViewHolder;

import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice;
    public ImageView imgProduct, img_fav,img_share,quickcart;

    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = (TextView)itemView.findViewById(R.id.product_text);
        txtProductPrice = (TextView)itemView.findViewById(R.id.product_price);
        imgProduct = (ImageView)itemView.findViewById(R.id.product_image);
        img_fav = (ImageView)itemView.findViewById(R.id.fav);
        img_share = (ImageView)itemView.findViewById(R.id.btn_share);
        quickcart = (ImageView)itemView.findViewById(R.id.quick_cart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
