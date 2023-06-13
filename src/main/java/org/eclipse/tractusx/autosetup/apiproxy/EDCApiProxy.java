package org.eclipse.tractusx.autosetup.apiproxy;

import java.net.URI;
import java.util.Map;

import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.node.ObjectNode;

import feign.Headers;

@FeignClient(name = "EDCApiProxy", url = "placeholder")
public interface EDCApiProxy {

	@PostMapping("/v2/assets")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createAsset(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

	@PostMapping("/v2/policydefinitions")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createPolicy(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

	@PostMapping("/v2/contractdefinitions")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createContractDefination(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

}
