package ru.overcode.gateway.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.shared.dto.market.MarketName;

import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market> findByName(MarketName name);
}
