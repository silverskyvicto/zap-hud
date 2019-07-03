/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2019 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;

/** Unit test for {@link HttpUpgradeProxyListener}. */
public class HttpUpgradeProxyListenerTest {
    private static String httpUrl = "http://www.example.com/";
    private static String httpsUrl = "https://www.example.com/";

    @Test
    public void upgradedHttpUrlUpgradedInHtml() throws URIException {
        // Given
        URI httpUri = new URI(httpUrl, true);
        String body = "<head></head><body>" + httpUrl + "</body><body></body>";
        ExtensionHUD ext = Mockito.mock(ExtensionHUD.class);
        Mockito.when(ext.isHudEnabled()).thenReturn(true);
        Mockito.when(ext.isUpgradedHttpsDomain(httpUri)).thenReturn(true);

        HttpUpgradeProxyListener hupl = new HttpUpgradeProxyListener(ext);
        HttpMessage msg = new HttpMessage();
        msg.getRequestHeader().setURI(httpUri);
        msg.getResponseHeader().setHeader(HttpResponseHeader.CONTENT_TYPE, "text/html");
        msg.setResponseBody(body);

        // When
        hupl.onHttpResponseReceived(msg);

        // Then
        assertEquals(body.replace(httpUrl, httpsUrl), msg.getResponseBody().toString());
    }

    @Test
    public void upgradedHttpUrlsUpgradedInHtml() throws URIException {
        // Given
        URI httpUri = new URI(httpUrl, true);
        String body =
                "<head></head><body>"
                        + httpUrl
                        + " "
                        + httpUrl
                        + " "
                        + httpUrl
                        + "</body><body></body>";
        ExtensionHUD ext = Mockito.mock(ExtensionHUD.class);
        Mockito.when(ext.isHudEnabled()).thenReturn(true);
        Mockito.when(ext.isUpgradedHttpsDomain(httpUri)).thenReturn(true);

        HttpUpgradeProxyListener hupl = new HttpUpgradeProxyListener(ext);
        HttpMessage msg = new HttpMessage();
        msg.getRequestHeader().setURI(httpUri);
        msg.getResponseHeader().setHeader(HttpResponseHeader.CONTENT_TYPE, "text/html");
        msg.setResponseBody(body);

        // When
        hupl.onHttpResponseReceived(msg);

        // Then
        assertEquals(body.replace(httpUrl, httpsUrl), msg.getResponseBody().toString());
    }

    @Test
    public void upgradedHttpUrlNotUpgradedInPng() throws URIException {
        // Given
        URI httpUri = new URI(httpUrl, true);
        String body = "any old text" + httpUrl + "blah blah blah";
        ExtensionHUD ext = Mockito.mock(ExtensionHUD.class);
        Mockito.when(ext.isHudEnabled()).thenReturn(true);
        Mockito.when(ext.isUpgradedHttpsDomain(httpUri)).thenReturn(true);

        HttpUpgradeProxyListener hupl = new HttpUpgradeProxyListener(ext);
        HttpMessage msg = new HttpMessage();
        msg.getRequestHeader().setURI(httpUri);
        msg.getResponseHeader().setHeader(HttpResponseHeader.CONTENT_TYPE, "image/png");
        msg.setResponseBody(body);

        // When
        hupl.onHttpResponseReceived(msg);

        // Then
        assertEquals(body, msg.getResponseBody().toString());
    }

    @Test
    public void notUpgradedHttpUrlNotUpgradedInHtml() throws URIException {
        // Given
        URI httpUri = new URI(httpUrl, true);
        String body = "<head></head><body>" + httpUrl + "</body><body></body>";
        ExtensionHUD ext = Mockito.mock(ExtensionHUD.class);
        Mockito.when(ext.isHudEnabled()).thenReturn(true);
        Mockito.when(ext.isUpgradedHttpsDomain(httpUri)).thenReturn(false);

        HttpUpgradeProxyListener hupl = new HttpUpgradeProxyListener(ext);
        HttpMessage msg = new HttpMessage();
        msg.getRequestHeader().setURI(httpUri);
        msg.getResponseHeader().setHeader(HttpResponseHeader.CONTENT_TYPE, "text/html");
        msg.setResponseBody(body);

        // When
        hupl.onHttpResponseReceived(msg);

        // Then
        assertEquals(body, msg.getResponseBody().toString());
    }
}