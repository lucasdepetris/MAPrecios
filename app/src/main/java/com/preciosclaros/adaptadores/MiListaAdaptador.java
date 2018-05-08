package com.preciosclaros.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.preciosclaros.CircleTransform;
import com.preciosclaros.LineDividerGray;
import com.preciosclaros.MostrarLista;
import com.preciosclaros.R;
import com.preciosclaros.SimpleDividerItemDecoration;
import com.preciosclaros.modelo.Items;
import com.preciosclaros.modelo.Lista;
import com.preciosclaros.modelo.Listas;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lucas on 23/6/2017.
 */

public class MiListaAdaptador extends RecyclerView.Adapter<MiListaAdaptador.ViewHolder> {

    private List<Items>items = new ArrayList<Items>();
    private Context mContext;
    private boolean optimizada;
    public MiListaAdaptador(List<Items> items, Context context,boolean optimizada) {
        this.mContext = context;
        this.items = items;
        this.optimizada = optimizada;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MiListaAdaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_una_lista, null);

        return new MiListaAdaptador.ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(MiListaAdaptador.ViewHolder holder, int position) {
        final Items item = this.items.get(position);
        Picasso.with(holder.imgSucursal.getContext()).load("https://imagenes.preciosclaros.gob.ar/comercios/"+item.getComercio().getComercioId()+"-"+item.getComercio().getBanderaId()+".jpg")
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.no_image_aivalable)
                .into(holder.imgSucursal);
        holder.direccion.setText(item.getComercio().getDireccion());
        holder.totalDeclaradoPorComercio.setText("Total:$"+item.getTotal());
        holder.NombreSucursal.setText(item.getComercio().getBanderaDescripcion());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.productosDeSucursal.getContext());
        holder.productosDeSucursal.setLayoutManager(linearLayoutManager);
        holder.productosDeSucursal.addItemDecoration(new LineDividerGray(
                holder.productosDeSucursal.getContext()
        ));
        if(optimizada){
            ListasOptimizadasAdapter adapter = new ListasOptimizadasAdapter(item.getProductos(),mContext);
            holder.productosDeSucursal.setAdapter(adapter);
        }
        else {
            ProductosDeSucursalAdapter adapter = new ProductosDeSucursalAdapter(item.getProductos(),mContext);
            holder.productosDeSucursal.setAdapter(adapter);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View item;

        @BindView(R.id.nombreSucursal)
        TextView NombreSucursal;
        @BindView(R.id.reciclerProductosDeSucursal) RecyclerView productosDeSucursal;
        @BindView(R.id.direccionSucursalDeLista) TextView direccion;
        @BindView(R.id.imgSucursalDeLista) ImageView imgSucursal;
        @BindView(R.id.TotalPorSupermercadoDeclarado) TextView totalDeclaradoPorComercio;
        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
