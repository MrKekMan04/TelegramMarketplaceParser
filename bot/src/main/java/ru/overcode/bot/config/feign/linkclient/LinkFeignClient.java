package ru.overcode.bot.config.feign.linkclient;

import org.springframework.web.bind.annotation.*;
import ru.overcode.bot.dto.AddLinkRequest;
import ru.overcode.bot.dto.AddLinkResponse;
import ru.overcode.bot.dto.GetLinkResponse;
import ru.overcode.bot.dto.RemoveLinkRequest;
import ru.overcode.shared.api.ListResponse;
import ru.overcode.shared.api.Response;

public interface LinkFeignClient {

    @GetMapping("/api/v1/links")
    ListResponse<GetLinkResponse> getLinks(@RequestParam Long chatId);

    @PostMapping("/api/v1/links")
    Response<AddLinkResponse> addLink(@RequestBody AddLinkRequest request);

    @DeleteMapping("/api/v1/links/{linkId}")
    Response<Void> removeLink(@PathVariable Long linkId, @RequestBody RemoveLinkRequest request);

    @GetMapping("/api/v1/links/{linkId}/rules")
    ListResponse<String> getRules(@PathVariable Long linkId);

    @PostMapping("/api/v1/links/{linkId}/rules/{ruleId}")
    Response<Void> addRule(@PathVariable Long linkId, @PathVariable String ruleId, @RequestParam String params);

    @DeleteMapping("/api/v1/links/{linkId}/rules/{ruleId}")
    Response<Void> removeRule(@PathVariable Long linkId, @PathVariable String ruleId);
}