package no.nav.pam.annonsemottak.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StillingRepository extends JpaRepository<Stilling, Long> {

    Stilling findByUuid(String uuid);

}
