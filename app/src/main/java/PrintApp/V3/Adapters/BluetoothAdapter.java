package PrintApp.V3.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import PrintApp.V3.BluetoothPrinter.BluetoothUtils;
import PrintApp.V3.R;

public class BluetoothAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayoutResource;
    private List<BluetoothDevice> bluetoothDevices;

    public BluetoothAdapter(@NonNull Context context, int resource) {
        mContext = context;
        mLayoutResource = resource;
        this.bluetoothDevices = new ArrayList<>();
    }

    public void addAll(List<BluetoothDevice> devices){
        this.bluetoothDevices.addAll(devices);
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return this.bluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDevice device = (BluetoothDevice) getItem(position);

        convertView = LayoutInflater.from(mContext).inflate(mLayoutResource,parent, false);

        ImageView imageView = convertView.findViewById(R.id.bluetooth_type);

        if(BluetoothUtils.isBluetoothPrinter(device)){
            imageView.setImageResource(R.drawable.printer);
        }

        TextView txtName = convertView.findViewById(R.id.bluetooth_name);
        txtName.setText(device.getName());

        TextView txtAddress = convertView.findViewById(R.id.mac_address);
        txtAddress.setText(device.getAddress());


        return convertView;
    }

}
