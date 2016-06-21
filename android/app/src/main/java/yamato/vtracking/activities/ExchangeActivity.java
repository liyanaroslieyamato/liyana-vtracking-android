package yamato.vtracking.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import yamato.vtracking.R;
import yamato.vtracking.network.HttpClient;

public class ExchangeActivity extends AppCompatActivity {

    private static final int REQUEST_SCANNER_CODE = 21;
    private TextView trackingNoLabel;
    private EditText remarksField;
    private EditText exchangeCodeField;

    private String trackingNumber;

    private OkHttpClient client = HttpClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        Intent i = getIntent();
        trackingNumber =  i.getStringExtra("tracking_number");

        trackingNoLabel = (TextView) findViewById(R.id.tracking_number);
        trackingNoLabel.setText(trackingNumber);

        remarksField = (EditText) findViewById(R.id.remark);
        exchangeCodeField = (EditText) findViewById(R.id.exchange_code);
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

            // check scanned data
            exchangeCodeField.setText("");

            if (scannedCode != null && !scannedCode.startsWith("YE")) {
                // invalid code
                showAlert("Warning", String.format("%s is not an Exchange Code, Please scan again.", scannedCode));
            } else {
                exchangeCodeField.setText(scannedCode.trim());
            }
        }
    }

    public void onClickScan(View view) {
        launchScannerActivity();
    }

    public void onClickCancel(View view) {
        // Activity cancel
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onClickConfirmed(View view) {
        if (exchangeCodeField.getText().toString().trim().isEmpty()) {
            showAlert("Error", "The Exchange Code can't be EMPTY!");
            return;
        }

        String exchangeCode = exchangeCodeField.getText().toString();
        String remark = remarksField.getText().toString();
        String trackingNumber = trackingNoLabel.getText().toString();
        showConfirmation("Exchange Confirm", String.format("Tracking Number: %s\n Exchange Code: %s\n Remarks: %s", trackingNumber, exchangeCode, remark));
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setCancelable(false);
        builder.show();
    }

    public void showConfirmation(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent data = new Intent();
                data.putExtra("ExchangeCode", exchangeCodeField.getText().toString());
                data.putExtra("ExchangeRemark", remarksField.getText().toString());
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish();
            }
        });
        builder.setNeutralButton("Cancel", null);
        builder.setCancelable(true);
        builder.show();
    }
}
