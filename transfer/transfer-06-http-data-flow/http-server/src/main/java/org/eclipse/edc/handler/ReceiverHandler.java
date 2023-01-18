/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.edc.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.eclipse.edc.parser.DefaultHttpQueryParser;
import org.eclipse.edc.parser.HttpQueryParserBase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ReceiverHandler implements HttpHandler {

    private final HttpQueryParserBase queryParser = new DefaultHttpQueryParser();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        queryParser.parseQuery(query, parameters);

        // send response
        StringBuilder response = new StringBuilder();
        for (var entry : parameters.entrySet()) {
            response.append(entry.getKey()).append(" = ").append(parameters.get(entry.getKey())).append("\n");
        }
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());

        os.close();
    }
}
