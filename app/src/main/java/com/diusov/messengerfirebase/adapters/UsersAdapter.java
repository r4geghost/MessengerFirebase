package com.diusov.messengerfirebase.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diusov.messengerfirebase.R;
import com.diusov.messengerfirebase.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    private List<User> users = new ArrayList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.user_item,
                parent,
                false
        );
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = users.get(position);
        String userInfo = String.format(
                "%s %s, %s",
                user.getName(),
                user.getLastName(),
                user.getAge()
        );
        holder.textViewUserInfo.setText(userInfo);
        // saving background resource id
        int bgResId;
        if (user.getOnline()) {
            bgResId = R.drawable.circle_green;
        } else {
            bgResId = R.drawable.circle_red;
        }
        Drawable onlineStatusBg = ContextCompat.getDrawable(holder.itemView.getContext(), bgResId);
        holder.imageViewOnlineStatus.setImageDrawable(onlineStatusBg);

        // handle on user clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    protected static class UserHolder extends RecyclerView.ViewHolder {

        private TextView textViewUserInfo;
        private ImageView imageViewOnlineStatus;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserInfo = itemView.findViewById(R.id.textViewUserInfo);
            imageViewOnlineStatus = itemView.findViewById(R.id.imageViewOnlineStatus);
        }
    }
}
