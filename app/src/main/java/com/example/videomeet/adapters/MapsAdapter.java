package com.example.videomeet.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videomeet.R;
import com.example.videomeet.activities.MapsTracking;
import com.example.videomeet.models.User;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapViewHolder> {

    private List<User> users;

    public MapsAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_container_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MapViewHolder extends RecyclerView.ViewHolder{

        TextView textFirstChar, textUsername, textEmail;
        ImageView imageMaps;

        public MapViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageMaps = itemView.findViewById(R.id.imageMaps);
        }

        void setUserData(User user) {
            textFirstChar.setText(user.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", user.firstName, user.lastName));
            textEmail.setText(user.email);
            String longitude = user.longitude;
            String latitude = user.latitude;
            String firstName = user.firstName;
            String lastName = user.lastName;
            imageMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MapsTracking.class);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
