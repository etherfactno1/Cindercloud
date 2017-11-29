package cloud.cinder.cindercloud.wallet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.web3j.crypto.Credentials;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Getter
public class GeneratedCredentials {

    final DateFormat formatter = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'Z'");

    private String wallet;
    private PrivateKey privateKey;

    public String getFilename() {
        return formatter.format(new Date()) + "--" + Credentials.create(privateKey.asString()).getAddress() + ".json";
    }
}