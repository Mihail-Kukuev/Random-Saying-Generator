package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

public class Saying {

  @JsonIgnore
  private UUID id;
  private String text;
  private String author;
  private long likes;
  private long dislikes;

  public Saying() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public long getLikes() {
    return likes;
  }

  public void setLikes(long likes) {
    this.likes = likes;
  }

  public long getDislikes() {
    return dislikes;
  }

  public void setDislikes(long dislikes) {
    this.dislikes = dislikes;
  }
}
