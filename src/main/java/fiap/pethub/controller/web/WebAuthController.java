package fiap.pethub.controller.web;

import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registrar/tutor")
    public String registrarTutorForm(Model model) {
        model.addAttribute("tutorForm", new ResponsavelRequest());
        return "registrar-tutor";
    }

    @PostMapping("/registrar/tutor")
    public String registrarTutor(@Valid @ModelAttribute("tutorForm") ResponsavelRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "registrar-tutor";
        }
        try {
            authService.registrarResponsavel(request);
            return "redirect:/login?registered";
        } catch (Exception e) {
            result.rejectValue("email", "error.tutorForm", e.getMessage());
            return "registrar-tutor";
        }
    }

    @GetMapping("/registrar/vet")
    public String registrarVetForm(Model model) {
        model.addAttribute("vetForm", new VeterinarioRequest());
        return "registrar-vet";
    }

    @PostMapping("/registrar/vet")
    public String registrarVet(@Valid @ModelAttribute("vetForm") VeterinarioRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "registrar-vet";
        }
        try {
            authService.registrarVeterinario(request);
            return "redirect:/login?registered";
        } catch (Exception e) {
            result.rejectValue("email", "error.vetForm", e.getMessage());
            return "registrar-vet";
        }
    }
}
