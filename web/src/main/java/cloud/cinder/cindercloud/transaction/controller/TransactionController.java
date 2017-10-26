package cloud.cinder.cindercloud.transaction.controller;

import cloud.cinder.cindercloud.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/tx")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(method = GET)
    public String getTransactions(final Pageable pageable,
                                  final ModelMap modelMap,
                                  @RequestParam(name = "q", required = false) final Optional<String> searchKey) {
        if (searchKey.isPresent()) {
            modelMap.put("transactions", transactionService.find(searchKey.get(), pageable));
            modelMap.put("q", searchKey.get());
        } else {
            modelMap.put("transactions", transactionService.getLastTransactions(pageable));
            modelMap.put("q", "");
        }
        return "transactions/list";
    }


    @RequestMapping(value = "/{hash}", method = GET)
    public DeferredResult<ModelAndView> getTransaction(@PathVariable("hash") final String hash) {
        final DeferredResult<ModelAndView> result = new DeferredResult<>();
        transactionService.getTransaction(hash).subscribe(tx -> {
            final ModelAndView modelAndView = new ModelAndView("transactions/transaction");
            modelAndView.addObject("tx", tx);
            result.setResult(modelAndView);
        }, onError -> {
            result.setErrorResult("error");
        });
        return result;
    }

}