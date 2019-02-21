package examples.sdk.android.clover.com.appointo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

  private ArrayList<AppointmentModel> data;

  public AppointmentAdapter(ArrayList<AppointmentModel> data) {
    this.data = data;
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
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    AppointmentModel element = data.get(i);
    viewHolder.date.setText(element.date);
    viewHolder.time.setText(element.time);
    viewHolder.name.setText(element.name);
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView time;
    TextView date;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.profName);
      time = (TextView) itemView.findViewById(R.id.appTime);
      date = (TextView) itemView.findViewById(R.id.appDay);
    }

  }
}
