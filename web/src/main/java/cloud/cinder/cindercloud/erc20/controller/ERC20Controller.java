package cloud.cinder.cindercloud.erc20.controller;

import cloud.cinder.cindercloud.coinmarketcap.dto.Currency;
import cloud.cinder.cindercloud.cryptocompare.service.TokenPriceService;
import cloud.cinder.cindercloud.erc20.controller.dto.AddressTokenDto;
import cloud.cinder.cindercloud.token.service.ERC20Service;
import cloud.cinder.cindercloud.token.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "/address/{address}")
public class ERC20Controller {

    private final DecimalFormat formatter = new DecimalFormat("##.######");

    @Autowired
    private ERC20Service erc20Service;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenPriceService tokenPriceService;

    @RequestMapping(value = "/tokens")
    public String balanceOf(final @PathVariable("address") String address, final ModelMap modelmap) {
        List<AddressTokenDto> tokens = tokenService.findAll()
                .stream()
                .map(x -> {
                    final double rawBalance = erc20Service.balanceOf(address, x.getAddress()).doubleValue();
                    return new AddressTokenDto(
                            x,
                            formatter.format(rawBalance),
                            rawBalance,
                            tokenPriceService.getPriceAsString(x.getSymbol(), Currency.EUR, rawBalance),
                            tokenPriceService.getPriceAsString(x.getSymbol(), Currency.USD, rawBalance)
                    );
                })
                .filter(x -> x.getRawBalance() > 0)
                .collect(Collectors.toList());
        if (tokens.size() > 0) {
            tokenService.importAddress(address);
        }
        modelmap.put("tokens", tokens);
        return "addresses/tokens :: tokenlist";
    }

    @RequestMapping(value = "/token-transfers")
    public String tokenTransfers(final @PathVariable("address") String address, final ModelMap modelmap) {
        modelmap.put("tokenTransfers", tokenService.findTransfersByFromOrTo(address));
        return "addresses/tokens :: transfers";
    }

    @RequestMapping(value = "/has-token-transfers")
    @ResponseBody
    public boolean hasTokenTransfers(final @PathVariable("address") String address) {
        return tokenService.hasTokenTransfersFromOrTo(address);
    }
}
