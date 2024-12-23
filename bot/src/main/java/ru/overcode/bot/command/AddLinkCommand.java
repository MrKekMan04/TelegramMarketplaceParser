package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.overcode.bot.config.feign.linkclient.LinkFeignClient;
import ru.overcode.bot.dto.AddLinkRequest;
import ru.overcode.bot.dto.AddLinkResponse;
import ru.overcode.shared.api.Response;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddLinkCommand implements Command {

    private static final String COMMAND_NAME = "/add-link";
    private static final String COMMAND_DESCRIPTION = "Добавить ссылку в отслеживаемые";

    private final LinkFeignClient linkFeignClient;

    @Override
    public String command() {
        return COMMAND_NAME;
    }

    @Override
    public String description() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        Chat chat = update.message().chat();
        Long chatId = update.message().chat().id();
        String[] parameters = update.message().text().split(" ");

        try {
            if (parameters.length < 2) {
                return new SendMessage(chatId, "Укажите ссылку после команды.");
            }

            String url = parameters[1];
            URI uri = URI.create(url);;

            AddLinkRequest request = new AddLinkRequest(chatId, uri);

            Response<AddLinkResponse> addLinkResponse = linkFeignClient
                    .addLink(request);

            if (!addLinkResponse.getErrors().isEmpty()) {
                return new SendMessage(chatId, "Ссылка не найдена или не поддерживается ." + addLinkResponse.getErrors().getLast());
            }

            return new SendMessage(chatId, "Ссылка успешно добавлена. ID: " + addLinkResponse.getData().linkId());

        } catch (IllegalArgumentException e) {
            return new SendMessage(chatId, "Некорректный формат ссылки.");
        } catch (FeignException.UnprocessableEntity e) {
                return new SendMessage(chatId, "422"+ e.getMessage());
        } catch (FeignException.InternalServerError e) {
            return new SendMessage(chatId, "500" + e.getMessage());
        } catch (FeignException.NotFound e) {
            return new SendMessage(chatId, "404" + e.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId, "Произошла непредвиденная ошибка при добавлении ссылки." + e.getMessage());
        }
    }
}