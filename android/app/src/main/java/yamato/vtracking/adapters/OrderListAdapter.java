package yamato.vtracking.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import yamato.vtracking.R;
import yamato.vtracking.models.Order;

import static yamato.vtracking.R.color.order_default;
import static yamato.vtracking.R.color.order_in_transit;
import static yamato.vtracking.R.color.order_return_to_sender;
import static yamato.vtracking.R.color.order_tidiki_sorting_hub;
import static yamato.vtracking.R.color.order_uploaded;

/**
 * Created by Gison on 9/4/16.
 */
public class OrderListAdapter extends ArrayAdapter<Order> {
    public OrderListAdapter(Context context, ArrayList<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
        }
        // Lookup view for data population
        TextView doNumber = (TextView) convertView.findViewById(R.id.order_do_number);
        TextView status = (TextView) convertView.findViewById(R.id.order_status);
        TextView address = (TextView) convertView.findViewById(R.id.order_address);
        // Populate the data into the template view using the data object
        doNumber.setText(order.getDoNumber());
        status.setText(order.getLatestStatus());
        address.setText(order.getRAddress1());
        if("Order Uploaded".equalsIgnoreCase(order.getLatestStatus())) {
            status.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), order_uploaded));
        } else if("In Transit".equalsIgnoreCase(order.getLatestStatus())) {
            status.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), order_in_transit));
        } else if("Return to Sender".equalsIgnoreCase(order.getLatestStatus())) {
            status.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), order_return_to_sender));
        } else if("Tidiki Sorting Hub".equalsIgnoreCase(order.getLatestStatus())) {
            status.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), order_tidiki_sorting_hub));
        } else {
            status.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), order_default));
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
