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

drop table IF EXISTS app_service_catalog_mapping_tbl;


CREATE TABLE app_service_catalog_mapping_tbl (
	service_id varchar(255) NOT NULL,
	customer varchar(255) NOT NULL,
	canonical_service_id varchar(255) NULL,
	CONSTRAINT app_service_catalog_mapping_tbl_pkey PRIMARY KEY (service_id, customer)
);

ALTER TABLE app_service_catalog_mapping_tbl ADD CONSTRAINT fk_app_service_catalog_mapping_tbl_canonical_service_id FOREIGN KEY (canonical_service_id) REFERENCES app_service_catalog_tbl(canonical_service_id);

INSERT INTO app_service_catalog_mapping_tbl
(service_id, customer, canonical_service_id)
VALUES('12345', 'BMW', 'SDE-WITH-EDC-TX');