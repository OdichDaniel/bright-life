package com.matchstick.brightlife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matchstick.brightlife.R;
import com.matchstick.brightlife.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notificationList.get(position);
        holder.notificatinoTitle.setText(notification.getTitle());
        holder.notificationTime.setText(notification.getNotificaitonTime());
        holder.notificationText.setText(notification.getNotificationText());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificatinoTitle, notificationTime, notificationText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificatinoTitle = (TextView) itemView.findViewById(R.id.notification_title);
            notificationTime = (TextView) itemView.findViewById(R.id.notification_time);
            notificationText = (TextView) itemView.findViewById(R.id.notification_text);
        }
    }
}
