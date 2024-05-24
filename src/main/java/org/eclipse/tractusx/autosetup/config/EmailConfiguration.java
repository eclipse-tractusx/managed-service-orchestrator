/********************************************************************************
#* Copyright (c) 2022,2024 T-Systems International GmbH
#* Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
#*
#* See the NOTICE file(s) distributed with this work for additional
#* information regarding copyright ownership.
#*
#* This program and the accompanying materials are made available under the
#* terms of the Apache License, Version 2.0 which is available at
#* https://www.apache.org/licenses/LICENSE-2.0.
#*
#* Unless required by applicable law or agreed to in writing, software
#* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#* License for the specific language governing permissions and limitations
#* under the License.
#*
#* SPDX-License-Identifier: Apache-2.0
#********************************************************************************/
package org.eclipse.tractusx.autosetup.config;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.eclipse.tractusx.autosetup.constant.EmailConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class EmailConfiguration {

	private final EmailConfigurationProperty emailConfigurationProperty;

    @Bean
    public MimeMessage mimeMessage() {
       
    	Session session = Session.getInstance(properties(), new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfigurationProperty.getUsername(), emailConfigurationProperty.getPassword());
            }
        });
        return new MimeMessage(session);
    }

    @Bean
    public Properties properties() {
        Properties props = new Properties();
        props.put("mail.smtp.user", emailConfigurationProperty.getUsername());
        props.put("mail.smtp.host", emailConfigurationProperty.getHost());
        props.put("mail.smtp.port", emailConfigurationProperty.getPort());
        props.put("mail.smtp.starttls.enable", emailConfigurationProperty.getStartTlsEnable());
        props.put("mail.smtp.auth", emailConfigurationProperty.getAuth());
        return props;
    }
}