package org.eclipse.tractusx.autosetup.testservice.proxy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectorTestRequest {

	private String connectorHost;

	private String apiKeyHeader;

	private String apiKeyValue;

}
