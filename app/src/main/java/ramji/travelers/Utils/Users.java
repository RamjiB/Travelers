package ramji.travelers.Utils;


import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable{

    private String username;
    private String user_id;
    private String profile_image_path;
    private String city;
    private String about_me;

    public Users() {
    }

    @Override
    public String toString() {
        return "Users{" +
                "username='" + username + '\'' +
                ", user_id='" + user_id + '\'' +
                ", profile_image_path='" + profile_image_path + '\'' +
                ", city='" + city + '\'' +
                ", about_me='" + about_me + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_image_path() {
        return profile_image_path;
    }

    public void setProfile_image_path(String profile_image_path) {
        this.profile_image_path = profile_image_path;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }


    public static Creator<Users> getCREATOR() {
        return CREATOR;
    }

    public Users(String username, String user_id, String profile_image_path, String city, String about_me) {

        this.username = username;
        this.user_id = user_id;
        this.profile_image_path = profile_image_path;
        this.city = city;
        this.about_me = about_me;
    }

    private Users(Parcel in) {
        username = in.readString();
        user_id = in.readString();
        profile_image_path = in.readString();
        city = in.readString();
        about_me = in.readString();
    }

    private static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(user_id);
        dest.writeString(profile_image_path);
        dest.writeString(city);
        dest.writeString(about_me);
    }
}
