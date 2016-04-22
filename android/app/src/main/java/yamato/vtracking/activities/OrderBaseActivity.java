package yamato.vtracking.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yamato.vtracking.R;
import yamato.vtracking.adapters.OrderListAdapter;
import yamato.vtracking.config.AppConfig;
import yamato.vtracking.models.LoginStatus;
import yamato.vtracking.models.Order;
import yamato.vtracking.network.HttpClient;

public class OrderBaseActivity extends AppCompatActivity {
    private static final String ORDER_URL = AppConfig.ENDPOINT + "/hpapi/orders.json";
    private static final int REQUEST_SCANNER_CODE = 20;

    private final Gson gson = new Gson();

    private OkHttpClient client = HttpClient.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private OrderListAdapter adapter;
    private ListView orderListView;
    private ArrayList<Order> orderArray;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_base);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpListView();
        setUpSwipeToRefresh();
        setUpScanButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOrderData(false); // fetch data again
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSwipeToRefresh() {
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchOrderData(true);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setUpScanButton() {
        fab = (FloatingActionButton) findViewById(R.id.scan_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchScannerActivity();
            }
        });
    }

    private void setUpListView() {
        // create the adapter
        orderArray = new ArrayList<Order>();
        adapter = new OrderListAdapter(this, orderArray);

        // Loop up list view
        orderListView = (ListView) findViewById(R.id.orderList);
        // attach adapter
        orderListView.setAdapter(adapter);

        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = adapter.getItem(position);
                showOrderDetailActivity(order);
            }
        });
    }

    private void showOrderDetailActivity(Order order) {
        Intent intent = new Intent(OrderBaseActivity.this, OrderDetailActivity.class);
        intent.putExtra("ORDER", order);
        startActivity(intent);
    }

    public void launchScannerActivity() {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, REQUEST_SCANNER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SCANNER_CODE) {
            // received data from scanner view
            String scannedCode = data.getExtras().getString("data");
            Log.d("DEBUG", scannedCode);
            filterOrderList(scannedCode);
        }
    }

    private void filterOrderList(final String scannedResult) {
        Predicate<Order> predicate;
        if (scannedResult.startsWith("YE")) {
            predicate = new Predicate<Order>() {
                public boolean apply(Order obj) {
                    return obj.getExchangeCode().equals(scannedResult);
                }
            };
        } else {
            predicate = new Predicate<Order>() {
                public boolean apply(Order obj) {
                    return obj.getDoNumber().equals(scannedResult);
                }
            };
        }

        Collection<Order> orders = Collections.unmodifiableCollection(orderArray);
        Collection<Order> matchingOrders = Collections2.filter(orders, predicate);

        if (matchingOrders.size() == 0) {
            // try fetch the duplicated "Do-Number"-1
            predicate = new Predicate<Order>() {
                public boolean apply(Order obj) {
                    return obj.getDoNumber().startsWith(scannedResult);
                }
            };
            matchingOrders = Collections2.filter(orders, predicate);
        }

        if (matchingOrders.size() == 0) {
            showAlert("Can't find this DO", scannedResult);
            return;
        }

        if (matchingOrders.size() > 1) {
            showAlert("Something wrong with this DO, please take the screenshot and contact system admin.", scannedResult);
            return;
        }

        Order matchedOrder = matchingOrders.iterator().next();
        showOrderDetailActivity(matchedOrder);
    }

    public void fetchOrderData(final boolean isForSwipeRefresh) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ORDER_URL).newBuilder();
        urlBuilder.addQueryParameter("hpuser", "d"); // TODO: change the correct user?

        Request request = new Request.Builder()
                .header("Authorization", AppConfig.TOKEN)
                .url(urlBuilder.build().toString()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert("Error", "Something wrong when fetching the data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Order[] orders = gson.fromJson(response.body().charStream(), Order[].class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.addAll(orders);
                        if (isForSwipeRefresh) {
                            swipeContainer.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setCancelable(false);
        builder.show();
    }

}
