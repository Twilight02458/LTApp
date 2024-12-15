package com.example.ltapp;

import android.content.Intent;
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
import java.util.List;

public class DetailActivity4 extends AppCompatActivity {
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private RatingBar ratingInput;
    private Button btnPostComment;
    private List<Comment> comments;
    private ConstraintLayout btBack;
    private Button btnBook;
    private TextView tvAverageRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail4);
        
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
        btnBook = findViewById(R.id.btn_booking4);
        rvComments = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment);
        ratingInput = findViewById(R.id.rating_input);
        btnPostComment = findViewById(R.id.btn_post_comment);
        tvAverageRating = findViewById(R.id.tv_average_rating);
    }

    private void setupListeners() {
        btBack.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity4.this, MainActivity.class);
            startActivity(intent);
        });

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity4.this, BookingActivity4.class);
            startActivity(intent);
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
                
                updateAverageRating();
                
                Toast.makeText(this, "Đã đăng bình luận", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm một số bình luận mẫu
        addSampleComments();
        updateAverageRating();
    }

    private void addSampleComments() {
        comments.add(new Comment("Lý Văn Q", "Sân rất phù hợp cho người mới chơi!", 5));
        comments.add(new Comment("Ngô Thị R", "Không gian rộng rãi, sạch sẽ", 4.5f));
        comments.add(new Comment("Đinh Văn S", "Nhân viên nhiệt tình, giá cả hợp lý", 4));
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