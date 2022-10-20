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

CREATE TABLE app_service_catalog_tbl (
	canonical_service_id varchar(255) NOT NULL,
	ct_name varchar(255) NULL,
	service_tools varchar(255) NULL,
	workflow varchar(255) NULL,
	CONSTRAINT app_service_catalog_tbl_pkey PRIMARY KEY (canonical_service_id)
);

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('DFT-WITH-EDC', 'DFT-WITH-EDC', '[{"tool": "DFT_WITH_EDC","label": "dftwthedc"}]', 'EDC_DFT');

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('EDC', 'EDC', '[{"tool": "EDC","label": "edc"}]', 'EDC');
