package com.example.tyfashionblogapplication.serviceImpl;

import com.example.tyfashionblogapplication.DTO.EditPostRequestDto;
import com.example.tyfashionblogapplication.DTO.PostRequestDto;
import com.example.tyfashionblogapplication.enums.Role;
import com.example.tyfashionblogapplication.model.Post;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.repositories.PostRepository;
import com.example.tyfashionblogapplication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl {
    private PostRepository postRepository;
    private UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity<String> createPost(PostRequestDto postRequestDto, Long userId) {
        User optionalUser = this.userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No user with ID " + userId + "Found in my database"));

        Post post = new Post();
        post.setDescription(postRequestDto.getContent());
        post.setTitle(postRequestDto.getPostTitle());
        post.setUser(optionalUser);
        Post post1 = this.postRepository.save(post);
        return new ResponseEntity<>(post1.getTitle(), HttpStatus.CONFLICT);



    }

    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();
        return new ResponseEntity<>(allPosts, HttpStatus.FOUND);
    }



    public ResponseEntity<String> editPostById(EditPostRequestDto editPostRequestDto, Long postId, Long userId) {
        Post optionalPost = this.postRepository.findById(postId).orElseThrow(()-> new RuntimeException("No post with ID " + postId + "Found in my database"));
        User optionalUser = this.userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No user with ID " + userId + "Found in my database"));

        if (optionalPost.getUser().equals(optionalUser)){
            throw new RuntimeException("Please you can not edit this post simply because you were not the one that made the post");
        }
        optionalPost.setDescription(editPostRequestDto.getContent());
        this.postRepository.save(optionalPost);
        return new ResponseEntity<>("Post successfully edited",HttpStatus.OK);
    }

    public ResponseEntity<Post> findPostById(Long postId) {
        return new ResponseEntity<>(postRepository.findById(postId).orElseThrow(()-> new RuntimeException("No Post Found with ID" + postId)), HttpStatus.FOUND);
    }

    public void deletePostById(Long postId, User currentUser) {
        currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()));
        postRepository.deleteById(postId);
    }

    public ResponseEntity<List<Post>> searchByTitle(String title) {
        List<Post> allSearchByTitle = postRepository.findByTitleIgnoreCaseStartingWith(title);
        if (allSearchByTitle.isEmpty()){
            return new ResponseEntity<>(allSearchByTitle, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(allSearchByTitle, HttpStatus.FOUND);
        }
    }

    public ResponseEntity<Post> likePostById(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null){
            post.incrementPost();
            postRepository.save(post);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> unLikePostById(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post !=null && post.getLikes() > 0){
            post.decrementPost();
            postRepository.save(post);
            return new ResponseEntity<>(post, HttpStatus.OK);
        }else {
            Post zeroLikesPost = new Post(post.getTitle(), post.getDescription(), post.getContent(), post.getLikes());
            zeroLikesPost.setId(post.getId());
            zeroLikesPost.setPostDate(post.getPostDate());
            return new ResponseEntity<>(zeroLikesPost, HttpStatus.OK);
        }

    }
}
