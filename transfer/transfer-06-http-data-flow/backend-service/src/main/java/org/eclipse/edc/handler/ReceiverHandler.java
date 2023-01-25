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
import org.eclipse.edc.parser.HttpQueryParserBase;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

/**
 * A handler which is invoked to process HTTP exchanges.
 * Each HTTP exchange is handled by one of these handlers.
 */
public class ReceiverHandler implements HttpHandler {

    private final HttpQueryParserBase queryParser = new HttpQueryParserBase();

    /**
     * Handle the given request and generate an appropriate response.
     * See {@link HttpExchange} for a description of the steps
     * involved in handling an exchange.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is {@code null}
     * @throws IOException          if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // parse request
        var parameters = new HashMap<>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        queryParser.parseQuery(query, parameters);

        System.out.println("Request Body: " + new String(exchange.getRequestBody().readAllBytes()));
        exchange.sendResponseHeaders(200, 0);
    }
}
