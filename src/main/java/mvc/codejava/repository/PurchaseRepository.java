package mvc.codejava.repository;

import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUser(Optional<User> user);
    Optional<Purchase> findById(Long id);

}


