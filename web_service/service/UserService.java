package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileService profileService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByTaiKhoan(String taiKhoan) {
        return userRepository.findByTaiKhoan(taiKhoan);
    }
    
    public User saveUser(User user) {
        // Mã hóa mật khẩu nếu có thay đổi
        if (user.getMatKhau() != null && !user.getMatKhau().startsWith("$2a$")) {
            // Nếu mật khẩu chưa được mã hóa (không bắt đầu bằng $2a$), thì mã hóa
            user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
        }
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        // Mã hóa mật khẩu nếu có thay đổi
        if (user.getMatKhau() != null && !user.getMatKhau().startsWith("$2a$")) {
            // Nếu mật khẩu chưa được mã hóa (không bắt đầu bằng $2a$), thì mã hóa
            user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
        }
        return userRepository.save(user);
    }
    
    @Autowired
    private com.example.demo.repository.MessageRepository messageRepository;

    @Autowired
    private com.example.demo.repository.AppliedJobRepository appliedJobRepository;

    @Autowired
    private com.example.demo.repository.SavedJobRepository savedJobRepository;

    @Autowired
    private com.example.demo.repository.ProfileRepository profileRepository;

    @Autowired
    private com.example.demo.repository.CvProfileRepository cvProfileRepository;

    @Autowired
    private com.example.demo.repository.CompanyRepository companyRepository;

    @org.springframework.transaction.annotation.Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Xóa các bản ghi liên quan trước để tránh lỗi ràng buộc khóa ngoại

            // Nếu là người tìm việc, xóa các ứng tuyển của họ trước
            if ("NV".equals(user.getRole().getTenVaiTro())) {
                appliedJobRepository.deleteByMaNguoiTimViec(id);
            }

            // Xóa các công việc đã lưu bởi người dùng này
            savedJobRepository.deleteByMaNguoiDung(id);

            // Xóa hồ sơ người dùng nếu có
            profileRepository.deleteByMaNguoiTimViec(id);

            // Xóa các hồ sơ CV của người dùng
            cvProfileRepository.deleteByMaNguoiTimViec(id);

            // Nếu là nhà tuyển dụng, xử lý công ty của họ
            if ("NTD".equals(user.getRole().getTenVaiTro())) {
                // Xóa các công việc của công ty do người dùng này tạo
                companyRepository.findByUserMaNguoiDung(id).forEach(company -> {
                    // Xóa các công việc liên quan đến công ty
                    // (giả sử có repository cho jobdetails)
                    // jobDetailRepository.deleteByCompanyId(company.getMaCongTy());
                });

                // Xóa công ty do người dùng này tạo
                companyRepository.deleteByMaNhaTuyenDung(id);
            }

            // Xóa tin nhắn gửi và nhận bởi người dùng này
            messageRepository.deleteByMaNguoiGuiOrMaNguoiNhan(id, id);

            // Cuối cùng xóa người dùng
            userRepository.deleteById(id);
        }
    }
    
    public boolean existsByTaiKhoan(String taiKhoan) {
        return userRepository.existsByTaiKhoan(taiKhoan);
    }
    
    public User registerUser(String taiKhoan, String matKhau, String tenHienThi, String email, String soDienThoai, Role role) {
        if (userRepository.existsByTaiKhoan(taiKhoan)) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(matKhau);
        User user = new User(taiKhoan, encodedPassword, tenHienThi, email, soDienThoai);
        user.setRole(role);

        User savedUser = saveUser(user);

        // Tự động tạo hồ sơ mặc định cho người dùng mới nếu là NV (người xin việc)
        // hoặc cho bất kỳ người dùng nào không có hồ sơ
        try {
            profileService.createDefaultProfileIfNotExists(savedUser);
        } catch (Exception e) {
            // Nếu tạo hồ sơ thất bại, log lỗi nhưng không làm hỏng quá trình đăng ký
            System.err.println("Không thể tạo hồ sơ mặc định cho người dùng " + taiKhoan + ": " + e.getMessage());
        }

        return savedUser;
    }

    public List<User> getUsersBySearch(String search) {
        return userRepository.findByTaiKhoanContainingOrTenHienThiContainingOrEmailContaining(search, search, search);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}