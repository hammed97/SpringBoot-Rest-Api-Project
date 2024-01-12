package com.example.tyfashionblogapplication.controller;

import com.example.tyfashionblogapplication.DTO.EditPostRequestDto;
import com.example.tyfashionblogapplication.DTO.PostDto;
import com.example.tyfashionblogapplication.DTO.PostRequestDto;
import com.example.tyfashionblogapplication.DTO.PostResponseDto;
import com.example.tyfashionblogapplication.model.Post;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.serviceImpl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private PostServiceImpl postService;
    @Autowired
    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }
    @PostMapping("/create-post/{userId}")
    public ResponseEntity<String> createBlogPost(@RequestBody PostRequestDto postRequestDto, @PathVariable Long userId){
        return postService.createPost(postRequestDto, userId);
    }
//    @PostMapping("/create-post")
//    @PreAuthorize("hasRole(ROLE_ADMIN)")
//    public ResponseEntity<PostResponseDto> createPost(@RequestBody Post newPost, @AuthenticationPrincipal User currentUser){
//        PostResponseDto post = postService.savePost(newPost, currentUser);
//        return new ResponseEntity<>(post, HttpStatus.CREATED);
//    }
    @GetMapping("/all-post")
    public ResponseEntity<List<Post>> getAllPosts(){
        return postService.getAllPosts();
    }
    @PutMapping("/edit-post/{postId}/{userId}")
    public ResponseEntity<String> editPostById(@RequestBody EditPostRequestDto editPostRequestDto, @PathVariable Long postId, @PathVariable Long userId){
        return postService.editPostById(editPostRequestDto, postId, userId);
    }
    @GetMapping("/get-post/{postId}")
    public ResponseEntity<Post> findPostById(@PathVariable Long postId){
        return postService.findPostById(postId);

    }
    @DeleteMapping("/delete-post/{postId}/{userId}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long postId, @AuthenticationPrincipal User currentUser){
        postService.deletePostById(postId, currentUser);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search-post/{title}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<List<Post>> searchByTitle(@PathVariable String title){
        return postService.searchByTitle(title);
    }
    @PutMapping("/like/{post-id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Post> likePost(@PathVariable Long postId, @AuthenticationPrincipal User currentUser){
        return postService.likePostById(postId);
    }
    @PutMapping("/unlike/{postId}")
    public ResponseEntity<?> unLikePost(@PathVariable Long postId){
        return postService.unLikePostById(postId);
    }
}
