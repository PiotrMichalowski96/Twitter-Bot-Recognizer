package com.university.twic.calculate.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TwitterUser {

  @SerializedName("id")
  @Expose
  private Long id;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("screen_name")
  @Expose
  private String screenName;

  @SerializedName("description")
  @Expose
  private String description;

  @SerializedName("verified")
  @Expose
  private Boolean verified;

  @SerializedName("followers_count")
  @Expose
  private Long followersCount;

  @SerializedName("friends_count")
  @Expose
  private Long friendsCount;

  @SerializedName("favourites_count")
  @Expose
  private Long favouritesCount;

  @SerializedName("statuses_count")
  @Expose
  private Long statusesCount;

  @SerializedName("created_at")
  @Expose
  private String createdAt;

  @SerializedName("default_profile")
  @Expose
  private Boolean defaultProfile;

  @SerializedName("default_profile_image")
  @Expose
  private Boolean defaultProfileImage;
}
