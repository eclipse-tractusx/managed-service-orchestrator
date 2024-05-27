/********************************************************************************
 * Copyright (c) 2022, 2023 T-Systems International GmbH
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.autosetup.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Resources;
import org.eclipse.tractusx.autosetup.constant.ToolType;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.utility.Certutil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CertificateManagerTest {
    @InjectMocks
    private CertificateManager certificateManager;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Test
    void createCertificate() {
        Customer customer = Customer.builder()
                .organizationName("Jaguar")
                .organizationUnitName("Unit Name")
                .email("customer.xyz.com")
                .contactNumber("9998767896")
                .tanNumber("123")
                .registrationNumber("3456")
                .country("DU")
                .state("GN")
                .city("BL")
                .build();

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.SDE_WITH_EDC_TRACTUS)
                .label("SDE")
                .build();
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("bpnNumber","BPN1234567");
        Map<String, String> resultMap = certificateManager.createCertificate(customer, selectedTools, mockInputMap, null);
        assertEquals(3, resultMap.size());
        assertEquals("BPN1234567", mockInputMap.get("bpnNumber"));
    }

    @Test
    void utilTest() throws IOException, CertificateException {
        try (var pemStream = Resources.getInputStream("cx-test.crt")) {
            var pem = new String(pemStream.readAllBytes());
            var cert = Certutil.loadCertificate(pem);
            var clientId = Certutil.getClientId(cert);
            var selfsigncertificate = Certutil.getAsString(cert);
            //var selfsigncertificateprivatekey = Certutil.getAsString(cert.);

            assertEquals("A0:BE:B6:A7:BD:E1:AD:06:51:9B:D1:30:11:BD:B0:27:DB:1F:08:44:keyid:A0:BE:B6:A7:BD:E1:AD:06:51:9B:D1:30:11:BD:B0:27:DB:1F:08:44", clientId);
            assertEquals(selfsigncertificate,"-----BEGIN CERTIFICATE-----\n" +
                    "MIIDxzCCAq+gAwIBAgIJANwo4Suo25ecMA0GCSqGSIb3DQEBCwUAMF0xCzAJBgNV\n" +
                    "BAYTAkRFMQ8wDQYDVQQHDAZCZXJsaW4xDDAKBgNVBAoMA0JNVzEvMC0GA1UEAwwm\n" +
                    "Y29ubmVjdG9yLmN4LXByZXByb2QuZWRjLmF3cy5ibXcuY2xvdWQwHhcNMjIwNzE1\n" +
                    "MTEzODI5WhcNMzIwNzEyMTEzODI5WjBdMQswCQYDVQQGEwJERTEPMA0GA1UEBwwG\n" +
                    "QmVybGluMQwwCgYDVQQKDANCTVcxLzAtBgNVBAMMJmNvbm5lY3Rvci5jeC1wcmVw\n" +
                    "cm9kLmVkYy5hd3MuYm13LmNsb3VkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
                    "CgKCAQEAnwQ2dHSLDysb8tLnRrwUUSHNFyq+dxEjRGTcmlwNMyK4OPPxyZsc69Bi\n" +
                    "15XL+DlSbExz6Rp0bCV+E9Btj9c9TIL8/2OluU2kOYtfV7/fu0oxXaKur/5gZ/kP\n" +
                    "B0lP9SevxMj9tsOchs7jYOEVrNsjSEkp4O3J2QyYzi3Q9cKu/RHVP6Vs+XXlvT9s\n" +
                    "WHQWGWDHFgGvNT8wT4Agy7GxMeELc0CW4EX3u+7B0xnmgzXQPEQt8o68b69BZVpo\n" +
                    "AWPm8flZ4M1XmQwj+nZAGxPvLuPU/iOF45Z9dij0sh9T0YHTcqmj5sEIjM9aayIK\n" +
                    "YgaPMsOlZKr1KwvcXAz8p5HOWjmvZwIDAQABo4GJMIGGMB0GA1UdDgQWBBSgvran\n" +
                    "veGtBlGb0TARvbAn2x8IRDAfBgNVHSMEGDAWgBSgvranveGtBlGb0TARvbAn2x8I\n" +
                    "RDAJBgNVHRMEAjAAMAsGA1UdDwQEAwIFoDAsBglghkgBhvhCAQ0EHxYdT3BlblNT\n" +
                    "TCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwDQYJKoZIhvcNAQELBQADggEBACB7rmwH\n" +
                    "6Gm/reNovv0RC5sMrWBm2YSpNbpIrJh4njdughSFu1E95UXvIy+WQQ4E/26ELDEM\n" +
                    "bINv1U1B59fhAftBx5pKbNjFR7eLPtb7hcHgZih7cQI64bk1dWsSCiD7U4HtncHb\n" +
                    "V6ExBclPbXulHExyVb7Vf1En+LhQz4HJDJMb6LzWEddH6xBaiAUc5E5sJQ2sKBOS\n" +
                    "F/SXx8yZWBCLIZOu1GzSBnui0oi3mCHXkPJoXvUFDKoK8BYuut5NzKLHplVqdm/Y\n" +
                    "l8wMZyXS1o8/b4fKJK9+Dpt8awj4hYujN5zJBnD3f9JxEEMp7JwZ8GC4lx4ix+v1\n" +
                    "uBm24UVauSGjp3E=\n" +
                    "-----END CERTIFICATE-----\n" +
                    "");
            assertEquals(clientId,"A0:BE:B6:A7:BD:E1:AD:06:51:9B:D1:30:11:BD:B0:27:DB:1F:08:44:keyid:A0:BE:B6:A7:BD:E1:AD:06:51:9B:D1:30:11:BD:B0:27:DB:1F:08:44");

        }
    }
}