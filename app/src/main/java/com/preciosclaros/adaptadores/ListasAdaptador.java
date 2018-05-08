package com.preciosclaros.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.preciosclaros.MisListas;
import com.preciosclaros.MostrarLista;
import com.preciosclaros.R;
import com.preciosclaros.modelo.Listas;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lucas on 19/6/2017.
 */

public class ListasAdaptador extends RecyclerView.Adapter<ListasAdaptador.ViewHolder> {

    private List<Listas> listas = new ArrayList<Listas>();
    private Context mContext;
    public ListasAdaptador(List<Listas> listas, Context context) {
        this.mContext = context;
        this.listas = listas;
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_lista, null);

        return new ListasAdaptador.ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Listas lista = this.listas.get(position);
        holder.NombreLista.setText(lista.getNombre());
        holder.Descripcion.setText(lista.getDescripcion());
        holder.NombreLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MostrarLista.class);
                intent.putExtra("idLista",lista.getId());
                v.getContext().startActivity(intent);
            }
        });
        holder.imgLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MostrarLista.class);
                intent.putExtra("idLista",lista.getId());
                v.getContext().startActivity(intent);
            }
        });
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof MisListas){

                    ((MisListas)mContext).showPopupEditarLista(lista);
                }
            }
        });
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof MisListas){

                    ((MisListas)mContext).showPopupEliminarLista(lista);
                }
            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View item;

        @BindView(R.id.NombreLista)
        TextView NombreLista;
        @BindView(R.id.DescripcionLista) TextView Descripcion;
        @BindView(R.id.imgLista)
        ImageView imgLista;
        @BindView(R.id.editarLista) ImageView editar;
        @BindView(R.id.borrarLista) ImageView borrar;
        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}