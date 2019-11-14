package saim.hassan.arfyppos.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import saim.hassan.arfyppos.Model.Favourites;
import saim.hassan.arfyppos.Model.Order;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "FYPDB.db";
    private static final int DB_VER = 2;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);

    }

    public boolean checkProductExists(String productId,String userPhone)
    {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",userPhone,productId);
        cursor = db.rawQuery(SQLQuery,null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;

    }

    public List<Order> getCarts(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable = "OrderDetail";
        qb.setTables(sqlTable);

        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))
                ));

            } while (c.moveToNext());
        }
        return result;

    }


    public void addToCart (Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());

        db.execSQL(query);
    }

    public void cleanCart(String userPhone)
    {
            SQLiteDatabase db = getReadableDatabase();
            String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
            db.execSQL(query);
    }

    public int getCountCart(String userPhone) {
        int count =0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail Where UserPhone='%s'",userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductId='%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart(String userPhone, String productId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = Quantity+1  WHERE UserPhone = '%s' AND ProductId='%s'",userPhone,productId);
        db.execSQL(query);
    }

    public void removefromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId= '%s'",phone,productId);
        db.execSQL(query);
    }

    //Favourites
    public void addToFavourites(Favourites product)
    {
       SQLiteDatabase db = getReadableDatabase();
       String querey = String.format("INSERT INTO Favourites(" +
               "ProductId,ProductName,ProductPrice,ProductMenuId,ProductImage,ProductDiscount,ProductDescription,UserPhone)" +
               "VALUES('%s','&s','%s','&s','%s','&s','%s','&s');",
               product.getProductId(),
               product.getProductName(),
               product.getProductPrie(),
               product.getProductMenuId(),
               product.getProductImage(),
               product.getProductDiscount(),
               product.getProductDescription(),
               product.getUserPhone());
       db.execSQL(querey);
    }
    public void removefromFavourites(String productId,String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String querey = String.format("DELETE FROM Favourites WHERE ProductId='%s' and UserPhone='%s';",productId,userPhone);
        db.execSQL(querey);
    }
    public boolean isFavourities(String productId,String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE ProductId='%s' and UserPhone='%s';",productId,userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }




    public List<Favourites> getAllFavourities(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductId","ProductName","ProductPrice","ProductMenuId","ProductImage","ProductDiscount","ProductDescription"};
        String sqlTable = "Favourites";
        qb.setTables(sqlTable);

        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Favourites> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
               result.add(new Favourites(
                       c.getString(c.getColumnIndex("ProductId")),
                       c.getString(c.getColumnIndex("ProductName")),
                       c.getString(c.getColumnIndex("ProductPrice")),
                       c.getString(c.getColumnIndex("ProductMenuId")),
                       c.getString(c.getColumnIndex("ProductImage")),
                       c.getString(c.getColumnIndex("ProductDiscount")),
                       c.getString(c.getColumnIndex("ProductDescription")),
                       c.getString(c.getColumnIndex("UserPhone"))
               ));

            } while (c.moveToNext());
        }
        return result;

    }



}
