package com.example.tuappto.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tuappto.R;
import java.util.ArrayList;
import negocio.Property;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private int resources;
    private ArrayList<Property> properties;
    public PropertyAdapter(ArrayList<Property>properties,int resources){
        this.properties = properties;
        this.resources = resources;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resources,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = properties.get(position);


        holder.textViewArea.setText("Area: " + property.getArea() + " mts^2");
        if(property.getSellOrRent().equals("sell")){
            holder.textViewKind.setText("Tipo: en Venta");
        }

        else if (property.getSellOrRent().equals("rent")){
            holder.textViewKind.setText("Tipo: en Arriendo");
        }

        else {
            holder.textViewKind.setText("Tipo: Venta/Arriendo");
        }

        holder.textViewParking.setText("Parqueaderos: " + String.valueOf(property.getParking()));
        holder.textViewRooms.setText("Habitaciones: " + String.valueOf(property.getRooms()));
        holder.textViewPrice.setText("Precio: " + String.valueOf(property.getPrice()));
        holder.textViewAddress.setText("Esto falta");
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewAddress;
        private TextView textViewArea;
        private TextView textViewParking;
        private TextView textViewRooms;
        private TextView textViewPrice;
        private TextView textViewKind;

        public View view;

        public ViewHolder(View view){
            super(view);
            this.view = view;
            this.textViewAddress = (TextView) view.findViewById(R.id.textViewAddress);
            this.textViewArea = (TextView) view.findViewById(R.id.textViewArea);
            this.textViewKind = (TextView) view.findViewById(R.id.textViewKind);
            this.textViewParking = (TextView) view.findViewById(R.id.textViewParking);
            this.textViewRooms = (TextView) view.findViewById(R.id.textViewRooms);
            this.textViewPrice = (TextView) view.findViewById(R.id.textViewPrice);
        }
    }
}
