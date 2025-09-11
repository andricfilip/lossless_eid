package pmf.kg.eid_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmf.kg.eid_api.entity.EidInfoEntity;
@Repository
public interface EidInfoRepository extends JpaRepository<EidInfoEntity, Long> {
    EidInfoEntity findByPersonalNumber(String personalNumber);

    boolean existsByPersonalNumber(String personalNumber);
    EidInfoEntity findEidInfoByPersonalNumber(String personalNumber);
}
