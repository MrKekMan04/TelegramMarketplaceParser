package ru.overcode.gateway.service.link.formatter;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AliexpressLinkFormatter implements LinkFormatter {

    private static final String ALIEXPRESS_HOST = "aliexpress.ru";

    private static final Pattern ALIEXPRESS_PATTERN =
            Pattern.compile("^https://aliexpress\\.ru/item/(?<itemId>\\d+)\\.html(\\?.*)?$");

    @Override
    public String getHost() {
        return ALIEXPRESS_HOST;
    }

    @Override
    public URI format(URI url) {
        Matcher matcher = ALIEXPRESS_PATTERN.matcher(url.toString().toLowerCase());
        if (matcher.matches()) {
            return URI.create("https://aliexpress.ru/item/" + matcher.group("itemId") + ".html");
        }
        return null;
    }
}
