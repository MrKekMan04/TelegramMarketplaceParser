package ru.overcode.gateway.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.repository.market.MarketRepository;
import ru.overcode.shared.dto.market.MarketName;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketDbService {

    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public Map<String, Market> findAll() {
        return marketRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Market::getUrlDomain,
                        Function.identity()
                ));
    }

    @Transactional(readOnly = true)
    public Page<Market> findAll(Pageable pageable) {
        return marketRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Market> findById(Long id) {
        return marketRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Market> findByName(MarketName name) {
        return marketRepository.findByName(name);
    }

    @Transactional
    public Market save(Market market) {
        return marketRepository.save(market);
    }
}
