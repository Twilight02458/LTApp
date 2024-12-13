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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.res.ColorStateList;
import android.widget.Toast;

public class BookingActivity extends AppCompatActivity {
    private Button btnDay1, btnDay2, btnDay3, btnDay4;
    private Button selectedButton = null;
    private Set<Button> selectedTimeButtons = new HashSet<>();
    private Button[] timeButtons = new Button[6];
    private Button btnBook;
    private TextView txtTotalPrice;
    private int defaultButtonColor;
    private static final int PRICE_PER_SLOT = 106000; // Giá tiền cho mỗi múi giờ
    private int totalPrice = 0;
    private ConstraintLayout btBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);


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

        // Set ngày cho các button
        setDatesForButtons();

        // Xử lý sự kiện click cho các button ngày
        View.OnClickListener dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                
                if (clickedButton == selectedButton) {
                    // Trở về màu ban đầu khi bỏ chọn
                    clickedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    clickedButton.setTextColor(getResources().getColor(R.color.blue));
                    selectedButton = null;
                    // Reset tổng tiền khi bỏ chọn ngày
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
                }

                // Reset tất cả các nút giờ về trạng thái unselected
                for (Button timeButton : timeButtons) {
                    timeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    timeButton.setTextColor(getResources().getColor(R.color.blue));
                }
                selectedTimeButtons.clear();
                
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

        // Set click listener cho các button ngày
        btnDay1.setOnClickListener(dateClickListener);
        btnDay2.setOnClickListener(dateClickListener);
        btnDay3.setOnClickListener(dateClickListener);
        btnDay4.setOnClickListener(dateClickListener);

        // Set click listener cho các button giờ
        for (Button timeButton : timeButtons) {
            timeButton.setOnClickListener(timeClickListener);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        
        // Thêm xử lý click cho nút Book
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBooking();
            }
        });
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
        // Kiểm tra xem đã chọn ngày và ít nhất 1 múi giờ chưa
        boolean isDateAndTimeSelected = (selectedButton != null && !selectedTimeButtons.isEmpty());
        
        // Enable/disable nút đặt sân
        btnBook.setEnabled(isDateAndTimeSelected);
        
        // Cập nhật hiển thị giá tiền
        if (totalPrice > 0) {
            String formattedPrice = String.format(Locale.getDefault(), "%,d", totalPrice);
            txtTotalPrice.setText("Thành tiền: " + formattedPrice + "đ");
        } else {
            txtTotalPrice.setText("Thành tiền: 0đ");
        }
    }

    private void saveBooking() {
        if (selectedButton != null && !selectedTimeButtons.isEmpty()) {
            // Lấy ngày đã chọn
            String selectedDate = selectedButton.getText().toString();
            
            // Tạo chuỗi chứa các múi giờ đã chọn
            StringBuilder timeSlots = new StringBuilder();
            for (Button timeButton : selectedTimeButtons) {
                if (timeSlots.length() > 0) {
                    timeSlots.append(", ");
                }
                timeSlots.append(timeButton.getText().toString());
            }
            
            // Lưu vào database (court_number = 0 cho sân chính)
            long result = dbHelper.addBooking(
                selectedDate,
                timeSlots.toString(),
                totalPrice,
                0
            );
            
            if (result != -1) {
                // Thông báo đặt sân thành công
                Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Thông báo lỗi
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}