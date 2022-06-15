package org.trading.controller;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.trading.drools.DroolsService;

@Controller
public class OrderController {
  private final DroolsService droolsService;

  @Autowired
  public OrderController(DroolsService droolsService) {
    this.droolsService = droolsService;
  }

  @GetMapping("/")
  public String index() {
    return "redirect:/index";
  }


  @GetMapping("/index")
  public String index(Model model) {
    var bids = droolsService.queryGetMidPrices();
    model.addAttribute("bids", bids.isEmpty() ? Collections.EMPTY_LIST : bids);
    return "index";
  }
}
