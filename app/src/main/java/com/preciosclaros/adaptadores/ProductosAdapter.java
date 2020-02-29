package com.preciosclaros.adaptadores;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.preciosclaros.Constants;
import com.preciosclaros.VerProductoPorId;
import com.preciosclaros.modelo.Producto;
import com.preciosclaros.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lucas on 22/6/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    private List<Producto> productos = new ArrayList<Producto>();
    private Context context;
    private String status;
    public ProductosAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos, null);
        return new ProductosAdapter.ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.ViewHolder holder, int position) {
        final Producto producto = this.productos.get(position);
        //holder.precioProducto.setText("$"+"precio");
        holder.descripcionProducto.setText(producto.getNombre());
        holder.marca.setText(producto.getMarca());
        holder.codigoBarra.setText(producto.getId());
        Picasso.with(holder.imgProducto.getContext()).load(Constants.SERVER_IMAGENES_PRODUCTOS+producto.getId()+".jpg")
                .placeholder(R.drawable.img_not_available).error(R.drawable.img_not_available).into(holder.imgProducto);
       holder.contenedor.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context, VerProductoPorId.class);
               intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
               intent.putExtra(Constants.ACTIVITY,Constants.BUSCAR_PRODUCTOS_ACTIVITY);
               intent.putExtra(Constants.ID_PRODUCTO,producto.getId());
               Activity activity = (Activity) context;
               activity.startActivity(intent);
               //context.startActivity(intent);
               activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

           }
       });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View item;

        @BindView(R.id.imgProducto)
        ImageView imgProducto;
        //@BindView(R.id.precioProducto) TextView precioProducto;
        @BindView(R.id.marcaProducto)TextView marca;
        @BindView(R.id.idProducto) TextView codigoBarra;
        @BindView(R.id.descripcionProducto) TextView descripcionProducto;
        @BindView(R.id.cont_buscarProductos)RelativeLayout contenedor;
        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }
    }
}