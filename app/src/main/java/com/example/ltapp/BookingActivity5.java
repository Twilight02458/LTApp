package com.example.ltapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import android.os.StrictMode;
import android.util.Log;
import com.example.ltapp.Api.CreateOrder;
import org.json.JSONObject;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.res.ColorStateList;
import android.widget.Toast;

public class BookingActivity5 extends AppCompatActivity {
    private Button btnDay1, btnDay2, btnDay3, btnDay4;
    private Button selectedButton = null;
    private Set<Button> selectedTimeButtons = new HashSet<>();
    private Button[] timeButtons = new Button[6];
    private Button btnBook;
    private TextView txtTotalPrice;
    private int defaultButtonColor;
    private static final int PRICE_PER_SLOT = 59000; // Giá tiền cho mỗi múi giờ
    private int totalPrice = 0;
    private ConstraintLayout btBack;
    private DatabaseHelper dbHelper;
    private Map<Button, Set<Button>> dateTimeSelections = new HashMap<>();
    private Map<Button, Integer> dateTotalPrices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        // Thêm khởi tạo ZaloPay SDK
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDay1 = findViewById(R.id.btnDay1);
        btnDay2 = findViewById(R.id.btnDay2);
        btnDay3 = findViewById(R.id.btnDay3);
        btnDay4 = findViewById(R.id.btnDay4);

        defaultButtonColor = btnDay1.getBackgroundTintList().getDefaultColor();

        timeButtons[0] = findViewById(R.id.btnTime1);
        timeButtons[1] = findViewById(R.id.btnTime2);
        timeButtons[2] = findViewById(R.id.btnTime3);
        timeButtons[3] = findViewById(R.id.btnTime4);
        timeButtons[4] = findViewById(R.id.btnTime5);
        timeButtons[5] = findViewById(R.id.btnTime6);

        btnBook = findViewById(R.id.btnBook);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        setDatesForButtons();

        // Ẩn tất cả các nút múi giờ khi khởi tạo
        for (Button timeButton : timeButtons) {
            timeButton.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                
                // Lưu lựa chọn hiện tại trước khi chuyển ngày
                if (selectedButton != null) {
                    dateTimeSelections.put(selectedButton, new HashSet<>(selectedTimeButtons));
                    dateTotalPrices.put(selectedButton, totalPrice);
                }

                if (clickedButton == selectedButton) {
                    // Trở về màu ban đầu khi bỏ chọn
                    clickedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    clickedButton.setTextColor(getResources().getColor(R.color.blue));
                    selectedButton = null;
                    selectedTimeButtons.clear();
                    totalPrice = 0;
                } else {
                    // Đặt lại màu cho button trước đó
                    if (selectedButton != null) {
                        selectedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                        selectedButton.setTextColor(getResources().getColor(R.color.blue));
                    }
                    // Đặt màu mới cho button được click
                    clickedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    clickedButton.setTextColor(getResources().getColor(android.R.color.white));
                    selectedButton = clickedButton;

                    // Khôi phục lựa chọn cho ngày được chọn
                    selectedTimeButtons.clear();
                    if (dateTimeSelections.containsKey(clickedButton)) {
                        selectedTimeButtons.addAll(dateTimeSelections.get(clickedButton));
                        totalPrice = dateTotalPrices.get(clickedButton);
                    } else {
                        totalPrice = 0;
                    }
                }

                // Cập nhật hiển thị của các nút giờ
                updateTimeButtonsVisibility();

                // Reset hiển thị của tất cả các nút giờ
                for (Button timeButton : timeButtons) {
                    timeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    timeButton.setTextColor(getResources().getColor(R.color.blue));
                }

                // Khôi phục trạng thái cho các nút giờ đã chọn
                for (Button timeButton : selectedTimeButtons) {
                    timeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    timeButton.setTextColor(getResources().getColor(android.R.color.white));
                }

                updateBookButtonAndPrice();
            }
        };

        View.OnClickListener timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;

                if (selectedTimeButtons.contains(clickedButton)) {
                    // Bỏ chọn time slot
                    clickedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    clickedButton.setTextColor(getResources().getColor(R.color.blue));
                    selectedTimeButtons.remove(clickedButton);
                    // Giảm tổng tiền khi bỏ chọn múi giờ
                    totalPrice -= PRICE_PER_SLOT;
                } else {
                    // Thêm time slot mới
                    clickedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    clickedButton.setTextColor(getResources().getColor(android.R.color.white));
                    selectedTimeButtons.add(clickedButton);
                    // Tăng tổng tiền khi chọn thêm múi giờ
                    totalPrice += PRICE_PER_SLOT;
                }
                updateBookButtonAndPrice();
            }
        };

        btnDay1.setOnClickListener(dateClickListener);
        btnDay2.setOnClickListener(dateClickListener);
        btnDay3.setOnClickListener(dateClickListener);
        btnDay4.setOnClickListener(dateClickListener);

        for (Button timeButton : timeButtons) {
            timeButton.setOnClickListener(timeClickListener);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thay đổi xử lý click cho nút Book
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZaloPay", "Button clicked");
                CreateOrder orderApi = new CreateOrder();
                try {
                    Log.d("ZaloPay", "Creating order...");
                    JSONObject data = orderApi.createOrder(txtTotalPrice.getText().toString());
                    String code = data.getString("return_code");
                    Log.d("ZaloPay", "Return code: " + code);
                    
                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        Log.d("ZaloPay", "Token: " + token);
                        
                        if (token == null || token.isEmpty()) {
                            Log.e("ZaloPay", "Token is null or empty");
                            return;
                        }
                        
                        ZaloPaySDK.getInstance().payOrder(BookingActivity5.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Log.d("ZaloPay", "Payment succeeded");
                                Intent intent1 = new Intent(BookingActivity5.this, PaymentNotification.class);
                                intent1.putExtra("result", "Thanh toán thành công!");
                                startActivity(intent1);
                                saveBooking();
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Log.d("ZaloPay", "Payment canceled");
                                Intent intent1 = new Intent(BookingActivity5.this, PaymentNotification.class);
                                intent1.putExtra("result", "Hủy thanh toán!");
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Log.e("ZaloPay", "Payment error: " + zaloPayError.toString());
                                Intent intent1 = new Intent(BookingActivity5.this, PaymentNotification.class);
                                intent1.putExtra("result", "Lỗi thanh toán!");
                                startActivity(intent1);
                            }
                        });
                    } else {
                        Log.e("ZaloPay", "Invalid return code: " + code);
                    }
                } catch (Exception e) {
                    Log.e("ZaloPay", "Error: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(BookingActivity5.this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Thêm phương thức xử lý kết quả thanh toán
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void setDatesForButtons() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Ngày hiện tại cho button 1
        btnDay1.setText(dateFormat.format(calendar.getTime()));

        // 3 ngày tiếp theo cho các button còn lại
        for (int i = 1; i <= 3; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            switch (i) {
                case 1:
                    btnDay2.setText(dateFormat.format(calendar.getTime()));
                    break;
                case 2:
                    btnDay3.setText(dateFormat.format(calendar.getTime()));
                    break;
                case 3:
                    btnDay4.setText(dateFormat.format(calendar.getTime()));
                    break;
            }
        }
    }

    private void updateBookButtonAndPrice() {
        boolean isDateAndTimeSelected = (selectedButton != null && !selectedTimeButtons.isEmpty());
        btnBook.setEnabled(isDateAndTimeSelected);
        
        if (!selectedTimeButtons.isEmpty() && selectedButton != null) {
            selectedButton.setEnabled(false);
            
            // Tính tổng tiền cho ngày được chọn
            int totalForSelectedDate = selectedTimeButtons.size() * PRICE_PER_SLOT;
            dateTotalPrices.put(selectedButton, totalForSelectedDate);
            
        } else {
            btnDay1.setEnabled(true);
            btnDay2.setEnabled(true);
            btnDay3.setEnabled(true);
            btnDay4.setEnabled(true);
            
            // Xóa giá tiền của ngày không còn được chọn
            if (selectedButton != null) {
                dateTotalPrices.remove(selectedButton);
            }
        }
        
        // Tính tổng tiền cho tất cả các ngày
        int totalAllDays = calculateTotalPriceAllDays();
        if (totalAllDays > 0) {
            String formattedPrice = String.format(Locale.getDefault(), "%,d", totalAllDays);
            txtTotalPrice.setText("Thành tiền: " + formattedPrice + "đ");
        } else {
            txtTotalPrice.setText("Thành tiền: 0đ");
        }
    }

    private void saveBooking() {
        if (selectedButton != null && !selectedTimeButtons.isEmpty()) {
            String selectedDate = selectedButton.getText().toString();
            
            StringBuilder timeSlots = new StringBuilder();
            for (Button timeButton : selectedTimeButtons) {
                if (timeSlots.length() > 0) {
                    timeSlots.append(", ");
                }
                timeSlots.append(timeButton.getText().toString());
            }
            
            int totalAllDays = calculateTotalPriceAllDays();
            
            long result = dbHelper.addBooking(
                selectedDate,
                timeSlots.toString(),
                totalAllDays,
                5,
                "Sân Điện Lực"
            );
            
            if (result != -1) {
                Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int calculateTotalPriceAllDays() {
        int total = 0;
        for (Integer price : dateTotalPrices.values()) {
            total += price;
        }
        return total;
    }

    private void updateTimeButtonsVisibility() {
        int visibility = (selectedButton != null) ? View.VISIBLE : View.INVISIBLE;
        for (Button timeButton : timeButtons) {
            timeButton.setVisibility(visibility);
        }
    }
}