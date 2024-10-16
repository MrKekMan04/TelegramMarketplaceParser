package ru.overcode.gateway.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.market.Market;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {

}
