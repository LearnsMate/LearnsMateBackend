package intbyte4.learnsmate.admin.domain.entity;

import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "admins")
@Table(name = "admins")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_code", nullable = false)
    private Long adminCode;

    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    @Column(name = "admin_password", nullable = false)
    private String adminPassword;

    @Column(name = "admin_department", nullable = false)
    private String adminDepartment;

    @Column(name = "admin_position", nullable = false)
    private String adminPosition;

    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @Column(name = "admin_phone", nullable = false)
    private String adminPhone;

    @Column(name = "admin_address", nullable = false)
    private String adminAddress;

    @Column(name = "admin_birthday", nullable = false)
    private LocalDateTime adminBirthday;

    @Column(name = "admin_jobtype", nullable = false)
    private String adminJobType;

    @Column(name = "admin_level", nullable = false)
    private String adminLevel;

    @Column(name = "admin_status", nullable = false)
    private Boolean adminStatus;

    @Column(name = "admin_lastlogindate", nullable = false)
    private LocalDateTime adminLastLoginDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AdminDTO convertToDTO() {
        return AdminDTO.builder()
                .adminCode(this.adminCode)
                .adminEmail(this.adminEmail)
                .adminPassword(this.adminPassword)
                .adminDepartment(this.adminDepartment)
                .adminPosition(this.adminPosition)
                .adminName(this.adminName)
                .adminPhone(this.adminPhone)
                .adminAddress(this.adminAddress)
                .adminBirthday(this.adminBirthday)
                .adminJobType(this.adminJobType)
                .adminLevel(this.adminLevel)
                .adminStatus(this.adminStatus)
                .adminLastLoginDate(this.adminLastLoginDate)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
