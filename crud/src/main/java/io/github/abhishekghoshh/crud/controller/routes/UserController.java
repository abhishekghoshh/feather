package io.github.abhishekghoshh.crud.controller.routes;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.*;
import com.linecorp.armeria.server.annotation.decorator.CorsDecorator;
import io.github.abhishekghoshh.crud.controller.advide.GlobalExceptionHandler;
import io.github.abhishekghoshh.crud.exception.DuplicateResourceException;
import io.github.abhishekghoshh.crud.exception.ResourceException;
import io.github.abhishekghoshh.crud.exception.UserNotFoundException;
import io.github.abhishekghoshh.crud.model.Blog;
import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import io.github.abhishekghoshh.crud.service.UserService;

import java.util.List;

@PathPrefix("/api/v1")
@ExceptionHandler(value = GlobalExceptionHandler.class)
@CorsDecorator(origins = {"*"}, credentialsAllowed = true)
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get("/users/")
    public HttpResponse users() {
        List<User> users = userService.getUsers();
        return HttpResponse.ofJson(HttpStatus.OK, users);
    }

    @Get("/users/:user-id")
    public HttpResponse userById(@Param("user-id") Long userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);
        return HttpResponse.ofJson(HttpStatus.OK, user);
    }

    @Post("/users/")
    public HttpResponse addUser(@RequestObject User user) throws DuplicateResourceException {
        user = userService.saveUser(user);
        return HttpResponse.ofJson(HttpStatus.CREATED, user);
    }

    @Put("/users/:user-id")
    public HttpResponse updateUser(@Param("user-id") Long userId, @RequestObject User user) throws NodeNotFoundException, DuplicateResourceException {
        user = userService.updateUser(userId, user);
        return HttpResponse.ofJson(HttpStatus.OK, user);
    }

    @Delete("/users/:user-id")
    public HttpResponse deleteUser(@Param("user-id") Long userId) throws UserNotFoundException {
        userService.deleteUser(userId);
        return HttpResponse.of(HttpStatus.ACCEPTED);
    }

    @Get("/users/:user-id/blogs")
    public HttpResponse userWithBlogs(@Param("user-id") Long userId) throws UserNotFoundException {
        User User = userService.getUserWithBlogs(userId);
        return HttpResponse.ofJson(HttpStatus.OK, User);
    }

    @Get("/users/:user-id/blogs/:blog-id")
    public HttpResponse getBlogByBlogIdAndUserId(@Param("user-id") Long userId, @Param("blog-id") Long blogId) throws ResourceException {
        Blog blog = userService.getBlogOfUser(userId, blogId);
        return HttpResponse.ofJson(HttpStatus.OK, blog);
    }

    @Post("/users/:user-id/blogs")
    public HttpResponse addBlogToUser(@Param("user-id") Long userId, @RequestObject Blog blog) throws UserNotFoundException {
        blog = userService.addBlogsToUser(userId, blog);
        return HttpResponse.ofJson(HttpStatus.CREATED, blog);
    }

    @Put("/users/:user-id/blogs/:blog-id")
    public HttpResponse updateBlogOfUser(@Param("user-id") Long userId, @Param("blog-id") Long blogId, @RequestObject Blog updatedBlog) throws ResourceException {
        Blog blog = userService.updateBlogOfUser(userId, blogId, updatedBlog);
        return HttpResponse.ofJson(HttpStatus.OK, blog);
    }

    @Delete("/users/:user-id/blogs/:blog-id")
    public HttpResponse deleteBlogOfUser(@Param("user-id") Long userId, @Param("blog-id") Long blogId) throws ResourceException {
        userService.deleteBlogOfUser(userId, blogId);
        return HttpResponse.of(HttpStatus.ACCEPTED);
    }
}