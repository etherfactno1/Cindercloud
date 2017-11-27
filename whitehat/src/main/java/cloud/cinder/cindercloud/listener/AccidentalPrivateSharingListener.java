package cloud.cinder.cindercloud.listener;

import cloud.cinder.cindercloud.credential.domain.LeakedCredential;
import cloud.cinder.cindercloud.credentials.service.CredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import rx.Subscription;

@Component
@Slf4j
public class AccidentalPrivateSharingListener {

    @Autowired
    private Web3j web3j;
    @Autowired
    private CredentialService credentialService;

    private Subscription subscription;

    @Scheduled(fixedDelay = 10000)
    public void start() {
        if (subscription == null) {
            log.debug("[Private Sharing] startup of subscription for accidental private sharing");
            this.subscription = subscribe();
        } else if (this.subscription.isUnsubscribed()) {
            log.debug("[Private Sharing] unsubscribed, resubbing to accidental private sharing");
            this.subscription = subscribe();
        }
    }

    private Subscription subscribe() {
        return web3j.pendingTransactionObservable()
                .subscribe(x -> {
                    if (x != null && x.getInput() != null && x.getInput().length() == 66) {
                        log.warn("{} might just accidently shared a private", x.getFrom());
                        credentialService.saveLeakedCredential(
                                LeakedCredential.builder()
                                        .address(x.getFrom())
                                        .privateKey(x.getInput())
                                        .build()
                        );
                    }
                }, error -> {
                    log.error("[Private Sharing]Problem with pending transactions, resubbing", error);
                    subscription.unsubscribe();
                    this.subscription = subscribe();
                });
    }
}
