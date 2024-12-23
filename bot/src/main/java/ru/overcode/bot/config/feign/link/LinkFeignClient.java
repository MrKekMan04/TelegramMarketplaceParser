package ru.overcode.bot.config.feign.link;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.overcode.bot.dto.link.AddLinkRequest;
import ru.overcode.bot.dto.link.AddLinkResponse;
import ru.overcode.bot.dto.link.GetLinkResponse;
import ru.overcode.bot.dto.link.RemoveLinkRequest;
import ru.overcode.bot.dto.rule.GetRulesResponse;
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
            value = "/api/v1/links/{linkId}/rules/{ruleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<RuleDto> addRule(@PathVariable Long linkId, @PathVariable String ruleId, @RequestParam String params);

    @DeleteMapping(
            value = "/api/v1/links/{linkId}/rules/{ruleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response<Void> removeRule(@PathVariable Long linkId, @PathVariable String ruleId);
}