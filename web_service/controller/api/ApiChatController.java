package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.model.UserWithLastMessage;
import com.example.demo.entity.Message;
import com.example.demo.model.StandardChatMessage;
import com.example.demo.service.ChatService;
import com.example.demo.service.MessageService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import com.example.demo.utils.ChatMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Lấy danh sách người có thể nhắn tin cho admin (tất cả người dùng)
    @GetMapping("/admin/users")
    public ResponseEntity<?> getChatUsersForAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        // Kiểm tra vai trò của người dùng có phải là Admin không
        if (!"ADMIN".equals(currentUser.get().getRole().getTenVaiTro())) {
            return ApiResponseUtil.error("Truy cập bị từ chối: Chỉ có admin mới được truy cập endpoint này");
        }

        List<UserWithLastMessage> chatUsers = chatService.getChatUsersForAdmin(currentUser.get());
        return ApiResponseUtil.success("Danh sách người trò chuyện cho admin đã được tải thành công", chatUsers);
    }

    // Lấy danh sách admin để nhắn tin cho nhà tuyển dụng
    @GetMapping("/employer/admins")
    public ResponseEntity<?> getAdminsForEmployer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        // Kiểm tra vai trò của người dùng có phải là NTD không
        if (!"NTD".equals(currentUser.get().getRole().getTenVaiTro())) {
            return ApiResponseUtil.error("Truy cập bị từ chối: Chỉ có nhà tuyển dụng mới được truy cập endpoint này");
        }

        List<UserWithLastMessage> admins = chatService.getAdminsForEmployer(currentUser.get());
        return ApiResponseUtil.success("Danh sách admin cho nhà tuyển dụng đã được tải thành công", admins);
    }

    // Lấy danh sách ứng viên đã ứng tuyển vào công ty của nhà tuyển dụng
    @GetMapping("/employer/applicants")
    public ResponseEntity<?> getApplicantsForEmployer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        // Kiểm tra vai trò của người dùng có phải là NTD không
        if (!"NTD".equals(currentUser.get().getRole().getTenVaiTro())) {
            return ApiResponseUtil.error("Truy cập bị từ chối: Chỉ có nhà tuyển dụng mới được truy cập endpoint này");
        }

        List<UserWithLastMessage> applicants = chatService.getApplicantsForEmployer(currentUser.get());
        return ApiResponseUtil.success("Danh sách ứng viên cho nhà tuyển dụng đã được tải thành công", applicants);
    }

    // Lấy lịch sử tin nhắn giữa hai người dùng
    @GetMapping("/messages/{otherUserId}")
    public ResponseEntity<?> getConversation(@PathVariable Integer otherUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        User otherUser = userService.findById(otherUserId);
        if (otherUser == null) {
            return ApiResponseUtil.error("Không tìm thấy người dùng khác");
        }

        // Kiểm tra quyền truy cập dựa trên vai trò
        String currentRole = currentUser.get().getRole().getTenVaiTro();
        String otherUserRole = otherUser.getRole().getTenVaiTro();

        if ("ADMIN".equals(currentRole)) {
            // Admin có thể nhắn tin với bất kỳ ai
        } else if ("NTD".equals(currentRole)) {
            // NTD chỉ có thể nhắn tin với admin hoặc ứng viên đã ứng tuyển vào công ty của họ
            boolean isAdmin = "ADMIN".equals(otherUserRole);
            boolean isApplicant = chatService.getApplicantsForEmployer(currentUser.get())
                    .stream()
                    .anyMatch(u -> u.getUser().getMaNguoiDung().equals(otherUser.getMaNguoiDung()));

            if (!isAdmin && !isApplicant) {
                return ApiResponseUtil.error("Truy cập bị từ chối: Bạn chỉ có thể trò chuyện với admin hoặc ứng viên từ công ty của bạn");
            }
        } else if ("NV".equals(currentRole)) {
            // Ứng viên chỉ có thể nhắn tin với admin hoặc NTD của công ty mà họ đã ứng tuyển
            boolean isAdmin = "ADMIN".equals(otherUserRole);
            // Kiểm tra nếu người này là NTD và họ đã ứng tuyển cho công ty của người đó
            boolean isApplicableEmployer = false;
            if ("NTD".equals(otherUserRole)) {
                // Kiểm tra xem người dùng hiện tại đã ứng tuyển cho công ty của NTD này chưa
                isApplicableEmployer = chatService.hasAppliedToEmployer(currentUser.get(), otherUser);
            }

            if (!isAdmin && !isApplicableEmployer) {
                return ApiResponseUtil.error("Truy cập bị từ chối: Bạn chỉ có thể trò chuyện với admin hoặc nhà tuyển dụng bạn đã ứng tuyển");
            }
        } else {
            return ApiResponseUtil.error("Truy cập bị từ chối: Vai trò người dùng không được hỗ trợ");
        }

        // Đánh dấu tin nhắn là đã đọc
        chatService.markMessagesAsRead(currentUser.get(), otherUser);

        // Lấy lịch sử tin nhắn và chuyển đổi sang StandardChatMessage sử dụng tiện ích
        List<Message> conversation = chatService.getConversation(currentUser.get(), otherUser);
        List<StandardChatMessage> standardConversation = ChatMessageUtils.toStandardChatMessages(conversation);

        // Ghi log để debug
        System.out.println("Số lượng tin nhắn lấy được: " + conversation.size());

        return ApiResponseUtil.success("Lịch sử trò chuyện được tải thành công", standardConversation);
    }

    // API dành cho người tìm việc (NVT) để lấy danh sách người có thể trò chuyện
    @GetMapping("/applicant/available-chats")
    public ResponseEntity<?> getAvailableChatsForApplicant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        // Kiểm tra vai trò của người dùng có phải là NV không
        if (!"NV".equals(currentUser.get().getRole().getTenVaiTro())) {
            return ApiResponseUtil.error("Truy cập bị từ chối: Chỉ có người tìm việc mới được truy cập endpoint này");
        }

        // Lấy danh sách admin và các NTD mà người dùng đã ứng tuyển
        List<UserWithLastMessage> availableChats = chatService.getAvailableChatsForApplicant(currentUser.get());
        return ApiResponseUtil.success("Danh sách người trò chuyện cho người tìm việc đã được tải thành công", availableChats);
    }

    // API để gửi tin nhắn
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody StandardChatMessage standardMsg) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("Người dùng chưa xác thực");
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return ApiResponseUtil.error("Không tìm thấy người dùng");
        }

        try {
            // Xác thực thông tin người nhận
            User receiver = userService.findById(standardMsg.getReceiverId());
            if (receiver == null) {
                return ApiResponseUtil.error("Không tìm thấy người nhận");
            }

            // Kiểm tra quyền truy cập dựa trên vai trò (giống như trong getConversation)
            String currentRole = currentUser.get().getRole().getTenVaiTro();
            User otherUser = receiver;

            if ("ADMIN".equals(currentRole)) {
                // Admin có thể nhắn tin với bất kỳ ai
            } else if ("NTD".equals(currentRole)) {
                // NTD chỉ có thể nhắn tin với admin hoặc ứng viên đã ứng tuyển vào công ty của họ
                boolean isAdmin = "ADMIN".equals(otherUser.getRole().getTenVaiTro());
                boolean isApplicant = chatService.getApplicantsForEmployer(currentUser.get())
                        .stream()
                        .anyMatch(u -> u.getUser().getMaNguoiDung().equals(otherUser.getMaNguoiDung()));

                if (!isAdmin && !isApplicant) {
                    return ApiResponseUtil.error("Truy cập bị từ chối: Bạn chỉ có thể trò chuyện với admin hoặc ứng viên từ công ty của bạn");
                }
            } else if ("NV".equals(currentRole)) {
                // Ứng viên chỉ có thể nhắn tin với admin hoặc NTD của công ty mà họ đã ứng tuyển
                String otherUserRoleName = otherUser.getRole().getTenVaiTro();
                // Chuyển đổi tên vai trò đầy đủ sang rút gọn
                if ("Người tìm việc".equals(otherUserRoleName)) {
                    otherUserRoleName = "NV";
                } else if ("Nhà tuyển dụng".equals(otherUserRoleName)) {
                    otherUserRoleName = "NTD";
                } else if ("Admin".equals(otherUserRoleName)) {
                    otherUserRoleName = "ADMIN";
                }

                boolean isAdmin = "ADMIN".equals(otherUserRoleName);
                // Kiểm tra nếu người này là NTD và họ đã ứng tuyển cho công ty của người đó
                boolean isApplicableEmployer = false;
                if ("NTD".equals(otherUserRoleName)) {
                    // Kiểm tra xem người dùng hiện tại đã ứng tuyển cho công ty của NTD này chưa
                    isApplicableEmployer = chatService.hasAppliedToEmployer(currentUser.get(), otherUser);
                }

                if (!isAdmin && !isApplicableEmployer) {
                    return ApiResponseUtil.error("Truy cập bị từ chối: Bạn chỉ có thể trò chuyện với admin hoặc nhà tuyển dụng bạn đã ứng tuyển");
                }
            } else {
                return ApiResponseUtil.error("Truy cập bị từ chối: Vai trò người dùng không được hỗ trợ");
            }

            // Tạo và lưu tin nhắn
            Message message = new Message(currentUser.get(), receiver, standardMsg.getContent());
            Message savedMessage = messageService.saveMessage(message);

            if (savedMessage == null) {
                return ApiResponseUtil.error("Không thể lưu tin nhắn vào cơ sở dữ liệu");
            }

            // Chuyển đổi sang StandardChatMessage sử dụng tiện ích
            StandardChatMessage response = ChatMessageUtils.toStandardChatMessage(savedMessage);

            // Gửi tin nhắn qua WebSocket đến người nhận (nếu đang trực tuyến)
            messagingTemplate.convertAndSendToUser(
                    receiver.getTaiKhoan(),
                    "/queue/messages",
                    response
            );

            // Gửi tin nhắn qua WebSocket đến người gửi (để cập nhật giao diện)
            messagingTemplate.convertAndSendToUser(
                    currentUser.get().getTaiKhoan(),
                    "/queue/messages",
                    response
            );

            // Gửi thông báo reload cho cả người gửi và người nhận
            notificationService.notifyUserReload(currentUser.get().getTaiKhoan(), "new_message");
            notificationService.notifyUserReload(receiver.getTaiKhoan(), "new_message");

            System.out.println("Tin nhắn đã được gửi từ API và gửi qua WebSocket thành công");

            return ApiResponseUtil.success("Tin nhắn đã được gửi thành công", response);
        } catch (Exception e) {
            return ApiResponseUtil.error("Lỗi khi gửi tin nhắn: " + e.getMessage());
        }
    }
}