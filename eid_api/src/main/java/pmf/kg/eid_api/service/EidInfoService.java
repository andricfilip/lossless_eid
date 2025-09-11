package pmf.kg.eid_api.service;

import com.andric.EidCard;
import com.andric.EidInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pmf.kg.eid_api.entity.EidInfoEntity;
import pmf.kg.eid_api.repository.EidInfoRepository;

import javax.smartcardio.CardException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EidInfoService {
    private final EidInfoRepository eidInfoRepository;

    @Transactional
    public void save(EidCard card) throws CardException, IOException {
        if (!eidInfoRepository.existsByPersonalNumber(card.readEidInfo().getPersonalNumber())) {
            eidInfoRepository.save(new EidInfoEntity(card));
        }
    }
    @Transactional
    public EidInfoEntity findByPersonalNumber(String personalNumber) throws IOException {
        EidInfoEntity eid = eidInfoRepository.findByPersonalNumber(personalNumber);
        saveImage(eid.getPhoto(),"slika.png");
        return eid;
    }


    public static void saveImage(byte[] imageBytes, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(imageBytes);
        }
    }

}
