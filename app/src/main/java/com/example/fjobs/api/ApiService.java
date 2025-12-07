package com.example.fjobs.api;

import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.models.LoginRequest;
import com.example.fjobs.models.RegisterRequest;
import com.example.fjobs.models.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Authentication APIs
    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);

    // Job APIs
    @GET("v1/job-details")
    Call<ApiResponse> getAllJobs();

    @GET("v1/job-details/{id}")
    Call<ApiResponse> getJobById(@Path("id") int jobId);

    @POST("v1/job-details")
    Call<ApiResponse> createJob(@Body JobDetail job);

    @PUT("v1/job-details/{id}")
    Call<ApiResponse> updateJob(@Path("id") int jobId, @Body JobDetail job);

    @DELETE("v1/job-details/{id}")
    Call<ApiResponse> deleteJob(@Path("id") int jobId);

    // Company APIs
    @GET("v1/companies")
    Call<ApiResponse> getAllCompanies();

    @GET("v1/companies/{id}")
    Call<ApiResponse> getCompanyById(@Path("id") int companyId);

    // API để lấy công việc của công ty
    @GET("v1/companies/{id}/jobs")
    Call<ApiResponse> getJobsByCompanyId(@Path("id") int companyId);

    // Search API
    @GET("v1/job-details/search")
    Call<ApiResponse> searchJobs(@Query("keyword") String keyword);

    // Open Search API (ít điều kiện hơn)
    @GET("v1/job-details/search-open")
    Call<ApiResponse> searchJobsOpen(@Query("keyword") String keyword);

    // No Status Search API (không áp dụng điều kiện trạng thái)
    @GET("v1/job-details/search-no-status")
    Call<ApiResponse> searchJobsNoStatus(@Query("keyword") String keyword);

    // Featured Jobs API
    @GET("v1/job-details/featured")
    Call<ApiResponse> getFeaturedJobs();

    // Featured Companies API
    @GET("v1/companies/featured")
    Call<ApiResponse> getFeaturedCompanies();

    // Profile APIs
    @GET("v1/profiles/my-profile")
    Call<ApiResponse> getMyProfile();

    @POST("v1/profiles/my-profile")
    Call<ApiResponse> createMyProfile(@Body com.example.fjobs.models.Profile profile);

    @PUT("v1/profiles/my-profile")
    Call<ApiResponse> updateMyProfile(@Body com.example.fjobs.models.Profile profile);

    // Upload APIs
    @Multipart
    @POST("v1/profiles/my-profile/avatar")
    Call<ApiResponse> uploadAvatar(@Part MultipartBody.Part file);

    @Multipart
    @POST("v1/profiles/my-profile/cv")
    Call<ApiResponse> uploadCv(@Part MultipartBody.Part file);

    // Applied Jobs APIs
    @GET("v1/applied-jobs/my-applications")
    Call<ApiResponse> getMyApplications();

    @POST("v1/applied-jobs")
    Call<ApiResponse> applyForJob(@Body AppliedJobRequest request);

    @DELETE("v1/applied-jobs/{id}")
    Call<ApiResponse> cancelApplication(@Path("id") int applicationId);

    // Saved Jobs APIs
    @GET("v1/saved-jobs/my-saved-jobs")
    Call<ApiResponse> getMySavedJobs();

    @POST("v1/saved-jobs")
    Call<ApiResponse> saveJob(@Body SaveJobRequest request);

    @DELETE("v1/saved-jobs")
    Call<ApiResponse> unsaveJob(@Body UnsaveJobRequest request);

    @GET("v1/saved-jobs/check/{jobId}")
    Call<ApiResponse> checkIfJobSaved(@Path("jobId") int jobId);

    // Request classes
    class AppliedJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }

    class SaveJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }

    class UnsaveJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }
}