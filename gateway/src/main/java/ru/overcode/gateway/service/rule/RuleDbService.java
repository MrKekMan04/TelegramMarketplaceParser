package ru.overcode.gateway.service.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.repository.rule.RuleRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RuleDbService {

    private final RuleRepository ruleRepository;

    @Transactional(readOnly = true)
    public List<LinkRuleDto> findAllByChatIdAndLinkIdIn(Long chatId, Collection<Long> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return List.of();
        }
        return ruleRepository.findAllByChatIdAndLinkIdIn(chatId, linkIds);
    }

    @Transactional(readOnly = true)
    public Optional<Rule> findById(Long ruleId) {
        return ruleRepository.findById(ruleId);
    }

    @Transactional(readOnly = true)
    public List<Rule> findAllByLinkId(Long linkId) {
        return ruleRepository.findAllByLinkId(linkId);
    }

    @Transactional(readOnly = true)
    public Page<Rule> findAll(Pageable pageable) {
        return ruleRepository.findAll(pageable);
    }

    @Transactional
    public Rule save(Rule rule) {
        return ruleRepository.save(rule);
    }
}
