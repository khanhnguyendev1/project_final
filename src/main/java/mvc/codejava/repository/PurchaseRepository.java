package mvc.codejava.repository;

import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}


