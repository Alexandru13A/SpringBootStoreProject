package ro.store.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.admin.aws.AmazonS3Util;
import ro.store.admin.currency.CurrencyRepository;
import ro.store.common.entity.Currency;
import ro.store.common.entity.Setting.GeneralSettingBag;
import ro.store.common.entity.Setting.Setting;

@Controller
public class SettingController {

  private SettingService settingService;
  private CurrencyRepository currencyRepository;

  public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
    this.settingService = settingService;
    this.currencyRepository = currencyRepository;
  }

  @GetMapping("/settings")
  public String listAll(Model model) {

    List<Setting> settings = settingService.listAllSettings();
    List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

    model.addAttribute("currencies", currencies);

    for (Setting setting : settings) {
      model.addAttribute(setting.getKey(), setting.getValue());
    }

    // for aws and concatenate with html for Store logo
    // model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);

    return "settings/settings";
  }

  // Save general settings
  @PostMapping("/settings/save_general")
  public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
      HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

    GeneralSettingBag settingBag = settingService.getGeneralSettings();
    saveSiteLogo(multipartFile, settingBag);
    saveCurrencySymbol(request, settingBag);
    updateSettingValuesFromForm(request, settingBag.list());

    redirectAttributes.addFlashAttribute("message", "General settings have been saved.");
    return "redirect:/settings";
  }

  // set and save new site logo
  private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      String value = "/site-logo/" + fileName;
      settingBag.updateSiteLogo(value);

      // UPLOAD THE PHOTO FOR LOGO AT AWS S3 DATABASE SERVER
      String uploadDirectoryForAWS = "site-logo";
      AmazonS3Util.deleteFolder(uploadDirectoryForAWS);
      AmazonS3Util.uploadFile(uploadDirectoryForAWS, fileName, multipartFile.getInputStream());

      // UPLOAD THE PHOTO FOR LOGO AT THE SERVER
      // String uploadDirectory = "site-logo/";
      // FileUploadUtil.cleanDirectory(uploadDirectory);
      // FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
    }
  }

  // saved setting for Currency Symbol that appear to price
  private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {

    Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
    Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

    if (findByIdResult.isPresent()) {
      Currency currency = findByIdResult.get();
      settingBag.updateCurrencySymbol(currency.getSymbol());
    }

  }

  private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
    for (Setting setting : listSettings) {
      String value = request.getParameter(setting.getKey());
      if (value != null) {
        setting.setValue(value);
      }
    }

    settingService.saveAll(listSettings);
  }

  @PostMapping("/settings/save_mail_server")
  public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {

    List<Setting> mailServerSettings = settingService.getMailServerSettings();
    updateSettingValuesFromForm(request, mailServerSettings);
    redirectAttributes.addFlashAttribute("message", "Mail server settings have been save");

    return "redirect:/settings";
  }

  @PostMapping("/settings/save_mail_templates")
  public String saveMailTemplatesSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {

    List<Setting> mailTemplateSettings = settingService.getMailTemplatesSettings();
    updateSettingValuesFromForm(request, mailTemplateSettings);
    redirectAttributes.addFlashAttribute("message", "Mail template settings have been save");

    return "redirect:/settings";
  }

  @PostMapping("/settings/save_payment")
  public String savePaymentSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {

    List<Setting> paymentSettings = settingService.getPaymentSettings();
    updateSettingValuesFromForm(request, paymentSettings);

    redirectAttributes.addFlashAttribute("message", "Payment settings have been saved");

    return "redirect:/settings#payment";
  }

}
