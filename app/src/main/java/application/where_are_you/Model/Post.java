package application.where_are_you.Model;

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
        this.Title = data.get("title").toString();
        this.Description = data.get("description").toString();
        this.UserId = data.get("userId").toString();
        this.ImageUrl = data.get("imageUrl").toString();
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
