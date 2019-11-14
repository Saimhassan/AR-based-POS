package saim.hassan.arfyppos.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import saim.hassan.arfyppos.Cart;
import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Interface.ItemClickListener;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.R;




public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, final int i) {

        Picasso.with(cart.getBaseContext())
                .load(listData.get(i).getImage())
                .resize(70,70)
                .centerCrop()
                .into(cartViewHolder.cart_image);

        cartViewHolder.btn_quantitty.setNumber(listData.get(i).getQuantity());
        cartViewHolder.btn_quantitty.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(i);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //Update TextBill
                int total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item:orders)
                    total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en","US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotalPrice.setText(fmt.format(total));

            }
        });


        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(i).getPrice()))*(Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.txt_price.setText(fmt.format(price));
        cartViewHolder.txt_cart_name.setText(listData.get(i).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position)
    {
        return listData.get(position);
    }

    public  void removeItem(int position)
    {
           listData.remove(position);
           notifyItemRemoved(position);
    }
    public void restoreItem(Order item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }


}
