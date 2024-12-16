package com.example.ltapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ltapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private ListView listViewFavorites;
    private ArrayList<String> favoritesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("FavoritePrefs", MODE_PRIVATE);
        listViewFavorites = findViewById(R.id.listViewFavorites);
        favoritesList = new ArrayList<>(sharedPreferences.getStringSet("favorites", new HashSet<>()));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritesList);
        listViewFavorites.setAdapter(adapter);
        ConstraintLayout back8 = findViewById(R.id.btBack);

        
        // Xử lý sự kiện click item trong ListView để xóa
        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy item được chọn
            String selectedItem = favoritesList.get(position);

            // Giả sử bạn đã lưu `id` và các thông tin liên quan vào String dạng "id|info"
            String[] parts = selectedItem.split("\\|"); // Tách chuỗi dựa trên ký tự '|'
            String sanBongId = parts[0]; // id là phần đầu tiên
            String sanBongInfo = parts[1]; // Thông tin sân bóng

            switch(sanBongId) {
                case "0":
                    Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                    startActivity(intent);
                    break;
                case "1":
                    Intent intent1 = new Intent(FavoriteActivity.this, DetailActivity1.class);
                    startActivity(intent1);
                    break;


            }
        });
        listViewFavorites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                favoritesList.remove(position);

                // Cập nhật SharedPreferences
                Set<String> updatedFavorites = new HashSet<>(favoritesList);
                sharedPreferences.edit().putStringSet("favorites", updatedFavorites).apply();

                // Cập nhật ListView
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        binding.bottomMenu.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.btHome) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.btYeuthich) {
                Intent intent = new Intent(FavoriteActivity.this, FavoriteActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.btUsers) {
                Intent intent = new Intent(FavoriteActivity.this, UsersActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
            return true;
        });
        back8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại activity
        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());
        favoritesList.clear();
        favoritesList.addAll(favorites);
        adapter.notifyDataSetChanged();
    }
}