package mvc.codejava.repository;

import mvc.codejava.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
}
