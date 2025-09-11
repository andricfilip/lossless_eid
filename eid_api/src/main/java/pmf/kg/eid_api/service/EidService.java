package pmf.kg.eid_api.service;

import com.andric.EidCard;
import com.andric.EidInfo;
import com.andric.Reader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pmf.kg.eid_api.websocket.EidWebSocketHandler;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class EidService {
    private static final Logger logger = LoggerFactory.getLogger(EidService.class);

    private final AtomicReference<EidCard> currentCard = new AtomicReference<>();
    private final AtomicReference<EidInfo> currentInfo = new AtomicReference<>();
    private final EidWebSocketHandler wsHandler;
    private final EidInfoService eidInfo;
    private Reader reader; // Čitač koji ćemo ponovo inicijalizovati

//    // Konstruktor za inicijalizaciju WebSocket handler-a
//    public EidService(EidWebSocketHandler wsHandler) {
//        this.wsHandler = wsHandler;
//    }

    @PostConstruct
    public void init() {
        initializeReader();
    }

    // Metod za inicijalizaciju čitača
    private void initializeReader() {
        try {
            List<CardTerminal> terminals = TerminalFactory.getDefault().terminals().list();

            if (terminals.isEmpty()) {
                String errorMessage = "Nema pronađenih eID čitača";
                logger.error(errorMessage);
                wsHandler.broadcast(errorMessage);  // Obaveštavanje frontenda
                return;
            }

            CardTerminal terminal = terminals.get(0);
            reader = new Reader(terminal); // Povezivanje sa čitačem

            // Dodavanje listenera za praćenje ubačene/izvađene kartice
            reader.addCardListener(new Reader.ReaderListener() {
                @Override
                public void inserted(EidCard card) {
                    try {
                        logger.info("Kartica ubačena.");
                        currentCard.set(card);
                        EidInfo info = card.readEidInfo();
                        eidInfo.save(card);
                        currentInfo.set(info);
                        wsHandler.broadcast("CARD_INSERTED");
                    } catch (CardException e) {
                        logger.error("Greška pri čitanju kartice", e);
                        wsHandler.broadcast("CARD_READ_ERROR");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void removed() {
                    logger.info("Kartica izvađena.");
                    currentCard.set(null);
                    currentInfo.set(null);
                    wsHandler.broadcast("CARD_REMOVED");
                }
            });

        } catch (Exception e) {
            logger.error("Greška pri inicijalizaciji čitača", e);
            wsHandler.broadcast("READER_INIT_ERROR");
        }
    }

    // Metod za ponovno traženje čitača
    public void refreshReader() {
        initializeReader(); // Ponovno pokušavanje inicijalizacije čitača
    }

    public boolean isCardPresent() {
        return currentCard.get() != null;
    }

    public EidInfo getCurrentInfo() {
        return currentInfo.get();
    }
}
