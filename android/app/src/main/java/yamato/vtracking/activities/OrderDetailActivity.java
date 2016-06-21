package yamato.vtracking.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yamato.vtracking.R;
import yamato.vtracking.config.AppConfig;
import yamato.vtracking.models.Order;
import yamato.vtracking.models.StatusUpdateResponse;
import yamato.vtracking.network.HttpClient;
import yamato.vtracking.utils.SessionManager;

public class OrderDetailActivity extends AppCompatActivity implements LocationListener {
    private Order order;
    private TextView senderLabel;
    private TextView receiverLabel;
    private TextView cod_amountLabel;
    private TextView typeLabel;
    private TextView paymentModeLabel;
    private TextView addressLabel;
    private TextView contactLabel;
    private TextView postcodeLabel;
    private TextView oRemarksLabel;
    private TextView exchangeCodeLabel;
    private TextView trackingLabel;
    private EditText remarkContentEditText;
    private OkHttpClient client = HttpClient.getInstance();

    private final Gson gson = new Gson();

    private static final String UPDATE_ORDER_STATUS_URL = AppConfig.ENDPOINT + "/hpapi/lswtal.json";

    private static final int NETWORK_PERMISSION = 2;
    private static final int LOCATION_PERMISSION = 3;

    private static final int REQUEST_EXCHANGE_CODE = 20;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private RelativeLayout progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        this.order = (Order) i.getSerializableExtra("ORDER");

        progressBar = (RelativeLayout) findViewById(R.id.progressBarLayout);

        bindViews();
        bindData();
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

    private void bindViews() {
        trackingLabel = (TextView) findViewById(R.id.tracking_number);
        senderLabel = (TextView) findViewById(R.id.sender);
        receiverLabel = (TextView) findViewById(R.id.receiver);
        cod_amountLabel = (TextView) findViewById(R.id.cod_amount);
        typeLabel = (TextView) findViewById(R.id.order_type);
        paymentModeLabel = (TextView) findViewById(R.id.cod_amount_title);
        addressLabel = (TextView) findViewById(R.id.address);
        contactLabel = (TextView) findViewById(R.id.contact);
        postcodeLabel = (TextView) findViewById(R.id.postCode);
        oRemarksLabel = (TextView) findViewById(R.id.o_remark);
        exchangeCodeLabel = (TextView) findViewById(R.id.exchange_code);
        remarkContentEditText = (EditText) findViewById(R.id.remark_content);
    }

    private void bindData() {
        trackingLabel.setText(order.getDoNumber());
        senderLabel.setText(order.getSName());
        receiverLabel.setText(order.getRName());
        cod_amountLabel.setText(order.getCodAmount());
        typeLabel.setText(order.getOrderType());

        if (order.getPaymentTerm() != null) {
            paymentModeLabel.setText(String.format("%s Amount", order.getPaymentTerm()));
        } else {
            paymentModeLabel.setText(" Amount");
        }

        if (order.getRAddress1() == null) {
            order.setRAddress1("-");
        }
        if (order.getRAddress2() == null) {
            order.setRAddress2("-");
        }
        if (order.getRAddress3() == null) {
            order.setRAddress3("-");
        }
        addressLabel.setText(String.format("%s, %s, %s", order.getRAddress1(), order.getRAddress2(), order.getRAddress3()));
        if (order.getRContactNumber1() == null) {
            order.setRContactNumber1("-");
        }
        if (order.getRContactNumber2() == null) {
            order.setRContactNumber2("-");
        }
        contactLabel.setText(String.format("%s, %s", order.getRContactNumber1(), order.getRContactNumber2()));

        postcodeLabel.setText(order.getRPostcode());
        oRemarksLabel.setText(order.getRemarks());
        exchangeCodeLabel.setText(order.getExchangeCode());
    }

    public void btn_updateOrderStatus(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_return:
                updateOrderStatus(9);
                break;
            case R.id.btn_undelivered:
                String remarksContent = this.remarkContentEditText.getText().toString();
                if (remarksContent == null || remarksContent.isEmpty()) {
                    showAlert("Warning", "The Remarks should NOT be EMPTY!", false);
                    return;
                }
                updateOrderStatus(7);
                break;
            case R.id.btn_delivered:
                if (order.getLatestStatus().equals("Exchange")) {
                    if (order.getExchangeCode().trim().isEmpty()) {
                        showAlert("Error", "This order can't be delivered without exchange", false);
                        return;
                    }
                }

                showDeliveredConfirmation("Delivered Confirm?", String.format("Tracking Number: %s Delivered.", order.getDoNumber()));
                break;
            case R.id.btn_pickup:
                updateOrderStatus(6);
                break;
            case R.id.btn_warehouse:
                updateOrderStatus(10);
                break;
            case R.id.btn_outForDelivery:
                updateOrderStatus(2);
                break;
            case R.id.btn_exchange:
                Intent exchangeViewIntent = new Intent(getApplicationContext(), ExchangeActivity.class);
                exchangeViewIntent.putExtra("tracking_number", order.getDoNumber());
                startActivityForResult(exchangeViewIntent, REQUEST_EXCHANGE_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_EXCHANGE_CODE) {
            String exchangeCode = data.getExtras().getString("ExchangeCode");
            String exchangeRemark = data.getExtras().getString("ExchangeRemark");
            // received data from Exchange view
            if (exchangeCode != null) {
                exchangeCodeLabel.setText(exchangeCode);
            }
            if (exchangeRemark != null) {
                remarkContentEditText.setText(exchangeRemark);
            }

            updateOrderStatus(11);
        }
    }

    public Location getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return null;
        }
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void updateOrderStatus(int statusId) {
        String addressLocation = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, NETWORK_PERMISSION);
            return;
        }

        Location currentLocation = this.getLocation();

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strReturnedAddress.append(returnedAddress.getCountryName()).append("\n");
                strReturnedAddress.append(returnedAddress.getPostalCode());
                addressLocation = strReturnedAddress.toString();
            }
            else {
                Toast.makeText(getApplicationContext(), "No address returned", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Cannot get address", Toast.LENGTH_SHORT).show();
        }

        if (currentLocation == null) {
            Toast.makeText(getApplicationContext(), "Cannot Update Order due to NO GPS access.", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(UPDATE_ORDER_STATUS_URL).newBuilder();
        urlBuilder.addQueryParameter("do_number", this.order.getDoNumber());
        urlBuilder.addQueryParameter("status_id", "" + statusId);
        urlBuilder.addQueryParameter("responsible", SessionManager.instance(this.getApplicationContext()).getWorkId());
        urlBuilder.addQueryParameter("handset_user_id", SessionManager.instance(this.getApplicationContext()).getHandSetUserId());
        urlBuilder.addQueryParameter("longitude", "" + currentLocation.getLongitude());
        urlBuilder.addQueryParameter("latitude", "" + currentLocation.getLatitude());
        urlBuilder.addQueryParameter("location", addressLocation);
        urlBuilder.addQueryParameter("exchange_code", this.exchangeCodeLabel.getText().toString());
        urlBuilder.addQueryParameter("remarks", this.remarkContentEditText.getText().toString());

        startLoading();
        Request request = new Request.Builder()
                .header("Authorization", AppConfig.TOKEN)
                .url(urlBuilder.build().toString()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Run view-related code back on the main thread
                Log.e("ERROR", "Failed to update order.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopLoading();
                        Toast.makeText(getApplicationContext(), "Cannot Update Order.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final StatusUpdateResponse statusUpdateResponse = gson.fromJson(response.body().charStream(), StatusUpdateResponse.class);
                if (statusUpdateResponse.getStatus().equals("success")) {
                    // save current user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert("Success", "The Order Updated.", true);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert("Error", "Some error happened, Please try again.", true);
                        }
                    });
                }
            }
        });
    }

    public void showAlert(String title, String message, boolean isFinished) {
        stopLoading();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", isFinished ? new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        } : null);
        builder.setCancelable(false);
        builder.show();
    }

    public void showDeliveredConfirmation(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateOrderStatus(3);
            }
        });
        builder.setNeutralButton("Cancel", null);
        builder.setCancelable(true);
        builder.show();
    }

    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Request for Network access
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case NETWORK_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please grant Internet permission for data exchange with the server.", Toast.LENGTH_SHORT).show();
                }
                return;
            case LOCATION_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please grant GPS permission for the APP.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}