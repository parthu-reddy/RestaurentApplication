package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@FeignClient(name = "kyc-client", url = "${kyb.base.url:http://localhost:8080}")
public interface KycClient {

    @GetMapping("/mock/fssai")
    ResponseEntity<Map> verifyFssai(@RequestParam("fssai") String fssai);
    
    @GetMapping("/mock/gstin")
    ResponseEntity<Map> verifyGstin(@RequestParam("gstin") String gstin);

    @GetMapping("/mock/pennydrop")
    ResponseEntity<Map> verifyBankAccount(@RequestParam("account") String account, @RequestParam("ifsc") String ifsc);
}
