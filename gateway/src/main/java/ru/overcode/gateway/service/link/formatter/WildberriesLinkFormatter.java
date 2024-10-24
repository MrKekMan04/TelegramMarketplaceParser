package ru.overcode.gateway.service.link.formatter;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WildberriesLinkFormatter implements LinkFormatter {

    private static final String WILDBERRIES_HOST = "www.wildberries.ru";
    private static final Pattern WILDBERRIES_PATTERN =
            Pattern.compile("^https://www.wildberries.ru/catalog/(?<itemId>\\d+)/.*$");

    @Override
    public String getHost() {
        return WILDBERRIES_HOST;
    }

    @Override
    public URI format(URI url) {
        Matcher matcher = WILDBERRIES_PATTERN.matcher(url.toString().toLowerCase());
        if (matcher.matches()) {
            return URI.create("https://www.wildberries.ru/catalog/" + matcher.group("itemId"));
        }
        return url;
    }
}
