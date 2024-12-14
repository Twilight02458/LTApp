package com.example.ltapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.RatingBar;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private RatingBar ratingInput;
    private Button btnPostComment;
    private List<Comment> comments;
    private DatabaseHelper dbHelper;
    private static final int COURT_ID = 1; // ID của sân hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ConstraintLayout back = findViewById(R.id.btBack);
        Button book1 = findViewById(R.id.btn_book);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        book1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
        dbHelper = new DatabaseHelper(this);
        initCommentSection();
        loadComments(); // Tải comments từ database
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCommentSection() {
        rvComments = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment);
        ratingInput = findViewById(R.id.rating_input);
        btnPostComment = findViewById(R.id.btn_post_comment);

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);

        btnPostComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            float rating = ratingInput.getRating();
            
            if (!content.isEmpty()) {
                // Lưu comment vào database
                long result = dbHelper.addComment("Người dùng", content, rating, COURT_ID);
                
                if (result != -1) {
                    Comment newComment = new Comment("Người dùng", content, rating);
                    commentAdapter.addComment(newComment);
                    
                    // Clear input
                    etComment.setText("");
                    ratingInput.setRating(0);
                    
                    Toast.makeText(this, "Đã đăng bình luận", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi đăng bình luận", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadComments() {
        // Lấy danh sách comments từ database
        List<Comment> savedComments = dbHelper.getComments(COURT_ID);
        
        // Thêm vào adapter
        for (Comment comment : savedComments) {
            commentAdapter.addComment(comment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
