package examples.sdk.android.clover.com.appointo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Endpoints {

  @GET("/")
  Call<List<ProfDataObject>> getAllProfs();


}
