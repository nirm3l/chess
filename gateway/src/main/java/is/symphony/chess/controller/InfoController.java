package is.symphony.chess.controller;

//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class InfoController {

//    @GetMapping("/info")
//    public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
//        return oauth2User
//                .mapNotNull(user -> {
//                    if (user.getAttribute("email") != null) {
//                        return user.getAttribute("email");
//                    }
//                    else {
//                        return user.getName();
//                    }
//                })
//                .map(name -> String.format("Hi, %s", name));
//    }
}
