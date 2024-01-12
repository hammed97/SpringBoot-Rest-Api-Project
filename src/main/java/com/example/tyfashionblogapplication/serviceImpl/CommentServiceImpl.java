package com.example.tyfashionblogapplication.serviceImpl;

import com.example.tyfashionblogapplication.DTO.CommentRequestDto;
import com.example.tyfashionblogapplication.DTO.EditCommentRequestDto;
import com.example.tyfashionblogapplication.enums.Role;
import com.example.tyfashionblogapplication.model.Comment;
import com.example.tyfashionblogapplication.model.Post;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.repositories.CommentRepository;
import com.example.tyfashionblogapplication.repositories.PostRepository;
import com.example.tyfashionblogapplication.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Comment> createComment(CommentRequestDto commentRequestDto, Long userId, Long postId) {
        User user = this.userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Does Not Exist"));
        Post post = this.postRepository.findById(postId).orElseThrow(()-> new RuntimeException("Post Does Not Exist"));

        Comment comment = new Comment();
        comment.setContent(commentRequestDto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        this.commentRepository.save(comment);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);

    }

    public ResponseEntity<Comment> editComment(EditCommentRequestDto editCommentRequestDto, Long userId, Long commentId) {
        User user = this.userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Does Not Exist"));
        Comment editedComment = this.commentRepository.findById(commentId).orElseThrow(()-> new RuntimeException("Bad Request"));

        if (!editedComment.getUser().equals(user)){
            throw new RuntimeException("Only the Original Creator can edit comment");
        }
        editedComment.setContent(editCommentRequestDto.getContent());
        commentRepository.save(editedComment);
        return new ResponseEntity<>(editedComment, HttpStatus.OK);
    }

    public ResponseEntity<List<Comment>> findAllCommentById(Long postId) {
        List<Comment> findCommentById = commentRepository.findByPostId(postId);
        if (findCommentById.isEmpty()){
            return new ResponseEntity<>(findCommentById, HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<>(findCommentById, HttpStatus.FOUND);
        }
    }

    public ResponseEntity<List<Comment>> getAllComment() {
        List<Comment> allComment = commentRepository.findAll();
        return new ResponseEntity<>(allComment, HttpStatus.FOUND);
    }

    public ResponseEntity<List<Comment>> findCommentByContent(String content) {
        List<Comment> findByContent = commentRepository.findByContentIgnoringCaseContaining(content);
        if (findByContent.isEmpty()){
            return new ResponseEntity<>(findByContent, HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(findByContent, HttpStatus.FOUND);
        }
    }

    public ResponseEntity<Void> deleteComment(Long commentId, Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Does Not Exist"));
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(()-> new RuntimeException("Bad Request"));
        
        if (!(comment.getUser().equals(user)) || !user.getUserRole().equals(Role.ROLE_ADMIN)){
            throw new RuntimeException("User Not Authorized to Delete Comment");
        }

        commentRepository.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Comment> likeCommentById(Long id) {
        Optional<Comment> commentToLike = commentRepository.findById(id);
        if (commentToLike.isPresent()){
            Comment comment = commentToLike.get();
            comment.setCommentLikes(comment.getCommentLikes() + 1);
            commentRepository.save(comment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Comment> unlikeCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment!= null && comment.getCommentLikes() > 0){
            comment.decrementComment();
            commentRepository.save(comment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        }else {
            Comment zeroLikes = new Comment(comment.getContent(), comment.getCommentLikes());
            zeroLikes.setComment_id(comment.getComment_id());
            zeroLikes.setCommentDate(comment.getCommentDate());
            return new ResponseEntity<>(zeroLikes, HttpStatus.OK);
        }
    }
}
