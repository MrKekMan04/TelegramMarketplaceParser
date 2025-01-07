package ru.overcode.bot.config.feign.link;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.overcode.bot.dto.chat.RegistrationChatRequest;
import ru.overcode.bot.dto.link.AddLinkRequest;
import ru.overcode.bot.dto.link.AddLinkResponse;
import ru.overcode.bot.dto.link.GetLinkResponse;
import ru.overcode.bot.dto.link.RemoveLinkRequest;
import ru.overcode.bot.dto.rule.AddRuleRequest;
import ru.overcode.bot.dto.rule.GetRulesResponse;
import ru.overcode.bot.dto.rule.RemoveRuleRequest;
import ru.overcode.bot.dto.rule.RuleDto;
import ru.overcode.shared.api.ListResponse;
import ru.overcode.shared.api.Response;

public interface LinkFeignClient {

    @GetMapping(
            value = "/api/v1/links",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ListResponse<GetLinkResponse> getLinks(@RequestParam Long chatId);

    @PostMapping(
            value = "/api/v1/links",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<AddLinkResponse> addLink(@RequestBody AddLinkRequest request);

    @DeleteMapping(
            value = "/api/v1/links/{linkId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<Void> removeLink(@PathVariable Long linkId, @RequestBody RemoveLinkRequest request);

    @GetMapping(
            value = "/api/v1/links/{linkId}/rules",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ListResponse<GetRulesResponse> getRules(@PathVariable Long linkId);

    @PostMapping(
            value = "/api/v1/links/{linkId}/rules",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<RuleDto> addRule(@PathVariable Long linkId, @RequestBody AddRuleRequest request);

    @DeleteMapping(
            value = "/api/v1/links/{linkId}/rules/{ruleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<Void> removeRule(@PathVariable Long linkId, @PathVariable Long ruleId, @RequestBody RemoveRuleRequest request);

    @PostMapping(
            value = "/api/v1/registration",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<Void> registerChat(@RequestBody RegistrationChatRequest request);
}