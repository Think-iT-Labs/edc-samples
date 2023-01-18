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

package org.eclipse.edc;

import com.sun.net.httpserver.HttpServer;
import org.eclipse.edc.handler.HttpServerFactory;
import org.eclipse.edc.handler.ReceiverHandler;

import java.util.Optional;

public class EdcHttpServer {

    static final String HTTP_PORT = "edc.http.server.port";

    public static void main(String[] args) {
        int port = Integer.parseInt(Optional.ofNullable(System.getenv(HTTP_PORT)).orElse("4000"));
        HttpServer server = HttpServerFactory.createHttpServer(port);
        server.createContext("/receiver", new ReceiverHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("server started at " + port);
    }
}
