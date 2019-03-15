package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorListActivity extends AppCompatActivity {
    Call<List<ProfDataObject>> call;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager linear;
    CustomListAdapter customAdapter;
    List<ProfDataObject> profDataObjectArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_list);
        Intent i = getIntent();
        String url = i.getStringExtra("text");
        Log.e("Sam", i.getStringExtra("text"));

        Endpoints service = RetrofitInstance.getRetrofitInstance(url).create(Endpoints.class);
        call = service.getAllProfs();
        recyclerView = findViewById(R.id.recyclerView);
        linear = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linear);
        call.enqueue(new Callback<List<ProfDataObject>>() {
            @Override
            public void onResponse(Call<List<ProfDataObject>> call, Response<List<ProfDataObject>> response) {
                profDataObjectArrayList = response.body();
                customAdapter = new CustomListAdapter(ProfessorListActivity.this, getApplicationContext(), profDataObjectArrayList);
                recyclerView.setAdapter(customAdapter);
            }

            @Override
            public void onFailure(Call<List<ProfDataObject>> call, Throwable t) {
                Log.e("Sam", "here2");

            }

        });

    }
}
