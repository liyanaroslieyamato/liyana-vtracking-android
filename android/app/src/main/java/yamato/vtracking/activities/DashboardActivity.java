package yamato.vtracking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import yamato.vtracking.R;
import yamato.vtracking.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private TextView label_userName;
    private TextView label_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bindViews();
        bindData();
    }

    private void bindViews() {
        this.label_userName = (TextView)this.findViewById(R.id.label_userName);
        this.label_date = (TextView) this.findViewById(R.id.label_date);
    }

    private void bindData() {
        this.label_userName.setText(SessionManager.instance(this.getApplicationContext()).getUserName());
        this.label_date.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
    }

    public void goToOrderBase(View view) {
        Intent intent = new Intent(DashboardActivity.this, OrderBaseActivity.class);
        startActivity(intent);
    }
}
