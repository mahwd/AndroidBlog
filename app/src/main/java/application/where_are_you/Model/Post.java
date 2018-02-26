package application.where_are_you.Model;

import android.util.Log;

import java.util.Map;

/**
 * Created by mahwd on 2/26/18.
 */

public class Post {

    private String Title;
    private String Description;
    private String UserId;
    private String ImageUrl;

    public Post(Map<String, Object> data) {
        Log.d("post: ", data.toString());
        this.Title = (String) data.get("title");
        Log.d("post: ", this.Title);
        this.Description = (String) data.get("description");
        this.UserId = (String) data.get("user_id");
        this.ImageUrl = (String) data.get("image_uri");
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getUserId() {
        return UserId;
    }
}
