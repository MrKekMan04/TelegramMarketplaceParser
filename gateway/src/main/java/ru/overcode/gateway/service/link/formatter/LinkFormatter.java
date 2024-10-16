package ru.overcode.gateway.service.link.formatter;

import java.net.URI;

public interface LinkFormatter {

    boolean intercept(URI url);

    URI format(URI url);
}
