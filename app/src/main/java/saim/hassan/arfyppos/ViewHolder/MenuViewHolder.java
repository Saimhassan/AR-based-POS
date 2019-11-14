package saim.hassan.arfyppos.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textcategory;
    public ImageView imagecategory;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);


        textcategory = (TextView)itemView.findViewById(R.id.category_text);
        imagecategory = (ImageView)itemView.findViewById(R.id.category_image);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
