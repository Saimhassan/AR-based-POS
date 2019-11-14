package saim.hassan.arfyppos.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.R;

public class POSViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textpos;
    public ImageView imagepos;

    private ItemClickListener itemClickListener;

    public POSViewHolder(@NonNull View itemView) {
        super(itemView);


        textpos = (TextView)itemView.findViewById(R.id.pos_text);
        imagepos = (ImageView)itemView.findViewById(R.id.pos_image);

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
