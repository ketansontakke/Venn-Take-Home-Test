package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.LoadRequest;
import com.example.demo.dto.response.LoadResponse;
import com.example.demo.service.LoadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/loads")
public class LoadController {

	private final LoadService service;

	public LoadController(LoadService service) {
		this.service = service;
	}

	@PostMapping
	public LoadResponse processSingle(@Valid @RequestBody LoadRequest request) {
		LoadResponse response = service.process(request);

		if (response == null) {
			throw new IllegalArgumentException("Duplicate load ID");
		}

		return response;
	}
}