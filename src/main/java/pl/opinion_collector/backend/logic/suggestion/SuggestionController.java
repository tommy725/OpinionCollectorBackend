package pl.opinion_collector.backend.logic.suggestion;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.opinion_collector.backend.database_communication.model.*;
import pl.opinion_collector.backend.logic.product.ProductFacade;
import pl.opinion_collector.backend.logic.user.UserFacade;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/suggestions")
public class SuggestionController {

    @Autowired
    private Suggestions suggestionFacade;
    private UserFacade userFacade;
    private ProductFacade productFacade;

    /**
     * All Suggestions endpoint
     *
     * @return list of all Suggestions of user
     */
    @GetMapping("/user")
    public List<SuggestionShortDto> getUserSuggestions() {
        User user = userFacade.getUserByToken(getBearerTokenHeader());
        if (user == null) {
            throw new IllegalArgumentException("Authentication failed!");
        }
        List<Suggestion> userSuggestions = suggestionFacade.getUserSuggestions(user.getUserId());
        return userSuggestions.stream().map(this::mapSuggestionToDto).collect(Collectors.toList());
    }

    /**
     * Endpoint for all Suggestions
     *
     * @return - list of all Suggestions
     */
    @GetMapping("/get")
    public List<SuggestionShortDto> getAllSuggestions() {
        return suggestionFacade.getAllSuggestions().stream().
                map(this::mapSuggestionToDto).collect(Collectors.toList());
    }

    /**
     * Current user adds suggestion
     *
     * @param argHolder - sku and description
     */
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addSuggestion(@RequestBody ArgHolder argHolder) {

        String sku = argHolder.getSku();
        String description = argHolder.getDescription();

        // check whether user is valid
        User user = userFacade.getUserByToken(getBearerTokenHeader());
        if (user == null) {
            throw new IllegalArgumentException("Authentication failed!");
        }
        // check whether product is valid
        Product product = productFacade.getProductBySku(sku);
        if (product == null) {
            throw new IllegalArgumentException("Product with given SKU does not exist");
        }
        // add suggestion
        suggestionFacade.addSuggestion(user.getUserId(), product, description);
    }

    @PutMapping(value = "/reply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void replyToSuggestion(@RequestBody ArgHolderTwo argHolderTwo) {

        Integer suggestionId = argHolderTwo.getSuggestionId();
        String suggestionStatus = argHolderTwo.getSuggestionStatus();
        String suggestionReply = argHolderTwo.getSuggestionReply();

        // check whether user is valid
        User user = userFacade.getUserByToken(getBearerTokenHeader());
        if (user == null) {
            throw new IllegalArgumentException("Authentication failed!");
        }
        if (!user.getAdmin()) {
            throw new IllegalArgumentException("Only admin can reply to suggestions!");
        }

        suggestionFacade.replySuggestion(user.getUserId(), suggestionId, suggestionStatus, suggestionReply);

    }

    /**
     * Helper classes used to avoid dumping huge JSON onto frontend
     */
    @Data
    @AllArgsConstructor
    private class SuggestionShortDto {
        private Long suggestionId;
        private Review reviewId;
        private Long userId;
        private String description;
        private Long productId;
        private Long reviewerId;
    }

    @Data
    @AllArgsConstructor
    private class ArgHolder {
        private String description;
        private String sku;
    }

    @Data
    @AllArgsConstructor
    private class ArgHolderTwo {
        Integer suggestionId;
        String suggestionStatus;
        String suggestionReply;
    }


    /**
     * Helper functions
     */
    private SuggestionShortDto mapSuggestionToDto(Suggestion suggestion) {
        return new SuggestionShortDto(suggestion.getSuggestionId(), suggestion.getReview(),
                suggestion.getUserId().getUserId(), suggestion.getDescription(),
                suggestion.getProductId().getProductId(), suggestion.getReviewerId().getUserId());
    }


    public static String getBearerTokenHeader() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
    }

}
