package ro.store.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.store.admin.user.UserService;
import ro.store.admin.user.security.StoreUserDetails;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.User;

@Controller
public class AccountController {

  @Autowired
  private UserService service;

  @GetMapping("/account")
  public String viewDetails(@AuthenticationPrincipal StoreUserDetails loggedUser, Model model) {
    String email = loggedUser.getUsername();
    User user = service.getUserByEmail(email);
    model.addAttribute("user", user);
    return "/users/account_form";
  }

  @PostMapping("/account/update")
  private String saveDetails(User user, RedirectAttributes redirectAttributes,
      @AuthenticationPrincipal StoreUserDetails loggedUser,
      @RequestParam("image") MultipartFile multipartFile) throws IOException {

    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      user.setPhoto(fileName);
      User savedUser = service.updateAccount(user);

      String uploadDir = "user-photo/" + savedUser.getId();

      FileUploadUtil.cleanDirectory(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    } else {
      if (user.getPhoto().isEmpty())
        user.setPhoto(null);
      service.updateAccount(user);
    }
    loggedUser.setFirstName(user.getFirstName());
    loggedUser.setLastName(user.getLastName());

    redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");
    return getRedirectUrlToAffectedUser(user);
  }

  private String getRedirectUrlToAffectedUser(User user) {
    return "redirect:/account";
  }

}
