/********************************************************************************
 * Copyright (c) 2022,2024 T-Systems International GmbH
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.exception;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String DEFAULT_ERROR_VIEW = "error";

	@ExceptionHandler(NoDataFoundException.class)
	public ResponseEntity<String> handleNodataFoundException(NoDataFoundException ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<String> handleServiceException(ServiceException ex, WebRequest request) {
		return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handlePSQLException(Exception ex, WebRequest request) {
		return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<String> handleValidationException(ValidationException ex, WebRequest request) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex, WebRequest request) {
		log.error("FeignException: " + ex.getMessage());
		log.error("FeignException RequestBody: " + ex.request());
		log.error("FeignException ResponseBody: " + ex.contentUTF8());
		ObjectMapper objmap = new ObjectMapper();
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("msg", "Error in remote service execution");
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objmap.readValue(ex.contentUTF8(), Map.class);
			Object object = map.get("errors");
			if (object != null)
				errorResponse = prepareErrorResponse(object.toString());
		} catch (JsonMappingException e) {
			log.error("FeignException JsonMappingException " + e.getMessage());
		} catch (JsonProcessingException e) {
			log.error("FeignException JsonProcessingException " + e.getMessage());
		}

		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.status()));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		Map<String, String> errors = new HashMap<>();
		
		Object inputRequest = ex.getBindingResult().getTarget();
		if (inputRequest != null)
			log.error(LogUtil.encode(inputRequest.toString()));
		
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			Object fieldValue = ((FieldError) error).getRejectedValue();
			String errorMessage = error.getDefaultMessage();
			log.error(LogUtil.encode(fieldName+ "::"+fieldValue+" ->"+errorMessage));
			errors.put(fieldName, errorMessage);
		});
		
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	private Map<String, String> prepareErrorResponse(String errormsg) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("msg", errormsg);
		return errorResponse;
	}
	
}
