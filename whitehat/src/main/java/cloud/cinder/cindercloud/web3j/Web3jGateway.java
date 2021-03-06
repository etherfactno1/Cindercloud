package cloud.cinder.cindercloud.web3j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import javax.annotation.PostConstruct;

@Component
public class Web3jGateway {

    @Autowired
    private Web3j cindercloud;
    @Autowired
    @Qualifier("local")
    private Web3j localWeb3j;

    private Web3j currentProvider;

    @PostConstruct
    @Scheduled(fixedDelay = 20000)
    private void init() {
        currentProvider = updateCurrentProvider();
    }

    public Web3j web3j() {
        return currentProvider;
    }

    private Web3j updateCurrentProvider() {
        try {
            cindercloud.ethBlockNumber().send();
            return cindercloud;
        } catch (final Exception ex) {
            return localWeb3j;
        }
    }
}
