package examples.sdk.android.clover.com.appointo;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface Endpoints {

  @GET("/")
  Call<List<ProfDataObject>> getAllProfs();


}
