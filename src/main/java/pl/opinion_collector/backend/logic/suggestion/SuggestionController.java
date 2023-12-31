package pl.opinion_collector.backend.logic.suggestion;


import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.opinion_collector.backend.database_communication.model.User;
import pl.opinion_collector.backend.logic.suggestion.dto.AddSuggestionDto;
import pl.opinion_collector.backend.logic.suggestion.dto.AnswerSuggestionDto;
import pl.opinion_collector.backend.logic.suggestion.dto.PublicSuggestionDto;
import pl.opinion_collector.backend.logic.suggestion.dto.SuggestionDto;
import pl.opinion_collector.backend.logic.user.UserFacade;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin
@RestController
@RequestMapping("/suggestions")
public class SuggestionController {

    @Autowired
    private Suggestions suggestionFacade;
    @Autowired
    private UserFacade userFacade;

    /**
     * All Suggestions endpoint
     *
     * @return list of all Suggestions of user
     */
    @GetMapping("/user")
    public ResponseEntity<List<PublicSuggestionDto>> getUserSuggestions(HttpServletRequest req) {
        User user = userFacade.getUserByToken(getBearerTokenHeader(req));
        return ResponseEntity.ok().body(suggestionFacade.getUserSuggestions(user.getUserId()).stream()
                .map(PublicSuggestionDto::map).toList());
    }

    /**
     * Endpoint for all Suggestions
     *
     * @return - list of all Suggestions
     */
    @GetMapping("/get")
    public ResponseEntity<List<SuggestionDto>> getAllSuggestions() {
        return ResponseEntity.ok().body(suggestionFacade.getAllSuggestions());
    }

    /**
     * Current user adds suggestion
     *
     * @param addSuggestionDto - sku and description
     */
    @ApiParam(
            name = "addSuggestionDto",
            type = "AddSuggestionDto",
            value = "Contains crucial info about suggestion to be added: sku (product identifier), " +
                    "description (content of suggestion)",
            required = true)
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuggestionDto> addSuggestion(
            HttpServletRequest req,
            @RequestBody AddSuggestionDto addSuggestionDto
    ) {

        String sku = addSuggestionDto.getSku();
        String description = addSuggestionDto.getDescription();
        User user = userFacade.getUserByToken(getBearerTokenHeader(req));
        return ResponseEntity.ok().body(suggestionFacade.addSuggestion(user.getUserId(), sku, description));
    }

    @ApiParam(
            name = "answerSuggestionDto",
            type = "AnswerSuggestionDto",
            value = "Contains crucial reply to suggestion: suggestionId (what suggestion is being answered), " +
                    "suggestionStatus (what status should suggestion have (DECLINED / PENDING / DONE))" +
                    "suggestionReply (content of reply)",
            required = true)
    @PutMapping(value = "/reply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuggestionDto> replyToSuggestion(
            HttpServletRequest req,
            @RequestBody AnswerSuggestionDto answerSuggestionDto
    ) {

        Integer suggestionId = answerSuggestionDto.getSuggestionId();
        String suggestionStatus = answerSuggestionDto.getSuggestionStatus().name();
        String suggestionReply = answerSuggestionDto.getSuggestionReply();


        User user = userFacade.getUserByToken(getBearerTokenHeader(req));
        
        if (!user.getAdmin()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }

        return ResponseEntity.ok().body(suggestionFacade.replySuggestion(user.getUserId(), suggestionId,
                suggestionStatus, suggestionReply));
    }


    public static String getBearerTokenHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authorizationHeader.substring("Bearer ".length());
    }

}
