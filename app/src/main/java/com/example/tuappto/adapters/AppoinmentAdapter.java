package com.example.tuappto.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tuappto.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import negocio.Appointment;

public class AppoinmentAdapter extends RecyclerView.Adapter<AppoinmentAdapter.ViewHolder> implements View.OnClickListener{

   private int resources;
   private boolean kind;
   private ArrayList<Appointment> appoinments;
   private View.OnClickListener listener;


    public AppoinmentAdapter(ArrayList<Appointment> mAppoinment, int resources, boolean kind) {
        this.appoinments = mAppoinment;
        this.resources = resources;
        this.kind = kind; //seller false
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resources,parent,false);
        view.setOnClickListener(AppoinmentAdapter.this);
        return new AppoinmentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appoinments.get(position);

        if(!this.kind){
            holder.textViewName.setText("Nombre: " + appointment.getClientName() +" "+appointment.getClientSecondName());
            holder.textViewSecondName.setText(String.format("Telefono: %s", String.valueOf(appointment.getClientPhone())));
            downloadPhoto(String.valueOf(appointment.getClientImagePath()),holder.imageViewUser);
        }

        else {
            holder.textViewName.setText("Nombre: " + appointment.getOwnerName() +" "+ appointment.getOwnerSecondName());
            holder.textViewSecondName.setText(String.format("Telefono: %s", String.valueOf(appointment.getOwnerPhone())));
            downloadPhoto(String.valueOf(appointment.getOwnerImagePath()),holder.imageViewUser);
        }

        holder.textViewEmail.setText(String.format("Direccion: %s", String.valueOf(appointment.getAddress())));
        holder.textViewPhone.setText("Fecha: "+ appointment.getDay() + ":" + appointment.getMonth()+ ":"+appointment.getYear());
        holder.textViewProperty.setText("Hora: "+ appointment.getHour() + ":" + appointment.getMin());

    }

    @Override
    public int getItemCount() {
        return appoinments.size();
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
        private TextView textViewName;
        private TextView textViewSecondName;
        private TextView textViewEmail;
        private TextView textViewPhone;
        private TextView textViewProperty;
        private ImageView imageViewUser;


        public View view;

        private ViewHolder(View view){
            super(view);
            this.view = view;
            this.textViewName = view.findViewById(R.id.textViewName);
            this.textViewSecondName = view.findViewById(R.id.textViewSecondName);
            this.textViewEmail = view.findViewById(R.id.textViewEmail);
            this.textViewPhone = view.findViewById(R.id.textViewPhone);
            this.textViewProperty = view.findViewById(R.id.textViewProperty);
            this.imageViewUser = view.findViewById(R.id.imageViewUser);

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
