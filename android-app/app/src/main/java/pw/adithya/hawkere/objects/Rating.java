package pw.adithya.hawkere.objects;

public class Rating {
    private double hygieneRating, varietyRating, seatingRating, foodRating;
    private String placeID;
    private String review;
    private String userID;
    private String authorName;
    private long timestamp;

    public Rating() {
    }

    public Rating(String placeID, double hygieneRating, double varietyRating, double seatingRating, double foodRating, String review, String authorName, String userID, long timestamp) {
        this.hygieneRating = hygieneRating;
        this.varietyRating = varietyRating;
        this.seatingRating = seatingRating;
        this.foodRating = foodRating;
        this.review = review;
        this.authorName = authorName;
        this.userID = userID;
        this.timestamp = timestamp;
        this.placeID = placeID;
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
}
