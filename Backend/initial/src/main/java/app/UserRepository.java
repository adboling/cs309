package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

	// find by single parameter ==========================================

	public List<User> findAllByRole(String role);

	public List<User> findAllByPassword(String password);

	public List<User> findAllByFirstnameIgnoreCase(String first);

	public List<User> findAllByLastnameIgnoreCase(String last);

	public User findOneById(Integer id);

	public User findByUsernameIgnoreCase(String username);

	public User findByEmailIgnoreCase(String email);

	public List<User> findAllByUsernameContainingIgnoreCase(String username);

	// find by multiple parameters =======================================

	// delete by a single parameter ======================================

	public void deleteById(Integer id);
//	public void deleteByUsername(String username);
//	public void deleteByEmail(String email);

}
