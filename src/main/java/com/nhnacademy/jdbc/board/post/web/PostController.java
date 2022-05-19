package com.nhnacademy.jdbc.board.post.web;

import com.nhnacademy.jdbc.board.post.domain.Post;
import com.nhnacademy.jdbc.board.post.service.PostService;
import com.nhnacademy.jdbc.board.user.domain.User;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("/postView/{page}")
    public String getPostView(@PathVariable int page, ModelMap modelMap, HttpSession session){
        User user = (User) session.getAttribute("login");

        modelMap.put("postViews", postService.viewPosts(page));
        modelMap.put("page", page);
        modelMap.put("maxPage", postService.getMaxPage());
        modelMap.put("isAdmin", user.isAdmin());
        return "postView";
    }

    @GetMapping("/postInsert")
    public String getPostInsert() {
        return "postInsertForm";
    }

    @PostMapping("/postInsert")
    public String postPostInsert(@RequestParam String title,
                                 @RequestParam String content,
                                 HttpSession session) {
        User user = (User) session.getAttribute("login");
        postService.createPost(user.getUserNum(), title, content);
        return "redirect:/postView/0";
    }

    @GetMapping("/postDetail/{postNum}")
    public String getPostDetail(@PathVariable long postNum, ModelMap modelMap, HttpSession session) {
        User user = (User) session.getAttribute("login");
        Post post = postService.getPost(postNum);

        modelMap.put("post", post);
        modelMap.put("comments", postService.viewComments(postNum));
        modelMap.put("isWriter", Objects.equals(user.getUserId(), post.getWriterId()));
        modelMap.put("isAdmin", user.isAdmin());
        return "postDetailView";
    }

    @GetMapping("/postModify/{postNum}")
    public String getPostModify(@PathVariable long postNum, ModelMap modelMap) {
        modelMap.put("post", postService.getPost(postNum));
        return "postModifyForm";
    }

    @PostMapping("/postModify/{postNum}")
    public String postPostModify(@PathVariable long postNum,
                                 @RequestParam String title,
                                 @RequestParam String content,
                                 HttpSession session) {
        User user = (User) session.getAttribute("login");
        postService.modifyPost(postNum, title, content, user.getUserNum());
        return "redirect:/postView/0";
    }

    @GetMapping("/postDelete/{postNum}")
    public String getPostDelete(@PathVariable long postNum) {
        postService.deletePost(postNum);
        return "redirect:/postView/0";
    }

    @PostMapping("/commentInsert/{postNum}")
    public String postPostModify(@PathVariable long postNum,
                                 @RequestParam String commentContent,
                                 HttpSession session) {
        User user = (User) session.getAttribute("login");
        postService.createComment(postNum, user.getUserNum(), commentContent);
        return "redirect:/postDetail/{postNum}";
    }

    @GetMapping("/postDeleteList/{page}")
    public String getPostDeleteList(@PathVariable int page, ModelMap modelMap){
        modelMap.put("postViews", postService.viewPosts(page));
        modelMap.put("page", page);
        modelMap.put("maxPage", postService.getMaxPage());
        return "postDeleteList";
    }

    @GetMapping("/postRestore/{postNum}")
    public String getPostRestore(@PathVariable long postNum) {
        postService.restorePost(postNum);
        return "redirect:/postDeleteList/0";
    }
}
