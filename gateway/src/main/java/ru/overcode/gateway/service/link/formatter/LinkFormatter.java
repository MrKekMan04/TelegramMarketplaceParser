package ru.overcode.gateway.service.link.formatter;

import java.net.URI;

public interface LinkFormatter {

    String getHost();

    URI format(URI url);
}
