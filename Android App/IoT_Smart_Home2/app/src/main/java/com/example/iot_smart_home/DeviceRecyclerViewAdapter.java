package com.example.iot_smart_home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceIconMap;
import com.example.iot_smart_home.utils.device.DeviceList;

import java.util.List;


public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private List<Device> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private DeviceIconMap deviceMap;

    // data is passed into the constructor
    DeviceRecyclerViewAdapter(Context context, List<Device> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        this.deviceMap = new DeviceIconMap();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device = mData.get(position);
        holder.deviceName.setText(device.name);
        holder.deviceType.setImageResource(deviceMap.get(device.type));

        holder.deviceMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.deviceMenu = new PopupMenu(holder.deviceMore.getContext(), holder.deviceMore);
                holder.deviceMenu.getMenuInflater().inflate(R.menu.device_menu, holder.deviceMenu.getMenu());
                holder.deviceMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Delete")) {
                            holder.devices.delete(holder.deviceName.getText().toString());
                        }
                        return true;
                    }
                });
                holder.deviceMenu.show();
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        ImageView deviceType;
        ImageView deviceMore;
        PopupMenu deviceMenu;

        DeviceList devices = DeviceList.INSTANCE;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceType = itemView.findViewById(R.id.deviceType);
            deviceMore = itemView.findViewById(R.id.deviceMore);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            } catch (NullPointerException e) {
                Log.e("ERROR ONCLICK IN DEVICERECYCLEVIEWADAPTER", "Null pointer here. View: " + view.toString() + " mClickListener: " + mClickListener.toString());
                e.printStackTrace();
            }
        }
    }

    // convenience method for getting data at click position
    Device getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}