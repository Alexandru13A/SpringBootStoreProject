package ro.store.admin.role;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.store.common.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
