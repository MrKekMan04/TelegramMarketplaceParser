package ru.overcode.gateway.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.repository.market.MarketRepository;

import java.util.Map;
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
}
