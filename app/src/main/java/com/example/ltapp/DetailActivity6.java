package com.example.ltapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailActivity6 extends AppCompatActivity {
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private RatingBar ratingInput;
    private Button btnPostComment;
    private List<Comment> comments;
    private ConstraintLayout btBack;
    private Button btnYeuThich;
    private Button btnBook;
    private TextView tvAverageRating;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail6);
        
        initViews();
        setupListeners();
        initCommentSection();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btBack = findViewById(R.id.btBack);
        btnBook = findViewById(R.id.btn_booking6);
        btnYeuThich = findViewById(R.id.btn_yeuthich6);
        rvComments = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment);
        ratingInput = findViewById(R.id.rating_input);
        btnPostComment = findViewById(R.id.btn_post_comment);
        tvAverageRating = findViewById(R.id.tv_average_rating);
        sharedPreferences = getSharedPreferences("FavoritePrefs", MODE_PRIVATE);
    }

    private void setupListeners() {
        btBack.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity6.this, MainActivity.class);
            startActivity(intent);
        });

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity6.this, BookingActivity6.class);
            startActivity(intent);
        });
        btnYeuThich.setOnClickListener(v -> {
            String sanBongInfo = "Sân bóng đá Thăng Long - 7.000đ/giờ";
            String sanBongData = sanBongInfo;
            Set<String> favorites = new HashSet<>(
                    sharedPreferences.getStringSet("favorites", new HashSet<>())
            );

            if (favorites.contains(sanBongData)) {
                favorites.remove(sanBongData);
                Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                btnYeuThich.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            } else {
                favorites.add(sanBongData);
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                btnYeuThich.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
            }

            sharedPreferences.edit().putStringSet("favorites", favorites).apply();
        });
    }

    private void initCommentSection() {
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);

        btnPostComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            float rating = ratingInput.getRating();
            
            if (!content.isEmpty()) {
                Comment newComment = new Comment("Người dùng", content, rating);
                commentAdapter.addComment(newComment);
                
                // Clear input
                etComment.setText("");
                ratingInput.setRating(0);
                
                // Cập nhật rating trung bình
                updateAverageRating();
                
                Toast.makeText(this, "Đã đăng bình luận", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm một số bình luận mẫu
        addSampleComments();
        // Cập nhật rating trung bình ban đầu
        updateAverageRating();
    }

    private void addSampleComments() {
        comments.add(new Comment("Trương Văn W", "Sân tennis chất lượng cao!", 5));
        comments.add(new Comment("Lê Thị X", "Huấn luyện viên chuyên nghiệp", 4.5f));
        comments.add(new Comment("Đặng Văn Y", "Thiết bị đầy đủ, hiện đại", 4));
        commentAdapter.notifyDataSetChanged();
    }

    private void updateAverageRating() {
        float totalRating = 0;
        for (Comment comment : comments) {
            totalRating += comment.getRating();
        }
        float averageRating = comments.isEmpty() ? 0 : totalRating / comments.size();
        String formattedRating = String.format("%.1f", averageRating);
        tvAverageRating.setText(formattedRating + " ★");
    }
}