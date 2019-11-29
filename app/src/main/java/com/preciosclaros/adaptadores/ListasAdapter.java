package com.preciosclaros.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.preciosclaros.R;
import com.preciosclaros.modelo.Lista;
import com.preciosclaros.modelo.Listas;
import com.preciosclaros.modelo.Sucursales;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lucas on 15/6/2017.
 */
public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.ViewHolder> {

    private List<Listas> listas = new ArrayList<Listas>();
    private Context mContext;

    public ListasAdapter(List<Listas> listas, Context context) {
        this.mContext = context;
        this.listas = listas;
    }
    public List<Listas> getListas(){return this.listas;}

    @Override
    public int getItemCount() {
        return listas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_lista, null);
        return new ListasAdapter.ViewHolder(itemLayoutView);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Listas lista = this.listas.get(position);
        holder.nombre.setText(lista.getNombre());
        /*
        int cid = Integer.parseInt(sucursal.getComercioId());
        int cidMejor = Integer.parseInt(MejorSucursal.getComercioId());
        int id = Integer.parseInt(sucursal.getId());
        int idMejor = Integer.parseInt(MejorSucursal.getId());
        if(cid == cidMejor && sucursal.getBanderaId() == MejorSucursal.getBanderaId() && id == idMejor)
        {
            holder.estrella.setVisibility(View.VISIBLE);
        }
        else{holder.estrella.setVisibility(View.GONE);}
        */
        /*holder.distancia.setText(sucursal.getDistanciaDescripcion());
        holder.nombreComercio.setText(sucursal.getBanderaDescripcion());
        holder.direccion.setText(sucursal.getDireccion());
        try{
            precio = Double.parseDouble(sucursal.getPreciosProducto().getPrecioLista());
        }catch (NumberFormatException e){
            Log.e("TAG",e.getMessage());
            precio = 0.00;
        }
        if(precio == 0.00){
            holder.precio.setText("Precio no disponible.");
        }else{
            holder.precio.setText("$"+precio);
        }
        holder.localidad.setText(sucursal.getLocalidad());
        Picasso.with(holder.imgComercio.getContext()).load("https://imagenes.preciosclaros.gob.ar/comercios/"+sucursal.getComercioId()+"-"+sucursal.getBanderaId()+".jpg")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.img_not_available)
                .error(R.drawable.img_not_available)
                .into(holder.imgComercio);*/
        //holder.agregar.setVisibility(View.INVISIBLE);
        /*
        holder.agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mContext instanceof VerProductoPorId){

                    ((VerProductoPorId)mContext).showPopupAdaptador(sucursal);
                }

            }
        });
        */
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View item;
        /* @OnClick(R.id.agregar)public void agregarProd(){

             }*/
        @BindView(R.id.NombreLista)
        TextView nombre;
        @BindView(R.id.DescripcionLista)
        TextView descripcion;
        @BindView(R.id.editarLista)
        ImageView editarLista;
        @BindView(R.id.borrarLista)
        ImageView borrarLista;

        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ButterKnife.bind(this, itemView);
        }

    }

}
/*
public class SucursalesAdapter extends ArrayAdapter<Sucursales> {
    public SucursalesAdapter(Context context, ArrayList<Sucursales> sucursales) {
        super(context, 0, sucursales);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Sucursales sucursal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.producto_sucursal, parent, false);
        }
        // Lookup view for data population
        TextView distancia = (TextView) convertView.findViewById(R.id.distancia);
        TextView nombreComercio = (TextView) convertView.findViewById(R.id.nombreComercio);
        TextView direccionComercio = (TextView) convertView.findViewById(R.id.direccion);
        TextView precioComercio = (TextView) convertView.findViewById(R.id.precio);
        TextView localidad = (TextView) convertView.findViewById(R.id.localidad);
        ImageView imgComercio = (ImageView) convertView.findViewById(R.id.imgComercio);
        // Populate the data into the template view using the data object
        precioComercio.setText(sucursal.getPreciosProducto().getPrecioLista());
        distancia.setText(sucursal.getDistanciaDescripcion());
        nombreComercio.setText(sucursal.getBanderaDescripcion());
        direccionComercio.setText(sucursal.getDireccion());
        localidad.setText(sucursal.getLocalidad());
        Picasso.with(getContext()).load("https://imagenes.preciosclaros.gob.ar/comercios/"+sucursal.getComercioId()+"-1.jpg").into(imgComercio);
       // Double pre = sucursal.getPreciosProducto().getPrecioLista();
        // Return the completed view to render on screen
        return convertView;
    }
}*/
