package com.example.tuappto.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tuappto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

import negocio.Property;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> implements View.OnClickListener {

    private int resources;
    private ArrayList<Property> properties;
    private View.OnClickListener listener;


    public PropertyAdapter(ArrayList<Property>properties,int resources){
        this.properties = properties;
        this.resources = resources;

    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(resources,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

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

        holder.textViewParking.setText(String.format("Parqueaderos: %s", String.valueOf(property.getParking())));
        holder.textViewRooms.setText(String.format("Habitaciones: %s", String.valueOf(property.getRooms())));
        holder.textViewPrice.setText(String.format("Precio: %s", String.valueOf(property.getPrice())));


        holder.textViewAddress.setText(String.format("Direccion: %s", String.valueOf(property.getAddress())));
        downloadPhoto(property.getImagePath(),holder.imageViewProperty);

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewAddress;
        private TextView textViewArea;
        private TextView textViewParking;
        private TextView textViewRooms;
        private TextView textViewPrice;
        private TextView textViewKind;
        private ImageView imageViewProperty;

        public View view;

        private ViewHolder(View view){
            super(view);
            this.view = view;
            this.textViewAddress = view.findViewById(R.id.textViewAddress);
            this.textViewArea = view.findViewById(R.id.textViewArea);
            this.textViewKind = view.findViewById(R.id.textViewKind);
            this.textViewParking = view.findViewById(R.id.textViewParking);
            this.textViewRooms = view.findViewById(R.id.textViewRooms);
            this.textViewPrice = view.findViewById(R.id.textViewPrice);
            this.imageViewProperty = view.findViewById(R.id.imageViewProperty);
        }
    }

    private void downloadPhoto(String ruta, final ImageView iv) {
        db.setFirestoreSettings(settings);
        StorageReference photoRef = mStorageRef.child(ruta);
        final long ONE_MEGABYTE = 1024 * 1024 * 10; //(1024 bytes = 1 KB) x (1024 = 1 MB) x 1 = 1 MB
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes) {
                iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}




