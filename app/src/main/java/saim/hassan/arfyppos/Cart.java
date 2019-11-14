package saim.hassan.arfyppos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.service.autofill.Dataset;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import saim.hassan.arfyppos.Common.Common;
import saim.hassan.arfyppos.Database.Database;
import saim.hassan.arfyppos.Helper.RecyclerItemTouchHelper;
import saim.hassan.arfyppos.Interface.RecyclerItemTouchHelperListener;
import saim.hassan.arfyppos.Model.Order;
import saim.hassan.arfyppos.Model.Request;
import saim.hassan.arfyppos.ViewHolder.CartAdapter;
import saim.hassan.arfyppos.ViewHolder.CartViewHolder;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    public TextView txtTotalPrice;

    RelativeLayout rootLayout;

    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter ;

    //Location







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        //Swipe to Delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (Button)findViewById(R.id.btnplaceorder);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size()>0)
                     showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is empty !!!", Toast.LENGTH_SHORT).show();
            }
        });
        LoadListProduct();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step");
        alertDialog.setMessage("Please Enter Your Address:");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);
        MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        MaterialEditText edtComments = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        final RadioButton rdiCOD = (RadioButton)order_address_comment.findViewById(R.id.cashonDelivery);
        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {



            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        edtAddress.getText().toString(),
                        Common.currentUser.getName(),
                        "0",
                        txtTotalPrice.getText().toString(),
                        edtComments.getText().toString(),
                        cart
                );
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                Toast.makeText(Cart.this, "Thank you ,Order Placed Successfully", Toast.LENGTH_SHORT).show();
                finish();

                //Remove Fragment
             //   getFragmentManager().beginTransaction()
               //         .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Remove Fragment
            //    getFragmentManager().beginTransaction()
                  //      .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
            }
        });
        alertDialog.show();
    }



    private void LoadListProduct() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate Total Price

        int total = 0;
        for (Order order:cart)
        {
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
            Locale locale = new Locale("en","US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //we will remove items at List<Order> by position
        cart.remove(position);
        //After that we will delete all data from SQLLite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //And final we will update new data from List<Order> to SQLLITE
        for (Order item:cart)
        new Database(this).addToCart(item);

        //Refresh
        LoadListProduct();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removefromCart(deleteItem.getProductId(),Common.currentUser.getPhone());

            //Update TextBill
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item:orders)
                total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en","US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));


            //Make Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from cart!",Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                           adapter.restoreItem(deleteItem,deleteIndex);
                           new Database(getBaseContext()).addToCart(deleteItem);

                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item:orders)
                        total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en","US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                }
            });
              snackbar.setActionTextColor(Color.YELLOW);
              snackbar.show();

        }
    }
}
