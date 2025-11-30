package com.example.demo.controller.api;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import com.example.demo.entity.WorkType;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.WorkFieldService;
import com.example.demo.service.WorkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Sử dụng RestController để trả về JSON
@RequestMapping("/api/admin") // Đường dẫn chung cho các API admin
public class AdminDataController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WorkFieldService workFieldService;

    @Autowired
    private WorkTypeService workTypeService;

    @Autowired
    private JobDetailService jobDetailService;

    // --- API cho Người dùng ---

    // Lấy tất cả người dùng
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Người dùng có ID " + id + " đã được xóa.");
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng Logger trong môi trường thực
            return ResponseEntity.badRequest().body("Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }

    // Cập nhật vai trò cho người dùng
    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Integer id, @RequestParam Integer roleId) {
        Optional<User> userOpt = userService.getUserById(id);
        Optional<Role> roleOpt = roleService.getRoleById(roleId);

        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            Role role = roleOpt.get();
            user.setRole(role);
            userService.updateUser(user);
            return ResponseEntity.ok("Vai trò của người dùng có ID " + id + " đã được cập nhật.");
        } else {
            String errorMessage = (userOpt.isEmpty() ? "Người dùng" : "Vai trò") + " không tìm thấy.";
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    // --- API cho Công ty ---

    // Lấy tất cả công ty đang chờ duyệt
    @GetMapping("/companies/pending")
    public ResponseEntity<List<Company>> getPendingCompanies() {
        List<Company> pendingCompanies = companyService.getUnverifiedCompanies();
        return ResponseEntity.ok(pendingCompanies);
    }

    // Lấy tất cả công ty đã được xác thực
    @GetMapping("/companies/verified")
    public ResponseEntity<List<Company>> getVerifiedCompanies() {
        List<Company> verifiedCompanies = companyService.getVerifiedCompanies();
        return ResponseEntity.ok(verifiedCompanies);
    }

    // Lấy tất cả công ty (cả pending và verified)
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        // Giả sử service có phương thức này hoặc bạn có thể kết hợp kết quả từ hai phương thức trên
        List<Company> allCompanies = companyService.getAllCompanies(); // Cần phương thức này trong service
        // Nếu chưa có, tạm thời bạn có thể trả về hai danh sách riêng lẻ trong một object
        // hoặc chỉ lấy pending và verified rồi gộp lại ở đây.
        // Ví dụ:
        // List<Company> pending = companyService.getUnverifiedCompanies();
        // List<Company> verified = companyService.getVerifiedCompanies();
        // List<Company> all = new ArrayList<>();
        // all.addAll(pending);
        // all.addAll(verified);
        return ResponseEntity.ok(allCompanies);
    }

    // Xác nhận công ty
    @PutMapping("/companies/{id}/approve")
    public ResponseEntity<String> approveCompany(@PathVariable Integer id) {
        try {
            companyService.approveCompany(id);
            return ResponseEntity.ok("Công ty có ID " + id + " đã được xác nhận.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi xác nhận công ty: " + e.getMessage());
        }
    }

    // Từ chối công ty
    @PutMapping("/companies/{id}/reject")
    public ResponseEntity<String> rejectCompany(@PathVariable Integer id) {
        try {
            companyService.rejectCompany(id);
            return ResponseEntity.ok("Công ty có ID " + id + " đã bị từ chối.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi từ chối công ty: " + e.getMessage());
        }
    }

    // --- API cho Vai trò ---

    // Lấy tất cả vai trò
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Tạo vai trò mới
    @PostMapping("/roles")
    public ResponseEntity<String> createRole(@RequestParam String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
             return ResponseEntity.badRequest().body("Tên vai trò không được để trống.");
        }
        try {
            Role role = new Role(roleName.trim());
            roleService.saveRole(role);
            return ResponseEntity.ok("Vai trò '" + roleName + "' đã được tạo.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tạo vai trò: " + e.getMessage());
        }
    }

    // --- API cho Lĩnh vực nghề nghiệp (Work Fields) ---

    // Lấy tất cả lĩnh vực nghề nghiệp
    @GetMapping("/work-fields")
    public ResponseEntity<List<WorkField>> getAllWorkFields() {
        List<WorkField> workFields = workFieldService.getAllWorkFields();
        return ResponseEntity.ok(workFields);
    }

    // Lấy lĩnh vực nghề nghiệp theo ID
    @GetMapping("/work-fields/{id}")
    public ResponseEntity<WorkField> getWorkFieldById(@PathVariable Integer id) {
        Optional<WorkField> workFieldOpt = workFieldService.getWorkFieldById(id);
        if (workFieldOpt.isPresent()) {
            return ResponseEntity.ok(workFieldOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Tạo lĩnh vực nghề nghiệp mới
    @PostMapping("/work-fields")
    public ResponseEntity<String> createWorkField(@RequestParam String tenLinhVuc) {
        if (tenLinhVuc == null || tenLinhVuc.trim().isEmpty()) {
             return ResponseEntity.badRequest().body("Tên lĩnh vực không được để trống.");
        }
        try {
            WorkField workField = new WorkField(tenLinhVuc.trim());
            workFieldService.saveWorkField(workField);
            return ResponseEntity.ok("Lĩnh vực nghề nghiệp '" + tenLinhVuc + "' đã được tạo.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tạo lĩnh vực nghề nghiệp: " + e.getMessage());
        }
    }

    // Cập nhật lĩnh vực nghề nghiệp
    @PutMapping("/work-fields/{id}")
    public ResponseEntity<String> updateWorkField(@PathVariable Integer id, @RequestParam String tenLinhVuc) {
        if (tenLinhVuc == null || tenLinhVuc.trim().isEmpty()) {
             return ResponseEntity.badRequest().body("Tên lĩnh vực không được để trống.");
        }
        try {
            Optional<WorkField> workFieldOpt = workFieldService.getWorkFieldById(id);
            if (workFieldOpt.isPresent()) {
                WorkField workField = workFieldOpt.get();
                workField.setTenLinhVuc(tenLinhVuc.trim());
                workFieldService.updateWorkField(workField);
                return ResponseEntity.ok("Lĩnh vực nghề nghiệp có ID " + id + " đã được cập nhật.");
            } else {
                 return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật lĩnh vực nghề nghiệp: " + e.getMessage());
        }
    }

    // Xóa lĩnh vực nghề nghiệp
    @DeleteMapping("/work-fields/{id}")
    public ResponseEntity<String> deleteWorkField(@PathVariable Integer id) {
        try {
            workFieldService.deleteWorkField(id);
            return ResponseEntity.ok("Lĩnh vực nghề nghiệp có ID " + id + " đã được xóa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi xóa lĩnh vực nghề nghiệp: " + e.getMessage());
        }
    }

    // --- API cho Hình thức làm việc (Work Types) ---

    // Lấy tất cả hình thức làm việc
    @GetMapping("/work-types")
    public ResponseEntity<List<WorkType>> getAllWorkTypes() {
        List<WorkType> workTypes = workTypeService.getAllWorkTypes();
        return ResponseEntity.ok(workTypes);
    }

    // Lấy hình thức làm việc theo ID
    @GetMapping("/work-types/{id}")
    public ResponseEntity<WorkType> getWorkTypeById(@PathVariable Integer id) {
        Optional<WorkType> workTypeOpt = workTypeService.getWorkTypeById(id);
        if (workTypeOpt.isPresent()) {
            return ResponseEntity.ok(workTypeOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Tạo hình thức làm việc mới
    @PostMapping("/work-types")
    public ResponseEntity<String> createWorkType(@RequestParam String tenHinhThuc) {
        if (tenHinhThuc == null || tenHinhThuc.trim().isEmpty()) {
             return ResponseEntity.badRequest().body("Tên hình thức không được để trống.");
        }
        try {
            WorkType workType = new WorkType(tenHinhThuc.trim());
            workTypeService.saveWorkType(workType);
            return ResponseEntity.ok("Hình thức làm việc '" + tenHinhThuc + "' đã được tạo.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tạo hình thức làm việc: " + e.getMessage());
        }
    }

    // Cập nhật hình thức làm việc
    @PutMapping("/work-types/{id}")
    public ResponseEntity<String> updateWorkType(@PathVariable Integer id, @RequestParam String tenHinhThuc) {
        if (tenHinhThuc == null || tenHinhThuc.trim().isEmpty()) {
             return ResponseEntity.badRequest().body("Tên hình thức không được để trống.");
        }
        try {
            Optional<WorkType> workTypeOpt = workTypeService.getWorkTypeById(id);
            if (workTypeOpt.isPresent()) {
                WorkType workType = workTypeOpt.get();
                workType.setTenHinhThuc(tenHinhThuc.trim());
                workTypeService.updateWorkType(workType);
                return ResponseEntity.ok("Hình thức làm việc có ID " + id + " đã được cập nhật.");
            } else {
                 return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật hình thức làm việc: " + e.getMessage());
        }
    }

    // Xóa hình thức làm việc
    @DeleteMapping("/work-types/{id}")
    public ResponseEntity<String> deleteWorkType(@PathVariable Integer id) {
        try {
            workTypeService.deleteWorkType(id);
            return ResponseEntity.ok("Hình thức làm việc có ID " + id + " đã được xóa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi xóa hình thức làm việc: " + e.getMessage());
        }
    }

    // --- API cho Chi tiết Công việc (Job Details) ---

    // Lấy tất cả công việc
    @GetMapping("/jobs")
    public ResponseEntity<List<JobDetail>> getAllJobs() {
        List<JobDetail> jobs = jobDetailService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    // Lấy tất cả công việc theo trạng thái duyệt (e.g., "Chờ duyệt", "Đã duyệt")
    @GetMapping("/jobs/status/{status}")
    public ResponseEntity<List<JobDetail>> getJobsByStatus(@PathVariable String status) {
        List<JobDetail> jobs = jobDetailService.getJobsByTrangThaiDuyet(status);
        return ResponseEntity.ok(jobs);
    }

    // Lấy công việc đang chờ duyệt
    @GetMapping("/jobs/pending")
    public ResponseEntity<List<JobDetail>> getPendingJobs() {
        List<JobDetail> pendingJobs = jobDetailService.getJobsByTrangThaiDuyet("Chờ duyệt");
        return ResponseEntity.ok(pendingJobs);
    }

    // Lấy công việc đã duyệt
    @GetMapping("/jobs/approved")
    public ResponseEntity<List<JobDetail>> getApprovedJobs() {
        List<JobDetail> approvedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
        return ResponseEntity.ok(approvedJobs);
    }
}