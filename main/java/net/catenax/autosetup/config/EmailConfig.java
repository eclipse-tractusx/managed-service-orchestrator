/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.config;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Value("${mail.smtp.starttls.enable}")
    private Boolean startTlsEnable;

    @Value("${mail.smtp.auth}")
    private Boolean auth;

    @Bean
    public MimeMessage mimeMessage() {
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties(), new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Data_Intelligence_Hub_Login", "P/r8}rf5q)/Wr1gn");
            }
        });
        return new MimeMessage(session);
    }

    @Bean
    public Properties properties() {
        Properties props = new Properties();
        props.put("mail.smtp.user", "Data_Intelligence_Hub_Login");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", startTlsEnable);
        props.put("mail.smtp.auth", auth);
        return props;
    }
}