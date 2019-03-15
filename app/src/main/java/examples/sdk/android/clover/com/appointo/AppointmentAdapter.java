package examples.sdk.android.clover.com.appointo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

  private ArrayList<AppointmentModel> data;
  private AppointmentClickListener appCompatActivity;
  HashMap<String, ProfDataObject> profData;

  public AppointmentAdapter(ArrayList<AppointmentModel> data, AppointmentClickListener activity, HashMap<String, ProfDataObject> profData) {
    this.data = data;
    appCompatActivity = activity;
    this.profData = profData;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

    Context context = viewGroup.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View contactView = inflater.inflate(R.layout.appointment_layout, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(contactView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
    final AppointmentModel element = data.get(i);
    viewHolder.date.setText(element.date);
    viewHolder.time.setText(element.time);
    viewHolder.name.setText(profData.get(element.name).email);
    viewHolder.day.setText(profData.get(element.name).name);
    appCompatActivity.setImage(profData.get(element.name).getImageUrl(), viewHolder.imageView);
    viewHolder.delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        appCompatActivity.onDeletePress(element.getId());
      }
    });
    viewHolder.descriptionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AppCompatActivity appCompat = (AppCompatActivity)  appCompatActivity;
        FragmentTransaction ft = appCompat.getSupportFragmentManager().beginTransaction();
        Fragment prev = appCompat.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
          ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString("num", element.description);
        dialogFragment.setArguments(args);
        dialogFragment.show(ft, "dialog");
      }
    });

    viewHolder.map.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String lat = profData.get(element.name).getLatitude();
        String longi = profData.get(element.name).getLongitude();
        Activity activity = (Activity) appCompatActivity;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/" + lat + ",+" + longi + "/@" + lat + "," + longi + ",17z"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.getApplicationContext().startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView time;
    TextView date;
    TextView day;
    ImageView imageView;
    TextView delete;
    TextView map;
    TextView descriptionButton;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      map = (TextView) itemView.findViewById(R.id.map);
      name = (TextView) itemView.findViewById(R.id.profName);
      time = (TextView) itemView.findViewById(R.id.appTime);
      date = (TextView) itemView.findViewById(R.id.appDate);
      day = itemView.findViewById(R.id.appDay);
      imageView = (ImageView) itemView.findViewById(R.id.imageView);
      delete = (TextView) itemView.findViewById(R.id.deleteButton);
      descriptionButton = (TextView) itemView.findViewById(R.id.descButton);
    }

  }
}
