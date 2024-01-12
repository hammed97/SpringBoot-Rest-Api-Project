package com.example.tyfashionblogapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private long id;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "content", nullable = false)
    private String content;
    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List <Comment> comments = new ArrayList<>();


    @Column(name = "postDate", nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    @JsonIgnore
    private Date postDate;
    @JsonIgnore
    private Integer likes = 0;
    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> likeBy = new HashSet<>();



    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    public Post(String title, String description, String content, Integer likes) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.likes = likes;
    }
    public List<Comment> getComments(){
        return comments == null ? null : new ArrayList<>(comments);
    }
    public void setComments(List<Comment> comments){
        this.comments = comments;
    }
    public void incrementPost(){
        likes++;
    }
    public void decrementPost(){
        likes--;
    }
}
