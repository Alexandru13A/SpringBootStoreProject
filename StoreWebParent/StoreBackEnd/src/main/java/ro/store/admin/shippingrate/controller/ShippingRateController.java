package ro.store.admin.shippingrate.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.shippingrate.ShippingRateService;
import ro.store.common.entity.Country;
import ro.store.common.entity.ShippingRate;
import ro.store.common.exception.rate.ShippingRateAlreadyExistsException;
import ro.store.common.exception.rate.ShippingRateNotFoundException;


@Controller
public class ShippingRateController {

  private ShippingRateService shippingRateService;

  public ShippingRateController(ShippingRateService shippingRateService) {
    this.shippingRateService = shippingRateService;
  }

  private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";
  

  @GetMapping("/shipping_rates")
  public String listFirstPage() {
    return defaultRedirectURL;
  }

  @GetMapping("shipping_rates/page/{pageNum}")
  public String listByPage(
      @PagingAndSortingParam(listName = "shippingRates",moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
      @PathVariable("pageNum") int pageNum) {

    shippingRateService.listByPage(pageNum, helper);
    return "shipping_rates/shipping_rates";

  }

  @GetMapping("/shipping_rates/new")
  public String newRate(Model model) {
    List<Country> listCountries = shippingRateService.listAllCountries();

    model.addAttribute("shippingRate", new ShippingRate());
    model.addAttribute("listCountries", listCountries);
    model.addAttribute("pageTitle", "New Rate");

    return "shipping_rates/shipping_rate_form";
  }

  @PostMapping("/shipping_rates/save")
  public String saveRate(ShippingRate rate, RedirectAttributes re) {

    try {
      shippingRateService.save(rate);
      re.addFlashAttribute("message", "The shipping rate has been saved successfully.");
    } catch (ShippingRateAlreadyExistsException e) {
      re.addFlashAttribute("message", e.getMessage());
      System.out.println(e.getMessage());
    }
    return "redirect:/shipping_rates";
  }

  @GetMapping("/shipping_rates/edit/{id}")
  public String editRate(@PathVariable("id") Integer id, Model model, RedirectAttributes re) {

    try {
      ShippingRate shippingRate = shippingRateService.get(id);
      List<Country> listCountries = shippingRateService.listAllCountries();

      model.addAttribute("listCountries", listCountries);
      model.addAttribute("shippingRate", shippingRate);
      model.addAttribute("pageTitle", "Edit Rate ID: " + id);
      return "shipping_rates/shipping_rate_form";

    } catch (ShippingRateNotFoundException e) {
      re.addFlashAttribute("message", e.getMessage());
      return "redirect:/shipping_rates";
    }
  }

  @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
  public String updateCODSupport(@PathVariable("id") Integer id,
      @PathVariable("supported") Boolean supported, RedirectAttributes redirectAttributes) {

    try {
      shippingRateService.updateCODSupport(id, supported);
      String message =  "COD support for shipping rate ID: " + id + " has been updated.";
      redirectAttributes.addFlashAttribute("rateMessage",message);
    } catch (ShippingRateNotFoundException e) {
      redirectAttributes.addFlashAttribute("rateMessage", e.getMessage());
    }

    return "redirect:/shipping_rates";
  }

  @GetMapping("/shipping_rates/delete/{id}")
  public String deleteRate(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

    try {
      shippingRateService.delete(id);
      redirectAttributes.addFlashAttribute("message", "COD support for shipping rate ID: " + id + " has been deleted.");
    } catch (ShippingRateNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }

    return "redirect:/shipping_rates";

  }

}
