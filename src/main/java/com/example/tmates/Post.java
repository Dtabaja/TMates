package com.example.tmates;

public class Post implements Comparable<Post>{
    private User author;
    private String postTitle, postDescription, postDate, postId, authorId;

    // Constructor.
    public Post(User author, String postTitle, String postDescription, String postDate, String postId, String authorId){
        this.author = author;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postDate = postDate;
        this.postId = postId;
        this.authorId = authorId;
    }

    public Post(){

    }

    // Getters and setters.
    public User getAuthor(){
        return this.author;
    }

    public void setAuthor(User author){
        this.author = author;
    }

    public String getPostTitle(){
        return this.postTitle;
    }

    public void setPostTitle(String postTitle){
        this.postTitle = postTitle;
    }

    public String getPostDescription(){
        return this.postDescription;
    }

    public void setPostDescription(String postDescription){
        this.postDescription = postDescription;
    }

    public String getPostDate(){
        return this.postDate;
    }

    public void setPostDate(String postDate){
        this.postDate = postDate;
    }

    public String getPostId(){
        return this.postId;
    }

    public void setPostId(String postId){
        this.postId = postId;
    }

    public String getAuthorId(){
        return this.authorId;
    }

    public void setAuthorId(String authorId){
        this.authorId = authorId;
    }

    @Override
    public int compareTo(Post post) {
        return post.getPostDate().compareTo(this.getPostDate());
    }
}
