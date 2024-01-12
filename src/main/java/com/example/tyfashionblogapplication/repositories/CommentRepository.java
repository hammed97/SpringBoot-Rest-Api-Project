package com.example.tyfashionblogapplication.repositories;

import com.example.tyfashionblogapplication.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByPostId(Long postId);

    List<Comment> findByContentIgnoringCaseContaining(String content);

}
