package ru.overcode.gateway.service.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.dto.rule.internal.CreateRuleRequest;
import ru.overcode.gateway.dto.rule.internal.InternalRuleDto;
import ru.overcode.gateway.dto.rule.internal.UpdateRuleRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.page.PagedRequestMapper;
import ru.overcode.gateway.mapper.rule.RuleMapper;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.shared.api.PageContent;

@Service
@RequiredArgsConstructor
public class InternalRuleService {

    private final RuleDbService ruleDbService;
    private final PagedRequestMapper pagedRequestMapper;
    private final RuleMapper ruleMapper;

    public PageContent<InternalRuleDto> getRules(PagedRequest request) {
        Pageable pageable = pagedRequestMapper.toPageable(request);
        Page<Rule> rules = ruleDbService.findAll(pageable);
        return new PageContent<>(rules.getTotalPages(), ruleMapper.toInternalRuleDto(rules.getContent()));
    }

    @Transactional
    public void createRule(CreateRuleRequest request) {
        ruleDbService.findById(request.id())
                .ifPresent(rule -> {
                    throw new UnprocessableEntityException(GatewayExceptionMessage.RULE_EXISTS
                            .withParam("ruleId", request.id().toString()));
                });
        ruleDbService.save(ruleMapper.toEntity(request));
    }

    @Transactional
    public void updateRule(Long ruleId, UpdateRuleRequest request) {
        Rule rule = ruleDbService.findById(ruleId)
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_FOUND));
        ruleMapper.fillEntity(rule, request);
        ruleDbService.save(rule);
    }
}
