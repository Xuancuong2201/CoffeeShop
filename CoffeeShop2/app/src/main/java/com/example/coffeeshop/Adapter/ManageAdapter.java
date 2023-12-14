package com.example.coffeeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeshop.Model.WareHouse;
import com.example.coffeeshop.R;

import java.util.List;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ManageAdapterViewHolder> {
    Context context;
    private List<WareHouse> WareHouseList;

    public ManageAdapter(Context context, List<WareHouse> list) {
        this.context = context;
        this.WareHouseList = list;
    }
    @NonNull
    @Override
    public ManageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mange,parent,false);
        return new ManageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAdapterViewHolder holder, int position) {
        holder.Name.setText(new StringBuilder().append(WareHouseList.get(position).getName()));
        holder.ID.setText(new StringBuilder().append(WareHouseList.get(position).getID()));
        holder.Date.setText(new StringBuilder().append(WareHouseList.get(position).getDate()));
        holder.Depot.setText(new StringBuilder().append(WareHouseList.get(position).getDepot()));
        holder.Type.setText(new StringBuilder().append(WareHouseList.get(position).getType()));
    }

    @Override
    public int getItemCount() {
        return WareHouseList.size();
    }

    public static class ManageAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView ID,Name,Date,Depot,Type;
        public ManageAdapterViewHolder(
            @NonNull View itemView) {
            super(itemView);
            ID=itemView.findViewById(R.id.ID);
            Name=itemView.findViewById(R.id.Name);
            Date=itemView.findViewById(R.id.Date);
            Depot=itemView.findViewById(R.id.Depot);
            Type=itemView.findViewById(R.id.Type);
        }
    }
}
