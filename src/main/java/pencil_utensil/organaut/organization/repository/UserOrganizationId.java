package pencil_utensil.organaut.organization.repository;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserOrganizationId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "organization_id")
    private Long organizationId;

    public UserOrganizationId() {}

    public UserOrganizationId(Integer userId, Long organizationId) {
        this.userId = userId;
        this.organizationId = organizationId;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}