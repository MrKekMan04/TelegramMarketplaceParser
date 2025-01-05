package ru.overcode.bot.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.bot.config.feign.link.LinkFeignClient;
import ru.overcode.bot.dto.chat.RegistrationChatRequest;
import ru.overcode.bot.dto.link.*;
import ru.overcode.bot.dto.rule.AddRuleRequest;
import ru.overcode.bot.dto.rule.GetRulesResponse;
import ru.overcode.bot.dto.rule.RemoveRuleRequest;
import ru.overcode.shared.api.ListResponse;
import ru.overcode.shared.api.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final LinkFeignClient linkFeignClient;

    public String registerChat(Long chatId, String[] params) {
        return processCommand(params, 0,
                () -> {
                    Response<Void> response = linkFeignClient.registerChat(new RegistrationChatRequest(chatId));
                    return response.toString();
                },
                "Произошла непредвиденная ошибка"
        );
    }

    public String addLink(Long chatId, String[] params) {
        return processCommand(params, 1,
                () -> {
                    URI uri = URI.create(params[1]);
                    Response<AddLinkResponse> response = linkFeignClient.addLink(new AddLinkRequest(chatId, uri));
                    return "Ссылка успешно добавлена. ID: " + response.getData().linkId();
                },
                "Произошла непредвиденная ошибка при добавлении ссылки"
        );
    }

    public String removeLink(Long chatId, String[] params) {
        return processCommand(params, 1,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    Response<Void> response = linkFeignClient.removeLink(linkId, new RemoveLinkRequest(chatId));
                    return "Ссылка успешно удалена";
                },
                "Произошла непредвиденная ошибка при удалении ссылки"
        );
    }

    public String addRule(Long chatId, String[] params) {
        return processCommand(params, 3,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    Long ruleId = parseLong(params[2], "Некорректный формат ID правила");
                    Map<String, String> rules = parseRules(params[3]);
                    AddRuleRequest request = new AddRuleRequest(chatId, ruleId, rules);
                    linkFeignClient.addRule(linkId, request);
                    return "Правило успешно привязано к ссылке.";
                },
                "Произошла непредвиденная ошибка при привязке правила."
        );
    }

    public String removeRule(Long chatId, String[] params) {
        return processCommand(params, 2,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    Long ruleId = parseLong(params[2], "Некорректный формат ID правила");
                    Response<Void> response = linkFeignClient.removeRule(linkId, ruleId, new RemoveRuleRequest(chatId));
                    return response.toString();
                },
                "Произошла непредвиденная ошибка"
        );
    }

    public String getRules(Long chatId, String[] params) {
        return processCommand(params, 1,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    ListResponse<GetRulesResponse> response = linkFeignClient.getRules(linkId);
                    return response.getData().toString();
                },
                "Произошла непредвиденная ошибка"
        );
    }

    public String getLinks(Long chatId, String[] params) {
        return processCommand(params, 0,
                () -> {
                    List<GetLinkResponse> data = linkFeignClient.getLinks(chatId).getData();
                    return data.toString();
                },
                "Произошла непредвиденная ошибка"
        );
    }

    private String processCommand(String[] params, int expectedParams, Supplier<String> action, String defaultMessage) {
        if (!validateParams(params, expectedParams)) {
            return String.format("Некорректное количество параметров. Ожидается: %d.", expectedParams);
        }
        return handleExceptions(action, defaultMessage);
    }

    private String handleExceptions(Supplier<String> action, String defaultMessage) {
        try {
            return action.get();
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (FeignException.UnprocessableEntity e) {
            return parseErrorResponse(e);
        } catch (Exception e) {
            return defaultMessage;
        }
    }

    private boolean validateParams(String[] params, int expected) {
        return params.length == expected + 1;
    }

    private Long parseLong(String value, String errorMessage) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private Map<String, String> parseRules(String ruleString) {
        Map<String, String> rules = new HashMap<>();
        String[] pairs = ruleString.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Неверный формат правил. Используйте формат: ключ=значение;ключ=значение");
            }
            rules.put(keyValue[0], keyValue[1]);
        }
        return rules;
    }

    private String parseErrorResponse(FeignException e) {
        String responseBody = e.contentUTF8();
        if (responseBody.contains("LINK_NOT_FOUND")) return "Ссылка не найдена.";
        if (responseBody.contains("LINK_NOT_ADDED")) return "Ссылка не отслеживается";
        if (responseBody.contains("LINK_ALREADY_ADDED")) return "Ссылка уже добавлена";
        if (responseBody.contains("LINK_NOT_SUPPORTED")) return "Ссылка не поддерживается";
        if (responseBody.contains("RULE_NOT_FOUND")) return "Правило не найдено.";
        if (responseBody.contains("RULE_NOT_ADDED")) return "Правило не отслеживается.";
        if (responseBody.contains("RULE_ALREADY_ADDED")) return "Правило уже добавлено";
        if (responseBody.contains("CHAT_NOT_FOUND")) return "Чат не зарегистрирован.";
        if (responseBody.contains("CHAT_ALREADY_REGISTERED")) return "Чат уже зарегистрирован.";
        return "Ошибка при обработке";
    }
}
