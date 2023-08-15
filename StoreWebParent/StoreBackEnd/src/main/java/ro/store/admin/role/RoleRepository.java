package ro.store.admin.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ro.store.common.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
