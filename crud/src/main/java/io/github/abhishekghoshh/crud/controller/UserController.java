package io.github.abhishekghoshh.crud.controller;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.*;
import com.linecorp.armeria.server.annotation.decorator.CorsDecorator;
import io.github.abhishekghoshh.crud.controller.advide.GlobalExceptionHandler;
import io.github.abhishekghoshh.crud.model.Blog;
import io.github.abhishekghoshh.crud.model.User;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeAlreadyExistingException;
import io.github.abhishekghoshh.crud.neo4j.exception.NodeNotFoundException;
import io.github.abhishekghoshh.crud.service.UserService;

import java.util.List;

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
    public HttpResponse userById(@Param("user-id") Long userId) throws NodeNotFoundException {
        User user = userService.getUserById(userId);
        return HttpResponse.ofJson(HttpStatus.OK, user);
    }

    @Post("/users/")
    public HttpResponse addUser(@RequestObject User user) throws NodeAlreadyExistingException {
        user = userService.saveUser(user);
        return HttpResponse.ofJson(HttpStatus.CREATED, user);
    }

    @Put("/users/:user-id")
    public HttpResponse updateUser(@Param("user-id") Long userId, @RequestObject User user) throws NodeNotFoundException, NodeAlreadyExistingException {
        user = userService.updateUser(userId, user);
        return HttpResponse.ofJson(HttpStatus.OK, user);
    }

    @Delete("/users/:user-id")
    public HttpResponse deleteUser(@Param("user-id") Long userId) throws NodeNotFoundException {
        userService.deleteUser(userId);
        return HttpResponse.of(HttpStatus.ACCEPTED);
    }

    @Get("/users/:user-id/posts")
    public HttpResponse userWithPosts(@Param("user-id") Long userId) {
        User User = new User();
        return HttpResponse.ofJson(HttpStatus.OK, User);
    }

    @Get("/users/:user-id/posts/:post-id")
    public HttpResponse getBlogByPostIdAndUserId(@Param("user-id") Long userId, @Param("post-id") Long postId) {
        Blog blog = new Blog(System.currentTimeMillis(), "My second blog", "This is my second blog");
        return HttpResponse.ofJson(HttpStatus.OK, blog);
    }

    @Post("/users/:user-id/posts")
    public HttpResponse addBlogToUser(@Param("user-id") Long userId, @RequestObject Blog blog) {
        blog = new Blog(System.currentTimeMillis(), blog.getTitle(), blog.getContent());
        return HttpResponse.ofJson(HttpStatus.CREATED, blog);
    }

    @Put("/users/:user-id/posts/:post-id")
    public HttpResponse updateBlogOfUser(@Param("user-id") Long userId, @Param("post-id") Long postId, @RequestObject Blog updatedBlog) {
        Blog blog = new Blog(postId, updatedBlog.getTitle(), updatedBlog.getContent());
        return HttpResponse.ofJson(HttpStatus.OK, blog);
    }

    @Delete("/users/:user-id/posts/:post-id")
    public HttpResponse deleteBlogOfUser(@Param("user-id") Long userId, @Param("post-id") Long postId) {
        return HttpResponse.of(HttpStatus.NO_CONTENT);
    }
}