package pl.opinion_collector.backend.logic.user.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.opinion_collector.backend.database_communication.model.User;
import pl.opinion_collector.backend.logic.exception.type.ForbiddenException;
import pl.opinion_collector.backend.logic.user.UserFacade;
import pl.opinion_collector.backend.logic.user.dto.Mapper;
import pl.opinion_collector.backend.logic.user.dto.UserDto;
import pl.opinion_collector.backend.logic.user.dto.UserWithIdDto;
import pl.opinion_collector.backend.logic.user.payload.request.LoginArg;
import pl.opinion_collector.backend.logic.user.payload.request.SignupArg;
import pl.opinion_collector.backend.logic.user.payload.response.JwtArg;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserFacade userFacade;
    private static final Mapper mapper = new Mapper();

    /**
     * get all Users data
     *
     * @param httpServletRequest - HTTP Servlet Request
     * @return - {@link List<UserWithIdDto>}
     */
    @ApiOperation(value = "Get all users and their data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 401, message = "U are not authorized"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @GetMapping
    public ResponseEntity<List<UserWithIdDto>> getUsers(@ApiParam(name = "HTTP Servlet Request",
            value = "Request information for HTTP servlets") HttpServletRequest httpServletRequest) {
        if (!userFacade.getUserByToken(httpServletRequest.getHeader(AUTHORIZATION)
                .substring("Bearer ".length())).getAdmin()) {
            throw new ForbiddenException("U are not allowed to this resource");
        }
        return new ResponseEntity<>(userFacade.getAllUsers().stream()
                .map(mapper::mapUserWithId).toList(), HttpStatus.OK);
    }

    /**
     * register new User
     *
     * @param registerRequest - {@link SignupArg}
     * @param httpServletRequest - HTTP Servlet Request
     * @return {@link UserDto}
     */
    @ApiOperation(value = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Registration succeed"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 401, message = "U are not authorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 406, message = "Not allowed user registration data")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@ApiParam(name = "Registration request body",
                                                        value = "Body of the request")
                                                @Valid @RequestBody SignupArg registerRequest,
                                            @ApiParam(name = "HTTP Servlet Request",
                                                    value = "Request information for HTTP servlets")
                                          HttpServletRequest httpServletRequest) {
        String headerToken = "";
        User user = new User();
        String headerAuth = httpServletRequest.getHeader(AUTHORIZATION);
        if (headerAuth != null && !headerAuth.equals("")) {
            headerToken = httpServletRequest.getHeader(AUTHORIZATION).substring("Bearer ".length());
            user = userFacade.getUserByToken(headerToken);
            if (!headerToken.isBlank() && !user.getAdmin())
                throw new ForbiddenException("U can't register new account while u are logged in");
        }

        if (registerRequest.getIsAdmin() && !headerToken.isBlank() && user.getAdmin()) {
            return ResponseEntity.ok().body(mapper.mapUser(
                    userFacade.registerAdmin(registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getPictureUrl())));
        } else if (Boolean.TRUE.equals(registerRequest.getIsAdmin())) {
            throw new ForbiddenException("U are not authorized to register new admin");
        } else
            return ResponseEntity.ok().body(mapper.mapUser(
                    userFacade.register(registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getPictureUrl())));

    }

    /**
     * login to the user
     *
     * @param loginRequest - {@link LoginArg}
     * @param httpServletRequest - HTTP Servlet Request
     * @return - {@link JwtArg}
     */
    @ApiOperation(value = "Login to the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login succeed"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 401, message = "U are not authorized"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtArg> login(@ApiParam(name = "Login request", value = "Login request information")
                                       @Valid @RequestBody LoginArg loginRequest,
                                        @ApiParam(name = "HTTP Servlet Request",
                                        value = "Request information for HTTP servlets")
                                        HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader(AUTHORIZATION) != null)
            throw new ForbiddenException("U can't log in while u are logged in");

        String token = userFacade.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok().body(new JwtArg(token, mapper.mapUser(userFacade.getUserByToken(token))));
    }

    /**
     * update User data
     *
     * @param updateRequest - {@link SignupArg}
     * @param id - User id
     * @return - {@link UserWithIdDto}
     */
    @ApiOperation(value = "Update the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update succeed"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 401, message = "U are not authorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 406, message = "Now allowed user update data")
    })
    @PutMapping("/update")
    public ResponseEntity<UserWithIdDto> update(@ApiParam(name = "Update request",
            value = "Update request body needed to update user") @RequestBody SignupArg updateRequest,
            @RequestParam(name = "userId") Integer id) {
        return ResponseEntity.ok().body(mapper.mapUserWithId(
                userFacade.updateUser(id, updateRequest.getFirstName(),
                updateRequest.getLastName(), updateRequest.getEmail(),
                updateRequest.getPassword(), updateRequest.getPictureUrl(),
                updateRequest.getIsAdmin())));
    }

}
