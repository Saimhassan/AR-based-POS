package saim.hassan.arfyppos.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrdername;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);


        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
       txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrdername = (TextView)itemView.findViewById(R.id.order_name);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
//        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
