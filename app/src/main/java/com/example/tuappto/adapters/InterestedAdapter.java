package com.example.tuappto.adapters;

import android.graphics.BitmapFactory;
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
import negocio.Interest;

public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.ViewHolder> implements View.OnClickListener {

    private int resources;
    private ArrayList<Interest> interested;
    private View.OnClickListener listener;

    public InterestedAdapter(ArrayList<Interest>interested, int resources){
        this.interested = interested;
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Interest interest = interested.get(position);

        holder.textViewName.setText(String.format("Nombre: %s", String.valueOf(interest.getClientName())));
        holder.textViewSecondName.setText(String.format("Apellido: %s", String.valueOf(interest.getClientSecondName())));
        holder.textViewEmail.setText(String.format("Email: %s", String.valueOf(interest.getClientEmail())));
        holder.textViewPhone.setText(String.format("Telefono: %s", String.valueOf(interest.getClientPhone())));
        holder.textViewProperty.setText(String.format("Interesado en: %s", String.valueOf(interest.getProperty())));

        downloadPhoto(String.valueOf(interest.getClientImagePath()),holder.imageViewUser);

    }

    @Override
    public int getItemCount() {
        return interested.size();
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




