package com.example.tuappto.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuappto.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import negocio.Appointment;
import negocio.Property;

public class AppoinmentAdapter extends RecyclerView.Adapter<AppoinmentAdapter.ViewHolder> implements View.OnClickListener{
   private int publication;
   private ArrayList<Appointment> appoinments;
   private View.OnClickListener listener;


    public AppoinmentAdapter(ArrayList<Appointment> mAppoinment, int publication) {
        this.appoinments = mAppoinment;
        this.publication = publication;
    }




    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication,parent,false);
        view.setOnClickListener(AppoinmentAdapter.this);
        return new AppoinmentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appoinments.get(position);

        holder.textViewArea.setText("Año: " + String.valueOf(appointment.getYear()));
        holder.textViewParking.setText(String.format("Mes: %s", String.valueOf(appointment.getMonth())));
        holder.textViewRooms.setText(String.format("Dia: %s", String.valueOf(appointment.getDay())));
        holder.textViewPrice.setText(String.format("Hora: %s", String.valueOf(appointment.getHour())));
        holder.textViewKind.setText("Minutos:" + String.valueOf(appointment.getMin()));
        //poner String.format("Direccion: %s", String.valueOf(property. ACA EL METODO QUE LLAMA A LA DIRECCION DESDE PROPIEDAD ()))

        holder.textViewAddress.setText("Direccion: " + appointment.getAddress());

    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }

    @Override
    public int getItemCount() {
        return appoinments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewAddress;
        private TextView textViewArea;
        private TextView textViewParking;
        private TextView textViewRooms;
        private TextView textViewPrice;
        private TextView textViewKind;


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

        }
    }
}
