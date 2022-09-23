

CREATE TABLE auto_setup_trigger_tbl (
	trigger_id varchar(255) NOT NULL,
	autosetup_request text NULL,
	autosetup_result text NULL,
	autosetup_tenant_name varchar(255) NULL,
	created_timestamp varchar(255) NULL,
	modified_timestamp varchar(255) NULL,
	organization_name varchar(255) NULL,
	remark text NULL,
	status varchar(255) NULL,
	trigger_type varchar(255) NULL,
	CONSTRAINT auto_setup_trigger_tbl_pkey PRIMARY KEY (trigger_id)
);

CREATE TABLE auto_setup_trigger_details_tbl (
	id varchar(255) NOT NULL,
	created_date timestamp NULL,
	remark text NULL,
	status varchar(255) NULL,
	action varchar(255) NULL,
	step varchar(255) NULL,
	trigger_id varchar(255) NULL,
	CONSTRAINT auto_setup_trigger_details_tbl_pkey PRIMARY KEY (id)
);


ALTER TABLE auto_setup_trigger_details_tbl ADD CONSTRAINT fk_auto_setup_trigger_details_tbl_trigger_id FOREIGN KEY (trigger_id) REFERENCES auto_setup_trigger_tbl(trigger_id);