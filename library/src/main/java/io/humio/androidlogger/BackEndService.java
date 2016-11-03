package io.humio.androidlogger;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackEndService {
    String API_VERSION = "v1";

    // Login
    @POST("api/" + API_VERSION + "/dataspaces/esbjerg/ingest")
    Call<Void> ingest(@Body List<IngestRequest> ingestRequest);
}