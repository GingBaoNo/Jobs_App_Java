package com.example.demo.repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import com.example.demo.entity.JobPosition;
import com.example.demo.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobDetailRepository extends JpaRepository<JobDetail, Integer> {
    List<JobDetail> findByCompany(Company company);
    List<JobDetail> findByTrangThaiDuyet(String trangThaiDuyet);
    List<JobDetail> findByTrangThaiDuyetAndTrangThaiTinTuyen(String trangThaiDuyet, String trangThaiTinTuyen);
    List<JobDetail> findByNgayKetThucTuyenDungAfterAndTrangThaiDuyetAndTrangThaiTinTuyen(LocalDate ngayHienTai, String trangThaiDuyet, String trangThaiTinTuyen);
    List<JobDetail> findByWorkField(WorkField workField);

    List<JobDetail> findTop10ByTrangThaiDuyetAndTrangThaiTinTuyenOrderByLuotXemDesc(String trangThaiDuyet, String trangThaiTinTuyen);

    @Query("SELECT j FROM JobDetail j WHERE (UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%'))")
    List<JobDetail> findByTieuDeContainingOrChiTietContaining(@Param("keyword") String keyword);

    // Các phương thức tìm kiếm nâng cao
    @Query("SELECT j FROM JobDetail j WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR j.workField.maLinhVuc = :workField) " +
           "AND (:workType IS NULL OR j.workType.maHinhThuc = :workType) " +
           "AND (:minSalary IS NULL OR j.luong >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.luong <= :maxSalary) " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND j.ngayKetThucTuyenDung >= CURRENT_DATE")
    List<JobDetail> findByKeywordAndFilters(@Param("keyword") String keyword,
                                           @Param("workField") Integer workField,
                                           @Param("workType") Integer workType,
                                           @Param("minSalary") Integer minSalary,
                                           @Param("maxSalary") Integer maxSalary);

    // Phương thức tìm kiếm không áp dụng điều kiện trạng thái duyệt để cho phép tìm kiếm công việc đang chờ
    @Query("SELECT j FROM JobDetail j WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR j.workField.maLinhVuc = :workField) " +
           "AND (:workType IS NULL OR j.workType.maHinhThuc = :workType) " +
           "AND (:minSalary IS NULL OR j.luong >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.luong <= :maxSalary) " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND (j.ngayKetThucTuyenDung IS NULL OR j.ngayKetThucTuyenDung >= CURRENT_DATE)")
    List<JobDetail> findByKeywordAndFiltersWithoutStatus(@Param("keyword") String keyword,
                                           @Param("workField") Integer workField,
                                           @Param("workType") Integer workType,
                                           @Param("minSalary") Integer minSalary,
                                           @Param("maxSalary") Integer maxSalary);

    @Query("SELECT j FROM JobDetail j WHERE UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:companyName), '%') " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND j.ngayKetThucTuyenDung >= CURRENT_DATE")
    List<JobDetail> findByCompanyContaining(@Param("companyName") String companyName);

    @Query("SELECT j FROM JobDetail j WHERE (UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR j.workField.maLinhVuc = :workField) " +
           "AND (:workType IS NULL OR j.workType.maHinhThuc = :workType) " +
           "AND (:minSalary IS NULL OR j.luong >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.luong <= :maxSalary) " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND j.ngayKetThucTuyenDung >= CURRENT_DATE")
    org.springframework.data.domain.Page<JobDetail> findByKeywordAndFiltersWithPaging(@Param("keyword") String keyword,
                                           @Param("workField") Integer workField,
                                           @Param("workType") Integer workType,
                                           @Param("minSalary") Integer minSalary,
                                           @Param("maxSalary") Integer maxSalary,
                                           org.springframework.data.domain.Pageable pageable);

    // Phương thức tìm kiếm có phân trang không áp dụng điều kiện trạng thái duyệt
    @Query("SELECT j FROM JobDetail j WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR j.workField.maLinhVuc = :workField) " +
           "AND (:workType IS NULL OR j.workType.maHinhThuc = :workType) " +
           "AND (:minSalary IS NULL OR j.luong >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.luong <= :maxSalary) " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND (j.ngayKetThucTuyenDung IS NULL OR j.ngayKetThucTuyenDung >= CURRENT_DATE)")
    org.springframework.data.domain.Page<JobDetail> findByKeywordAndFiltersWithoutStatusWithPaging(@Param("keyword") String keyword,
                                           @Param("workField") Integer workField,
                                           @Param("workType") Integer workType,
                                           @Param("minSalary") Integer minSalary,
                                           @Param("maxSalary") Integer maxSalary,
                                           org.springframework.data.domain.Pageable pageable);

    // Phương thức tìm kiếm đơn lẻ theo ngành nghề hoặc hình thức làm việc
    @Query("SELECT j FROM JobDetail j WHERE (:workField IS NOT NULL AND j.workField.maLinhVuc = :workField) OR (:workType IS NOT NULL AND j.workType.maHinhThuc = :workType)")
    List<JobDetail> findByWorkFieldOrWorkType(@Param("workField") Integer workField, @Param("workType") Integer workType);

    // Phương thức tìm kiếm đơn lẻ theo ngành nghề hoặc hình thức làm việc với phân trang
    @Query("SELECT j FROM JobDetail j WHERE (:workField IS NOT NULL AND j.workField.maLinhVuc = :workField) OR (:workType IS NOT NULL AND j.workType.maHinhThuc = :workType)")
    org.springframework.data.domain.Page<JobDetail> findByWorkFieldOrWorkTypeWithPaging(@Param("workField") Integer workField, @Param("workType") Integer workType, org.springframework.data.domain.Pageable pageable);

    // Phương thức tìm kiếm kết hợp theo ngành nghề và hình thức làm việc
    @Query("SELECT j FROM JobDetail j WHERE (:workField IS NULL OR j.workField.maLinhVuc = :workField) AND (:workType IS NULL OR j.workType.maHinhThuc = :workType)")
    List<JobDetail> findByWorkFieldAndWorkType(@Param("workField") Integer workField, @Param("workType") Integer workType);

    // Phương thức tìm kiếm kết hợp theo ngành nghề và hình thức làm việc với phân trang
    @Query("SELECT j FROM JobDetail j WHERE (:workField IS NULL OR j.workField.maLinhVuc = :workField) AND (:workType IS NULL OR j.workType.maHinhThuc = :workType)")
    org.springframework.data.domain.Page<JobDetail> findByWorkFieldAndWorkTypeWithPaging(@Param("workField") Integer workField, @Param("workType") Integer workType, org.springframework.data.domain.Pageable pageable);

    // Phương thức tìm kiếm công việc theo tiêu đề (không phân biệt hoa thường)
    @Query("SELECT j FROM JobDetail j WHERE UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:title), '%')")
    List<JobDetail> findByTieuDeContainingIgnoreCase(@Param("title") String title);

    // Phương thức tìm kiếm công việc theo công ty và tiêu đề (không phân biệt hoa thường)
    @Query("SELECT j FROM JobDetail j WHERE j.company = :company AND UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:title), '%')")
    List<JobDetail> findByCompanyAndTieuDeContainingIgnoreCase(@Param("company") Company company, @Param("title") String title);

    // Phương thức tìm kiếm công việc theo trạng thái duyệt và tiêu đề công ty
    @Query("SELECT j FROM JobDetail j WHERE j.trangThaiDuyet = :trangThaiDuyet AND (UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:search), '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:search), '%'))")
    List<JobDetail> findByTrangThaiDuyetAndTieuDeContainingIgnoreCaseOrCompanyTenCongTyContainingIgnoreCase(@Param("trangThaiDuyet") String trangThaiDuyet, @Param("search") String search);

    // Phương thức tìm kiếm công việc theo tiêu đề hoặc tên công ty
    @Query("SELECT j FROM JobDetail j WHERE (UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:search), '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:search), '%'))")
    List<JobDetail> findByTieuDeContainingIgnoreCaseOrCompanyTenCongTyContainingIgnoreCase(@Param("search") String search);

    // Phương thức tìm kiếm mở (không áp dụng điều kiện trạng thái nghiêm ngặt, tương tự như tìm kiếm theo tiêu đề)
    @Query("SELECT j FROM JobDetail j WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%'))")
    List<JobDetail> findByKeywordWithoutStatus(@Param("keyword") String keyword);

    // Phương thức tìm kiếm toàn diện (không áp dụng điều kiện trạng thái)
    @Query("SELECT j FROM JobDetail j WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(j.company.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR j.workField.maLinhVuc = :workField) " +
           "AND (:workType IS NULL OR j.workType.maHinhThuc = :workType) " +
           "AND (:minSalary IS NULL OR j.luong >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.luong <= :maxSalary)")
    List<JobDetail> findByKeywordAndFiltersNoStatus(@Param("keyword") String keyword,
                                           @Param("workField") Integer workField,
                                           @Param("workType") Integer workType,
                                           @Param("minSalary") Integer minSalary,
                                           @Param("maxSalary") Integer maxSalary);

    // Thêm các phương thức tìm kiếm theo vị trí công việc và cấp độ kinh nghiệm
    @Query("SELECT j FROM JobDetail j " +
           "LEFT JOIN j.company c " +
           "LEFT JOIN j.workField wf " +
           "LEFT JOIN j.jobPosition jp " +
           "LEFT JOIN j.experienceLevel el " +
           "LEFT JOIN j.workType wt " +
           "WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(c.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR wf.maLinhVuc = :workField) " +
           "AND (:jobPosition IS NULL OR jp.maViTri = :jobPosition) " +
           "AND (:experienceLevel IS NULL OR el.maCapDo = :experienceLevel) " +
           "AND (:workType IS NULL OR wt.maHinhThuc = :workType) " +
           "AND (:workDiscipline IS NULL OR EXISTS (SELECT 1 FROM JobPosition jp2 WHERE jp2.maViTri = j.jobPosition.maViTri AND jp2.workDiscipline.maNganh = :workDiscipline)) " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND (j.ngayKetThucTuyenDung IS NULL OR j.ngayKetThucTuyenDung >= CURRENT_DATE) ")
    List<JobDetail> findByWorkFieldAndDisciplineAndPositionAndExperience(@Param("keyword") String keyword,
                                                                         @Param("workField") Integer workField,
                                                                         @Param("workDiscipline") Integer workDiscipline,
                                                                         @Param("jobPosition") Integer jobPosition,
                                                                         @Param("experienceLevel") Integer experienceLevel,
                                                                         @Param("workType") Integer workType);

    // Tìm kiếm có phân trang theo vị trí công việc và cấp độ kinh nghiệm
    @Query(value = "SELECT j FROM JobDetail j " +
           "LEFT JOIN j.company c " +
           "LEFT JOIN j.workField wf " +
           "LEFT JOIN j.jobPosition jp " +
           "LEFT JOIN j.experienceLevel el " +
           "LEFT JOIN j.workType wt " +
           "WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER(c.tenCongTy) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR wf.maLinhVuc = :workField) " +
           "AND (:jobPosition IS NULL OR jp.maViTri = :jobPosition) " +
           "AND (:experienceLevel IS NULL OR el.maCapDo = :experienceLevel) " +
           "AND (:workType IS NULL OR wt.maHinhThuc = :workType) " +
           "AND (:workDiscipline IS NULL OR EXISTS (SELECT 1 FROM JobPosition jp2 WHERE jp2.maViTri = j.jobPosition.maViTri AND jp2.workDiscipline.maNganh = :workDiscipline)) " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' " +
           "AND (j.ngayKetThucTuyenDung IS NULL OR j.ngayKetThucTuyenDung >= CURRENT_DATE) ",
           countQuery = "SELECT COUNT(j) FROM JobDetail j " +
           "LEFT JOIN j.workField wf " +
           "LEFT JOIN j.jobPosition jp " +
           "LEFT JOIN j.experienceLevel el " +
           "LEFT JOIN j.workType wt " +
           "WHERE (:keyword IS NULL OR UPPER(j.tieuDe) LIKE CONCAT('%', UPPER(:keyword), '%') OR j.chiTiet LIKE CONCAT('%', :keyword, '%') OR UPPER((SELECT c.tenCongTy FROM Company c WHERE c.maCongTy = j.company.maCongTy) ) LIKE CONCAT('%', UPPER(:keyword), '%')) " +
           "AND (:workField IS NULL OR wf.maLinhVuc = :workField) " +
           "AND (:jobPosition IS NULL OR jp.maViTri = :jobPosition) " +
           "AND (:experienceLevel IS NULL OR el.maCapDo = :experienceLevel) " +
           "AND (:workType IS NULL OR wt.maHinhThuc = :workType) " +
           "AND (:workDiscipline IS NULL OR EXISTS (SELECT 1 FROM JobPosition jp2 WHERE jp2.maViTri = j.jobPosition.maViTri AND jp2.workDiscipline.maNganh = :workDiscipline)) " +
           "AND j.trangThaiDuyet = 'Đã duyệt' " +
           "AND j.trangThaiTinTuyen = 'Mở' ")
    org.springframework.data.domain.Page<JobDetail> findByWorkFieldAndDisciplineAndPositionAndExperienceWithPaging(@Param("keyword") String keyword,
                                                                                                                 @Param("workField") Integer workField,
                                                                                                                 @Param("workDiscipline") Integer workDiscipline,
                                                                                                                 @Param("jobPosition") Integer jobPosition,
                                                                                                                 @Param("experienceLevel") Integer experienceLevel,
                                                                                                                 @Param("workType") Integer workType,
                                                                                                                 org.springframework.data.domain.Pageable pageable);

    // Tìm kiếm nâng cao có phân trang với nhiều tiêu chí
    @Query(value = "SELECT j FROM JobDetail j " +
           "LEFT JOIN j.company c " +
           "LEFT JOIN j.workField wf " +
           "LEFT JOIN j.jobPosition jp " +
           "LEFT JOIN j.experienceLevel el " +
           "LEFT JOIN j.workType wt " +
           "WHERE (:workField IS NULL OR wf.maLinhVuc = :workField) " +
           "AND (:jobPosition IS NULL OR jp.maViTri = :jobPosition) " +
           "AND (:workDiscipline IS NULL OR EXISTS (SELECT 1 FROM JobPosition jp2 WHERE jp2.maViTri = j.jobPosition.maViTri AND jp2.workDiscipline.maNganh = :workDiscipline)) ",
           countQuery = "SELECT COUNT(j) FROM JobDetail j " +
           "LEFT JOIN j.workField wf " +
           "LEFT JOIN j.jobPosition jp " +
           "LEFT JOIN j.experienceLevel el " +
           "LEFT JOIN j.workType wt " +
           "WHERE (:workField IS NULL OR wf.maLinhVuc = :workField) " +
           "AND (:jobPosition IS NULL OR jp.maViTri = :jobPosition) " +
           "AND (:workDiscipline IS NULL OR EXISTS (SELECT 1 FROM JobPosition jp2 WHERE jp2.maViTri = j.jobPosition.maViTri AND jp2.workDiscipline.maNganh = :workDiscipline)) ")
    org.springframework.data.domain.Page<JobDetail> findByKeywordAndFiltersAdvancedWithPaging(@Param("keyword") String keyword,
                                                                                             @Param("workField") Integer workField,
                                                                                             @Param("workDiscipline") Integer workDiscipline,
                                                                                             @Param("jobPosition") Integer jobPosition,
                                                                                             @Param("experienceLevel") Integer experienceLevel,
                                                                                             @Param("workType") Integer workType,
                                                                                             @Param("minSalary") Integer minSalary,
                                                                                             @Param("maxSalary") Integer maxSalary,
                                                                                             org.springframework.data.domain.Pageable pageable);

    // Tìm kiếm theo vị trí công việc
    List<JobDetail> findByJobPosition(JobPosition jobPosition);

    // Tìm kiếm theo ID vị trí công việc
    List<JobDetail> findByJobPosition_MaViTri(Integer maViTri);

    // Tìm kiếm theo cấp độ kinh nghiệm
    List<JobDetail> findByExperienceLevel(ExperienceLevel experienceLevel);

    // Tìm kiếm theo ID cấp độ kinh nghiệm
    List<JobDetail> findByExperienceLevel_MaCapDo(Integer maCapDo);

    // Tìm kiếm theo ID lĩnh vực
    List<JobDetail> findByWorkField_MaLinhVuc(Integer maLinhVuc);

    // Tìm kiếm theo ID hình thức làm việc
    List<JobDetail> findByWorkType_MaHinhThuc(Integer maHinhThuc);
}