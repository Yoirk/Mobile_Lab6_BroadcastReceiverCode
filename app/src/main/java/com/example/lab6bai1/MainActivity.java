package com.example.lab6bai1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest; // Import required for permissions

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView txtSender, txtMessage;
    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtSender = findViewById(R.id.txtSender);
        txtMessage = findViewById(R.id.txtMessage);

        // Kiểm tra và yêu cầu quyền nhận SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSION_REQUEST_CODE);
        }

        // Tạo và đăng ký BroadcastReceiver
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký receiver khi activity bị tắt
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }

    // BroadcastReceiver để nhận SMS
    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];

                String sender = "";
                StringBuilder messageBody = new StringBuilder();

                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sender = messages[i].getOriginatingAddress();  // Số điện thoại gửi
                    messageBody.append(messages[i].getMessageBody());  // Nội dung tin nhắn
                }

                // Hiển thị thông báo Toast
                Toast.makeText(context, "Có tin nhắn mới từ: " + sender, Toast.LENGTH_LONG).show();

                // Hiển thị thông tin tin nhắn lên UI
                txtSender.setText("SĐT gửi: " + sender);
                txtMessage.setText("Nội dung: " + messageBody.toString());
            }
        }
    }
}