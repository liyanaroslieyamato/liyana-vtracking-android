package yamato.vtracking.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yamato.vtracking.R;
import yamato.vtracking.config.AppConfig;
import yamato.vtracking.models.LoginStatus;
import yamato.vtracking.network.HttpClient;
import yamato.vtracking.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private static final String MESSAGE_LOGIN_SUCCESS = "Login Successfully!";
    private static final String MESSAGE_LOGIN_NO_ACCESS = "Sorry this account can't access!";
    private static final String MESSAGE_LOGIN_FAIL = "Failed to login.";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private static final int NETWORK_PERMISSION = 2;

    private static final String LOGIN_URL = AppConfig.ENDPOINT + "/hpapi/check_id.json";

    private final int REQUEST_CODE = 20;
    private final Gson gson = new Gson();

    private OkHttpClient client = HttpClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check for network permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, NETWORK_PERMISSION);
        }
    }

    public void launchScannerActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void buttonClicked(View view) {
        this.launchScannerActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String loginData = data.getExtras().getString("data");
            doLogin(loginData);
        }
    }

    private void doLogin(String userId) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, NETWORK_PERMISSION);
            return;
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(LOGIN_URL).newBuilder();
        urlBuilder.addQueryParameter("work_id", userId);

        Request request = new Request.Builder()
                .header("Authorization", AppConfig.TOKEN)
                .url(urlBuilder.build().toString()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Run view-related code back on the main thread
                Log.e("ERROR", "Failed to login.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), MESSAGE_LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final LoginStatus userStatus = gson.fromJson(response.body().charStream(), LoginStatus.class);
                if (userStatus.getStatus().equals("exist")) {
                    // save current user
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginSuccessHandler(userStatus);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLoginFailAlert();
                        }
                    });
                }

            }
        });
    }

    public void loginSuccessHandler(final LoginStatus userStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle(MESSAGE_LOGIN_SUCCESS);
        builder.setMessage("Login as " + userStatus.getName());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                SessionManager.instance(getApplicationContext())
                        .setLogin(userStatus.getHandsetUserId(), userStatus.getWorkId(),
                                userStatus.getName());

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void showLoginFailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle(MESSAGE_LOGIN_NO_ACCESS);
        builder.setMessage("Please try again");
        builder.setPositiveButton("OK", null);
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Request for Camera access
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;

            case NETWORK_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please grant Internet permission for data exchange with the server.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
