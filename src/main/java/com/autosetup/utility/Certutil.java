/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package com.autosetup.utility;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Certutil {

	 static {
	        Security.addProvider(new BouncyCastleProvider());
	    }

	    public static String getAki(X509Certificate cert) {
	        byte[] extensionValue = cert.getExtensionValue("2.5.29.35");
	        byte[] octets = DEROctetString.getInstance(extensionValue).getOctets();
	        AuthorityKeyIdentifier authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(octets);
	        byte[] keyIdentifier = authorityKeyIdentifier.getKeyIdentifier();
	        return HexFormat.ofDelimiter(":").withUpperCase().formatHex(keyIdentifier);
	    }

	    public static String getSki(X509Certificate cert) {
	        var extensionValue = cert.getExtensionValue("2.5.29.14");
	        var octets = DEROctetString.getInstance(extensionValue).getOctets();
	        SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.getInstance(octets);
	        var keyIdentifier = subjectKeyIdentifier.getKeyIdentifier();
	        return HexFormat.ofDelimiter(":").withUpperCase().formatHex(keyIdentifier);
	    }

	    public static X509Certificate loadCertificate(String pem) throws IOException, CertificateException {
	        try(var ts = new ByteArrayInputStream(pem.getBytes(UTF_8))) {
	            CertificateFactory fac  = CertificateFactory.getInstance("X509");
	            return  (X509Certificate) fac.generateCertificate(ts);
	        }
	    }

	    public static String getClientId(X509Certificate certificate) {
	        return getSki(certificate).concat(":keyid:").concat(getAki(certificate));
	    }

	    public static String getAsString(Object certificate) throws IOException {
	        StringWriter sw = new StringWriter();
	        try (JcaPEMWriter jpw = new JcaPEMWriter(sw)) {
	            jpw.writeObject(certificate);
	        }
	        return sw.toString();
	    }

	    public record CertKeyPair(X509Certificate certificate, KeyPair keyPair){}
	    public static CertKeyPair generateSelfSignedCertificateSecret(String name, Integer days, Integer bits) throws GeneralSecurityException, OperatorCreationException, CertIOException {
	        var subject = new X500Principal(name);
	        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(Optional.ofNullable(bits).orElse(2048), new SecureRandom());
	        var keyPair = keyPairGenerator.generateKeyPair();
	        var notBefore = System.currentTimeMillis();
	        var notAfter = notBefore + (1000L * 3600L * 24 * Optional.ofNullable(days).orElse(365));
	        var certBuilder = new JcaX509v3CertificateBuilder(
	                subject, // signed by
	                BigInteger.ONE,
	                new Date(notBefore),
	                new Date(notAfter),
	                subject,
	                keyPair.getPublic()
	        );
	        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
	        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature + KeyUsage.keyEncipherment));
	        var spki = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
	        var ski = new BcX509ExtensionUtils().createSubjectKeyIdentifier(spki);
	        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);
	        var aki = new BcX509ExtensionUtils().createAuthorityKeyIdentifier(spki);
	        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, aki);
	        var signer = new JcaContentSignerBuilder(("SHA256withRSA")).build(keyPair.getPrivate());
	        var certHolder = certBuilder.build(signer);
	        return new CertKeyPair(new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder), keyPair);
	    }
}
