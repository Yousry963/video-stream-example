package com.example.stream.audiovideo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.VideoService;

import reactor.core.publisher.Mono;

@RestController
public class VideoController {

	@Autowired
	private VideoService videoService;

	@GetMapping("/videos/{fileName}")
	public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList, @PathVariable("fileName") String fileName) {
		return Mono.just(videoService.getVideo(fileName, httpRangeList));
	}

}