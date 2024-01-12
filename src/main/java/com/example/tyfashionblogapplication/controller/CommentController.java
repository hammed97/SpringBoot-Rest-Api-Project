package com.example.tyfashionblogapplication.controller;

import com.example.tyfashionblogapplication.DTO.CommentRequestDto;
import com.example.tyfashionblogapplication.DTO.EditCommentRequestDto;
import com.example.tyfashionblogapplication.DTO.EditPostRequestDto;
import com.example.tyfashionblogapplication.model.Comment;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.serviceImpl.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public class CommentController {
    private CommentServiceImpl commentService;
    @Autowired
    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }
    @PostMapping("/create-comment/{userId}/{postId}")
    public ResponseEntity<Comment> createCommentByPostId(@RequestBody CommentRequestDto commentRequestDto,
                                                         @PathVariable Long userId,
                                                         @PathVariable Long postId){
        return commentService.createComment(commentRequestDto, userId,postId);
    }
    @PutMapping("/edit-comment/{userId}/{commentId}")
    public ResponseEntity<Comment> editCommentByPostId(@RequestBody EditCommentRequestDto editCommentRequestDto,
                                                   @PathVariable Long userId,
                                                   @PathVariable Long commentId){
        return commentService.editComment(editCommentRequestDto, userId,commentId);
    }
    @GetMapping("/find-comment-postId/{postId}")
    public ResponseEntity <List<Comment>> findAllCommentByPostId(@PathVariable Long postId){
        return commentService.findAllCommentById(postId);
    }
    @GetMapping("/all-comment")
    public ResponseEntity<List<Comment>> getAllComment(){
        return commentService.getAllComment();
    }
    @GetMapping("/get-comment/{content}")
    public ResponseEntity<List<Comment>> searchCommentByContent(@PathVariable String content){
        return commentService.findCommentByContent(content);
    }
    @DeleteMapping("/delete-comment/{commentId}/{userId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long commentId, @PathVariable Long userId){
        return commentService.deleteComment(commentId, userId);
    }
    @PutMapping("/like-comment/{id}")
    public ResponseEntity<Comment> likeCommentById(@PathVariable Long id){
        return commentService.likeCommentById(id);
    }
    @PutMapping("/unlike-comment/{id}")
    public ResponseEntity<Comment> unlikeCommentById(@PathVariable Long id){
        return commentService.unlikeCommentById(id);
    }
}
