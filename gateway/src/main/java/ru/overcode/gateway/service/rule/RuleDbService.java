package ru.overcode.gateway.service.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.repository.rule.RuleRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleDbService {

    private final RuleRepository ruleRepository;

    public List<LinkRuleDto> findAllByChatIdAndLinkIdIn(Long chatId, Collection<Long> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return List.of();
        }
        return ruleRepository.findAllByChatIdAndLinkIdIn(chatId, linkIds);
    }
}
