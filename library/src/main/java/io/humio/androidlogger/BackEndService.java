package io.humio.androidlogger;

import java.util.List;

import io.humio.androidlogger.models.IngestRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface BackEndService {
    @POST("ingest")
    Call<Void> ingest(@Body List<IngestRequest> ingestRequest);
}