package ru.overcode.scrapper.repository.link;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.shared.dto.market.MarketName;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Slice<Link> findLinksByMarketName(MarketName marketName, Pageable pageable);
}
