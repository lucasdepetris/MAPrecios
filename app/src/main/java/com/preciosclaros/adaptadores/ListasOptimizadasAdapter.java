package com.preciosclaros.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.preciosclaros.MostrarLista;
import com.preciosclaros.R;
import com.preciosclaros.modelo.Productos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lucas on 18/7/2017.
 */

public class ListasOptimizadasAdapter extends RecyclerView.Adapter<ListasOptimizadasAdapter.ViewHolder> {

    private List<Productos> productos = new ArrayList<Productos>();
    private Context mContext;
    public ListasOptimizadasAdapter(List<Productos> productos,Context context) {
        this.mContext = context;
        this.productos = productos;
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    @Override
    public ListasOptimizadasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_lista_optimizada, null);

        return new ListasOptimizadasAdapter.ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ListasOptimizadasAdapter.ViewHolder holder, int position) {
        final Productos lista = this.productos.get(position);
        Picasso.with(holder.imgProducto.getContext()).load("https://imagenes.preciosclaros.gob.ar/productos/"+lista.getProducto().getId()+".jpg")
                .placeholder(R.drawable.image_placeholder).error(R.drawable.no_image_aivalable).into(holder.imgProducto);
        holder.descripcionProducto.setText(lista.getProducto().getNombre());
        final String cant = String.valueOf(this.productos.get(position).getCantidad());
        holder.cantidad.setText(cant);
        String precio = String.valueOf(this.productos.get(position).getPrecioOptimo());
        holder.precioProducto.setText("$"+precio);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View item;

        @BindView(R.id.imgProductoMiLista)
        ImageView imgProducto;
        @BindView(R.id.precioProductoMiLista)
        TextView precioProducto;
        @BindView(R.id.descripcionProductoMiLista) TextView descripcionProducto;
        @BindView(R.id.cantidadProductoMiLista) TextView cantidad;

        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
