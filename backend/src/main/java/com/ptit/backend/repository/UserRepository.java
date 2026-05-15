package com.ptit.backend.repository;

import com.ptit.backend.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    long countByRoleRoleNameIgnoreCase(String roleName);

    @Query("""
            SELECT u
            FROM User u
            WHERE (:roleId IS NULL OR u.role.roleId = :roleId)
              AND (:status IS NULL OR u.status = :status)
              AND (
                  :keyword IS NULL
                  OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<User> searchUsers(
            @Param("roleId") Long roleId,
            @Param("status") Integer status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
