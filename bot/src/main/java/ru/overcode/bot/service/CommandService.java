package ru.overcode.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.bot.config.feign.link.LinkFeignClient;
import ru.overcode.bot.dto.chat.RegistrationChatRequest;
import ru.overcode.bot.dto.link.AddLinkRequest;
import ru.overcode.bot.dto.link.AddLinkResponse;
import ru.overcode.bot.dto.link.RemoveLinkRequest;
import ru.overcode.bot.dto.rule.AddRuleRequest;
import ru.overcode.bot.dto.rule.GetRulesResponse;
import ru.overcode.bot.dto.rule.RemoveRuleRequest;
import ru.overcode.bot.dto.rule.RuleDto;
import ru.overcode.shared.api.ErrorDto;
import ru.overcode.shared.api.ListResponse;
import ru.overcode.shared.api.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final LinkFeignClient linkFeignClient;
    private final ObjectMapper objectMapper;

    public String registerChat(Long chatId, String[] params) {
        return processCommand(params, 0,
                () -> {
                    linkFeignClient.registerChat(new RegistrationChatRequest(chatId));
                    return "Чат успешно зарегистрирован";
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
                    linkFeignClient.removeLink(linkId, new RemoveLinkRequest(chatId));
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
                    Response<RuleDto> response = linkFeignClient.addRule(linkId, request);

                    String description = response.getData().ruleDescription();
                    return String.format("Правило с идентификатором %s успешно привязано к ссылке.\n%s", ruleId, description);
                },
                "Произошла непредвиденная ошибка при привязке правила."
        );
    }

    public String removeRule(Long chatId, String[] params) {
        return processCommand(params, 2,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    Long ruleId = parseLong(params[2], "Некорректный формат ID правила");
                    linkFeignClient.removeRule(linkId, ruleId, new RemoveRuleRequest(chatId));
                    return "Правило успешно удаленно";
                },
                "Произошла непредвиденная ошибка"
        );
    }

    public String getRules(String[] params) {
        return processCommand(params, 1,
                () -> {
                    Long linkId = parseLong(params[1], "Некорректный формат ID ссылки");
                    ListResponse<GetRulesResponse> response = linkFeignClient.getRules(linkId);

                    return response.getData().isEmpty()
                            ? "Нет правил для данной ссылки."
                            : response.getData().stream()
                            .map(rule -> "Правило с Id " + rule.ruleId() + " и описанием: " + rule.description())
                            .collect(Collectors.joining("\n"));
                },
                "Произошла непредвиденная ошибка"
        );
    }

    public String getLinks(Long chatId, String[] params) {
        return processCommand(params, 0,
                () -> linkFeignClient.getLinks(chatId).getData().stream()
                        .map(link -> {
                            Long linkId = link.linkId();
                            URI linkUrl = link.linkUrl();
                            List<RuleDto> rules = link.rules();

                            String rulesPart = rules.isEmpty()
                                    ? "без правил"
                                    : rules.stream()
                                    .map(RuleDto::ruleId)
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "));

                            return "Ссылка " + linkUrl + " с Id " + linkId + " и правилами: " + rulesPart + ".";
                        })
                        .collect(Collectors.joining("\n")),
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
        } catch (FeignException.UnprocessableEntity | FeignException.BadRequest e) {
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
        try {
            Response<?> response = objectMapper.readValue(e.contentUTF8(), Response.class);

            if (!response.getErrors().isEmpty()) {
                return response.getErrors().stream()
                        .map(ErrorDto::message)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception ex) {
            return "Ошибка при обработке ответа сервера";
        }
        return "Ошибка при обработке";
    }
}
