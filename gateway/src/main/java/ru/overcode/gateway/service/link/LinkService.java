package ru.overcode.gateway.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.overcode.gateway.dto.chatlink.TelegramChatLinkMapper;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.link.LinkMapper;
import ru.overcode.gateway.mapper.rule.RuleMapper;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.service.chatlink.TelegramChatLinkDbService;
import ru.overcode.gateway.service.chatlink.rule.TelegramChatLinkRuleDbService;
import ru.overcode.gateway.service.link.formatter.LinkFormatter;
import ru.overcode.gateway.service.rule.RuleDbService;
import ru.overcode.gateway.service.telegramchat.TelegramChatService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final List<LinkFormatter> linkFormatters;
    private final TelegramChatService telegramChatService;
    private final TelegramChatLinkDbService telegramChatLinkDbService;
    private final TelegramChatLinkRuleDbService telegramChatLinkRuleDbService;
    private final LinkDbService linkDbService;
    private final RuleDbService ruleDbService;
    private final TelegramChatLinkMapper telegramChatLinkMapper;
    private final LinkMapper linkMapper;
    private final RuleMapper ruleMapper;

    @Transactional(readOnly = true)
    public List<GetLinkResponse> getLinksWithRules(Long chatId) {
        telegramChatService.throwIfNotExists(chatId);

        Map<Long, Link> linksById = linkDbService.getLinksForChat(chatId);

        if (CollectionUtils.isEmpty(linksById)) {
            return linkMapper.toGetLinkResponse(Map.of(), Map.of());
        }

        Map<Long, List<RuleDto>> rulesDtoByLinkId = ruleDbService
                .findAllByChatIdAndLinkIdIn(chatId, linksById.keySet()).stream()
                .collect(Collectors.groupingBy(
                        LinkRuleDto::getLinkId,
                        Collectors.mapping(ruleMapper::toRuleDto, Collectors.toList())
                ));

        return linkMapper.toGetLinkResponse(linksById, rulesDtoByLinkId);
    }

    @Transactional
    public AddLinkResponse addLink(Long chatId, URI linkUrl) {
        URI url = linkFormatters.stream()
                .filter(linkFormatter -> linkFormatter.intercept(linkUrl))
                .findFirst()
                .map(linkFormatter -> linkFormatter.format(linkUrl))
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_SUPPORTED
                        .withParam("url", linkUrl.toString())));

        telegramChatService.throwIfNotExists(chatId);

        Link link = linkDbService.findByUrl(url)
                .orElseGet(() -> linkDbService.createAndSave(url));

        telegramChatLinkDbService.findByChatIdAndLinkId(chatId, link.getId())
                .ifPresent(ignore -> {
                    throw new UnprocessableEntityException(GatewayExceptionMessage.LINK_ALREADY_ADDED
                            .withParam("url", linkUrl.toString()));
                });

        telegramChatLinkDbService.save(telegramChatLinkMapper.toTelegramChatLink(chatId, link.getId()));

        return linkMapper.toAddLinkResponse(link.getId());
    }

    @Transactional
    public void removeLink(Long chatId, Long linkId) {
        telegramChatService.throwIfNotExists(chatId);

        linkDbService.findById(linkId)
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_FOUND
                        .withParam("linkId", linkId.toString())));

        TelegramChatLink binding = telegramChatLinkDbService.findByChatIdAndLinkId(chatId, linkId)
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_ADDED
                        .withParam("linkId", linkId.toString())));

        telegramChatLinkDbService.deleteById(binding.getId());
        telegramChatLinkRuleDbService.deleteAllByChatLinkId(binding.getId());
    }
}
