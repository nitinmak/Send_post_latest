package com.sendpost.dreamsoft;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.sendpost.dreamsoft.Classes.Functions;
import com.sendpost.dreamsoft.Interface.AdapterClickListener;
import com.sendpost.dreamsoft.databinding.ItemUserListBinding;
import com.sendpost.dreamsoft.model.UserModel;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<UserModel> list;
    private AdapterClickListener listener;
    Context context;

    public UsersAdapter(Context context, List<UserModel> list, AdapterClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserListBinding binding = ItemUserListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int i) {
        holder.binding.setUsers(list.get(i));

        if (list.get(i).getProfile_pic()==null || list.get(i).getProfile_pic().equals("")){
            Glide.with(context)
                    .load(Functions.getItemBaseUrl("uploads/no_image.png"))
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image );
        }else {
            Glide.with(context)
                    .load(Functions.getItemBaseUrl(list.get(i).getProfile_pic()))
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image );
        }

        holder.binding.dateBtn.setText(Functions.getFormatedDate(list.get(i).getCreated_at()));
        holder.binding.dateBtn.setText(Functions.getFormatedDate(list.get(i).getCreated_at()));
        holder.binding.getRoot().setOnClickListener(view -> listener.onItemClick(view,i,list.get(i)));

        holder.binding.CallUser.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+list.get(i).getNumber()));
            context.startActivity(intent);

        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        ItemUserListBinding binding;

        ViewHolder(@NonNull ItemUserListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }

}