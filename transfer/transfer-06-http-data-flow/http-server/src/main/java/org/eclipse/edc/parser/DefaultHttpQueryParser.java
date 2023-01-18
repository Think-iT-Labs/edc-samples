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

package org.eclipse.edc.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultHttpQueryParser implements HttpQueryParserBase {
    @Override
    public void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query == null) {
            return;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] param = pair.split("=");
            String key = null;
            String value = null;
            if (param.length > 0) {
                key = URLDecoder.decode(param[0],
                        System.getProperty("file.encoding"));
            }

            if (param.length > 1) {
                value = URLDecoder.decode(param[1],
                        System.getProperty("file.encoding"));
            }

            if (parameters.containsKey(key)) {
                var obj = parameters.get(key);
                if (obj instanceof List<?>) {
                    List<String> values = (List<String>) obj;
                    values.add(value);

                } else if (obj instanceof String) {
                    List<String> values = new ArrayList<>();
                    values.add((String) obj);
                    values.add(value);
                    parameters.put(key, values);
                }
            } else {
                parameters.put(key, value);
            }
        }
    }
}
