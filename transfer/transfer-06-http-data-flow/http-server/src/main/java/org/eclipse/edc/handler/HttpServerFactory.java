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

import com.sun.net.httpserver.HttpServer;
import org.eclipse.edc.exception.UnableToStartServerException;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerFactory {

    public static HttpServer createHttpServer(int port) {
        try {
            return HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new UnableToStartServerException("Unable to start server at port " + port, e);
        }
    }
}
