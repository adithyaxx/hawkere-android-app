package pw.adithya.hawkere.objects;

public class Rating implements Comparable<Rating> {
    private double hygieneRating;
    private double varietyRating;
    private double seatingRating;
    private double foodRating;
    private double totalRating;
    private String placeID;
    private String review;
    private String userID;
    private String authorName;
    private long timestamp;
    private String authorPic;

    public String getAuthorPic() {
        return authorPic;
    }

    public void setAuthorPic(String authorPic) {
        this.authorPic = authorPic;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getHygieneRating() {
        return hygieneRating;
    }

    public void setHygieneRating(double hygieneRating) {
        this.hygieneRating = hygieneRating;
    }

    public double getVarietyRating() {
        return varietyRating;
    }

    public void setVarietyRating(double varietyRating) {
        this.varietyRating = varietyRating;
    }

    public double getSeatingRating() {
        return seatingRating;
    }

    public void setSeatingRating(double seatingRating) {
        this.seatingRating = seatingRating;
    }

    public double getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(double foodRating) {
        this.foodRating = foodRating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Rating r) {
        if (timestamp < r.getTimestamp())
            return 1;
        else if (timestamp > r.getTimestamp())
            return -1;

        return 0;
    }
}
