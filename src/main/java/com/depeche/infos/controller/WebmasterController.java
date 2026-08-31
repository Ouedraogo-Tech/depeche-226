package com.depeche.infos.controller;

import com.depeche.infos.entity.Webmaster;
import com.depeche.infos.service.WebmasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webmasters")
public class WebmasterController {

    private final WebmasterService webmasterService;

    public WebmasterController(WebmasterService webmasterService) {
        this.webmasterService = webmasterService;
    }

    @PostMapping
    public ResponseEntity<Webmaster> creer(@RequestBody Webmaster webmaster) {
        Webmaster nouveauWebmaster = webmasterService.creerWebmaster(webmaster);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauWebmaster);
    }
}