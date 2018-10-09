package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

class UserEntity(
  @SerializedName("name") val name: String,
  @SerializedName("profile_image") val profileImage: ProfileImage
)