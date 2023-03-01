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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenSSLClientManager {

	public String executeCommand(String command) {
		String[] cmd = { "bash", "-c", command };
		List<String> strList = new ArrayList<>();
		strList.addAll(Arrays.asList(cmd));
		StringBuilder output = new StringBuilder();
		Process process = null;
		try {
			process = new ProcessBuilder(strList).start();
		} catch (IOException e) {
			Thread.currentThread().interrupt();
			
		}
		if(process != null) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
				int exitVal = process.waitFor();
				if (exitVal == 0) {
					log.info("OpenSSL command executed sucessfully");
				} else {
					log.error("Error in command: " + LogUtil.encode(output.toString()));
				}

			} catch (InterruptedException | IOException e) {
				Thread.currentThread().interrupt();
				
			}
		}
		return output.toString();
	}

}
