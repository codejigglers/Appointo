package examples.sdk.android.clover.com.appointo;

import android.widget.ImageView;

public interface AppointmentClickListener {

  public void onDeletePress(String id);

  public void setImage(String url, ImageView v);
}
