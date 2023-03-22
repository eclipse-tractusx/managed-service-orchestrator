package org.eclipse.tractusx.autosetup.testservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ConnectorTestServiceProxy", url = "${connector.test.service.url}")
public interface ConnectorTestServiceProxy {

	@PostMapping("/connector-test")
	public ConnectorTestServiceResponse verifyConnectorTestingThroughTestService(@RequestBody ConnectorTestRequest connectorTestRequest);

}
