package examples.sdk.android.clover.com.appointo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder> {
    Context myContext;
    List<ProfDataObject> arr = new ArrayList<>();
    ProfessorListActivity prof;

    public CustomListAdapter(ProfessorListActivity professorListActivity, Context context, List<ProfDataObject> profList) {
        this.prof = professorListActivity;
        this.myContext = context;
        this.arr = profList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_list_activity, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.textView.setText(arr.get(i).getName());
        setImage(arr.get(i).getImageUrl(), myViewHolder.imageView);
        myViewHolder.textView2.setText(arr.get(i).getEmail());
        myViewHolder.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ProfDataObject> tmp = new ArrayList<>();
                ProfDataObject p = new ProfDataObject();
                p.setName(arr.get(i).getName());
                p.setLongitude(arr.get(i).getLongitude());
                p.setLatitude(arr.get(i).getLatitude());
                p.setImageUrl(arr.get(i).getImageUrl());
                p.setEmail(arr.get(i).getEmail());
                tmp.add(p);
                Intent i = new Intent(myContext, BookEvent.class);
                i.putExtra(Intents.PROF_DATA, (Serializable) tmp);
                prof.startActivity(i);
            }
        });
    }

    public void setImage(String url, ImageView imageView) {
        Glide.with(myContext).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(myContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        TextView textView2;
        Button b;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.imageView);
            textView2 = itemView.findViewById(R.id.email);
            b = itemView.findViewById(R.id.bookAppoint);

        }
    }
}
