package pmf.kg.eid_api.controller;

import com.andric.EidInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pmf.kg.eid_api.entity.EidInfoEntity;
import pmf.kg.eid_api.service.EidInfoService;
import pmf.kg.eid_api.service.EidService;

import java.io.IOException;

@RestController
@RequestMapping("/api/eid")
@RequiredArgsConstructor
public class EidController {

    private final EidService service;
    private final EidInfoService eidInfoService;


    @GetMapping("/status")
    public String status() {
        return service.isCardPresent() ? "CARD_PRESENT" : "NO_CARD";
    }

    @GetMapping("/info")
    public EidInfo info() {
        EidInfo info = service.getCurrentInfo();
        if (info == null) {
            throw new IllegalStateException("Nema ubačene kartice");
        }
        return info;
    }

    // Endpoint za osvežavanje čitača
    @GetMapping("/refresh")
    public String refreshReader() {
        service.refreshReader(); // Ponovno traženje čitača
        return "Čitač je osvežen. Pokušavam ponovo da pronađem čitač.";
    }

    @GetMapping("/user")
    public EidInfoEntity user(@RequestParam String personalNumber) throws IOException {
        return eidInfoService.findByPersonalNumber(personalNumber);
    }
}

