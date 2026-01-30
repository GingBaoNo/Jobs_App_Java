package com.example.fjobs.api;

import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.CvProfile;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.models.LoginRequest;
import com.example.fjobs.models.RegisterRequest;
import com.example.fjobs.models.StandardChatMessage;
import com.example.fjobs.models.User;

import java.util.Map;
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

    // OTP APIs
    @POST("auth/forgot-password")
    Call<ApiResponse> sendOtp(@Body Map<String, String> emailRequest);

    @POST("auth/verify-otp")
    Call<ApiResponse> verifyOtp(@Body Map<String, String> otpRequest);

    @POST("auth/reset-password")
    Call<ApiResponse> resetPassword(@Body Map<String, String> resetPasswordRequest);

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

    // Search by Hierarchy API (tìm kiếm theo cấu trúc phân cấp mới) - sử dụng API mới
    @GET("v1/job-details/search-advanced")
    Call<ApiResponse> searchJobsByHierarchy(
            @Query("keyword") String keyword,
            @Query("workField") Integer workField,
            @Query("workDiscipline") Integer workDiscipline,
            @Query("jobPosition") Integer jobPosition,
            @Query("experienceLevel") Integer experienceLevel,
            @Query("workType") Integer workType
    );

    // APIs cho cấu trúc phân cấp
    @GET("v1/work-fields")
    Call<ApiResponse> getAllWorkFields();

    @GET("v1/work-disciplines/field/{workFieldId}")
    Call<ApiResponse> getWorkDisciplinesByField(@Path("workFieldId") Integer workFieldId);

    @GET("v1/job-positions/discipline/{disciplineId}")
    Call<ApiResponse> getJobPositionsByDiscipline(@Path("disciplineId") Integer disciplineId);

    @GET("v1/experience-levels")
    Call<ApiResponse> getAllExperienceLevels();

    @GET("v1/work-types")
    Call<ApiResponse> getAllWorkTypes();

    // Featured Jobs API
    @GET("v1/job-details/featured")
    Call<ApiResponse> getFeaturedJobs();

    // APIs cho tìm kiếm nâng cao theo từng tiêu chí
    @GET("v1/job-details/by-field/{fieldId}")
    Call<ApiResponse> searchJobsByField(@Path("fieldId") Integer fieldId);

    @GET("v1/job-details/by-type/{typeId}")
    Call<ApiResponse> searchJobsByType(@Path("typeId") Integer typeId);

    @GET("v1/job-details/by-position/{positionId}")
    Call<ApiResponse> searchJobsByPosition(@Path("positionId") Integer positionId);

    @GET("v1/job-details/by-experience/{experienceId}")
    Call<ApiResponse> searchJobsByExperience(@Path("experienceId") Integer experienceId);

    // API tìm kiếm nâng cao với phân trang
    @GET("v1/job-details/search-advanced")
    Call<ApiResponse> searchJobsAdvancedWithPaging(
            @Query("keyword") String keyword,
            @Query("workField") Integer workField,
            @Query("workDiscipline") Integer workDiscipline,
            @Query("jobPosition") Integer jobPosition,
            @Query("experienceLevel") Integer experienceLevel,
            @Query("workType") Integer workType,
            @Query("minSalary") Integer minSalary,
            @Query("maxSalary") Integer maxSalary,
            @Query("page") int page,
            @Query("size") int size
    );

    // API tìm kiếm nâng cao theo mô tả yêu cầu (bao gồm cả mức lương nhưng sẽ truyền null nếu không dùng)
    @GET("v1/advanced-search/jobs")
    Call<ApiResponse> searchJobsAdvanced(
            @Query("keyword") String keyword,
            @Query("fieldId") Integer fieldId,
            @Query("disciplineId") Integer disciplineId,
            @Query("positionId") Integer positionId,
            @Query("experienceId") Integer experienceId,
            @Query("typeId") Integer typeId,
            @Query("minSalary") Integer minSalary,
            @Query("maxSalary") Integer maxSalary,
            @Query("page") int page,
            @Query("size") int size
    );

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

    // CV Profile APIs
    @GET("v1/cv-profiles/my-cv-profiles")
    Call<ApiResponse> getMyCvProfiles();

    @GET("v1/cv-profiles/{id}")
    Call<ApiResponse> getCvProfileById(@Path("id") int cvProfileId);

    @POST("v1/cv-profiles")
    Call<ApiResponse> createCvProfile(@Body CvProfile cvProfile);

    @PUT("v1/cv-profiles/{id}")
    Call<ApiResponse> updateCvProfile(@Path("id") int cvProfileId, @Body CvProfile cvProfile);

    @DELETE("v1/cv-profiles/{id}")
    Call<ApiResponse> deleteCvProfile(@Path("id") int cvProfileId);

    @PUT("v1/cv-profiles/{id}/set-default")
    Call<ApiResponse> setCvProfileAsDefault(@Path("id") int cvProfileId);

    // Create CV Profile with files
    @Multipart
    @POST("v1/cv-profiles/create-with-files")
    Call<ApiResponse> createCvProfileWithFiles(
            @Part("tenHoSo") RequestBody tenHoSo,
            @Part("moTa") RequestBody moTa,
            @Part("hoTen") RequestBody hoTen,
            @Part("gioiTinh") RequestBody gioiTinh,
            @Part("ngaySinh") RequestBody ngaySinh,
            @Part("soDienThoai") RequestBody soDienThoai,
            @Part("trinhDoHocVan") RequestBody trinhDoHocVan,
            @Part("tinhTrangHocVan") RequestBody tinhTrangHocVan,
            @Part("kinhNghiem") RequestBody kinhNghiem,
            @Part("tongNamKinhNghiem") RequestBody tongNamKinhNghiem,
            @Part("gioiThieuBanThan") RequestBody gioiThieuBanThan,
            @Part("congKhai") RequestBody congKhai,
            @Part("viTriMongMuon") RequestBody viTriMongMuon,
            @Part("thoiGianMongMuon") RequestBody thoiGianMongMuon,
            @Part("loaiThoiGianLamViec") RequestBody loaiThoiGianLamViec,
            @Part("hinhThucLamViec") RequestBody hinhThucLamViec,
            @Part("loaiLuongMongMuon") RequestBody loaiLuongMongMuon,
            @Part("mucLuongMongMuon") RequestBody mucLuongMongMuon,
            @Part("laMacDinh") RequestBody laMacDinh,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part cvFile
    );

    // Update CV Profile with files
    @Multipart
    @PUT("v1/cv-profiles/{id}/update-with-files")
    Call<ApiResponse> updateCvProfileWithFiles(
            @Path("id") int cvProfileId,
            @Part("tenHoSo") RequestBody tenHoSo,
            @Part("moTa") RequestBody moTa,
            @Part("hoTen") RequestBody hoTen,
            @Part("gioiTinh") RequestBody gioiTinh,
            @Part("ngaySinh") RequestBody ngaySinh,
            @Part("soDienThoai") RequestBody soDienThoai,
            @Part("trinhDoHocVan") RequestBody trinhDoHocVan,
            @Part("tinhTrangHocVan") RequestBody tinhTrangHocVan,
            @Part("kinhNghiem") RequestBody kinhNghiem,
            @Part("tongNamKinhNghiem") RequestBody tongNamKinhNghiem,
            @Part("gioiThieuBanThan") RequestBody gioiThieuBanThan,
            @Part("congKhai") RequestBody congKhai,
            @Part("viTriMongMuon") RequestBody viTriMongMuon,
            @Part("thoiGianMongMuon") RequestBody thoiGianMongMuon,
            @Part("loaiThoiGianLamViec") RequestBody loaiThoiGianLamViec,
            @Part("hinhThucLamViec") RequestBody hinhThucLamViec,
            @Part("loaiLuongMongMuon") RequestBody loaiLuongMongMuon,
            @Part("mucLuongMongMuon") RequestBody mucLuongMongMuon,
            @Part("laMacDinh") RequestBody laMacDinh,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part cvFile
    );

    // Upload APIs cho hồ sơ CV cụ thể
    @Multipart
    @POST("v1/cv-profiles/{id}/upload-avatar")
    Call<ApiResponse> uploadCvProfileAvatar(@Path("id") int cvProfileId, @Part MultipartBody.Part file);

    @Multipart
    @POST("v1/cv-profiles/{id}/upload-cv")
    Call<ApiResponse> uploadCvProfileCv(@Path("id") int cvProfileId, @Part MultipartBody.Part file);

    // Apply for job with specific CV profile
    @POST("v1/applied-jobs/apply-with-cv-profile")
    Call<ApiResponse> applyForJobWithCvProfile(@Body AppliedJobWithCvProfileRequest request);

    // Request class for applying with CV profile
    class AppliedJobWithCvProfileRequest {
        private Integer jobDetailId;
        private Integer cvProfileId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }

        public Integer getCvProfileId() {
            return cvProfileId;
        }

        public void setCvProfileId(Integer cvProfileId) {
            this.cvProfileId = cvProfileId;
        }
    }

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

    @DELETE("v1/saved-jobs/{jobId}")
    Call<ApiResponse> unsaveJob(@Path("jobId") int jobId);

    @GET("v1/saved-jobs/check/{jobId}")
    Call<ApiResponse> checkIfJobSaved(@Path("jobId") int jobId);

    // Chat APIs
    @GET("v1/chat/admin/users")
    Call<ApiResponse> getChatUsersForAdmin();

    @GET("v1/chat/employer/admins")
    Call<ApiResponse> getAdminsForEmployer();

    @GET("v1/chat/employer/applicants")
    Call<ApiResponse> getApplicantsForEmployer();

    @GET("v1/chat/applicant/available-chats")
    Call<ApiResponse> getAvailableChatsForApplicant();

    // API kết hợp cho nhà tuyển dụng (lấy cả admin và ứng viên) - sẽ được thực hiện trong fragment
    // Không cần endpoint riêng cho API này vì sẽ gọi cả hai endpoint riêng biệt

    @GET("v1/chat/messages/{otherUserId}")
    Call<ApiResponse> getConversation(@Path("otherUserId") int otherUserId);

    @POST("v1/chat/send")
    Call<ApiResponse> sendMessage(@Body Map<String, Object> messageData);

    @POST("v1/chat/send")
    Call<ApiResponse> sendStandardMessage(@Body StandardChatMessage standardMessage);

    // Request classes cho chat
    class MessageRequest {
        private int senderId;
        private int receiverId;
        private String content;

        public MessageRequest(int senderId, int receiverId, String content) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }

        public int getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(int receiverId) {
            this.receiverId = receiverId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

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

    // APIs mới cho trang chủ - cập nhật đúng endpoint
    @GET("v1/home/latest-jobs")
    Call<ApiResponse> getLatestJobs();

    @GET("v1/home/popular-work-fields")
    Call<ApiResponse> getPopularWorkFields();

    @GET("v1/home/market-overview")
    Call<ApiResponse> getMarketStatistics();
}