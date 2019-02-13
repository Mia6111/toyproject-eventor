package me.toyproject.mia.controller;

import lombok.AllArgsConstructor;
import me.toyproject.mia.account.HostDto;
import me.toyproject.mia.service.AccountService;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/hosts")
@AllArgsConstructor
public class HostController {

    private AccountService accountService;

    @GetMapping("/hosts/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        Resource<HostDto> resource = new Resource<>(accountService.findHostById(id));
        resource.add(linkTo(methodOn(HostController.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(resource);
    }
}
