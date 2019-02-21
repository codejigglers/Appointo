package examples.sdk.android.clover.com.appointo;

public class AppointmentModel {

  String time;
  String name;
  String imageUrl;
  String date;

  public String getTime() {
    return time;
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getDate() {
    return date;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setDate(String date) {
    this.date = date;
  }

}
