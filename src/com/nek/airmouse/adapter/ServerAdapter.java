package com.nek.airmouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nek.airmouse.R;
import com.nek.airmouse.db.dto.ServerObj;

import java.util.List;

public class ServerAdapter extends ArrayAdapter<ServerObj> {

    private LayoutInflater inflater;

    public ServerAdapter(Context context, List<ServerObj> serverObjs) {
        super(context, R.layout.item_server, serverObjs);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_server, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.populate(getItem(position));

        return convertView;
    }

    class Holder {
        TextView nameText;
        TextView ipText;
        TextView macText;

        public Holder(View v) {
            nameText = (TextView) v.findViewById(R.id.item_server_name_text);
            ipText = (TextView) v.findViewById(R.id.item_server_ip_text);
            macText = (TextView) v.findViewById(R.id.item_server_mac_text);
        }

        public void populate(ServerObj item) {
            nameText.setText(item.getName());
            ipText.setText(item.getIpAddress());
            macText.setText(item.getMacAddress());
        }

    }
}
