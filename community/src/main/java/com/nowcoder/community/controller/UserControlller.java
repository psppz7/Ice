package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserControlller {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private Producer kaptcha;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage()
    {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST) //上传头像
    public String uploadHeader(MultipartFile headerImage, Model model)
    {
        if(headerImage==null)
        {
            model.addAttribute("error","未选择图片");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));  //文件后缀名
        if(StringUtils.isBlank(suffix))
        {
            model.addAttribute("error","文件的格式不对");
            return "/site/setting";
        }
        filename = CommunityUtil.generateUUID() + suffix;

        File file = new File(uploadPath + "/" + filename);

        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        //更新当前用户的头像路径
        User user = hostHolder.getUser();

        String url = domain + contextPath + "/user/header/" + filename;

        userService.updateHeader(user.getId(),url);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response)
    {
        filename = uploadPath + "/" + filename;  //拼凑为本地路径
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);

        try(OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(filename);)
        {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer))!=-1)
            {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/setpassword",method = RequestMethod.POST)
    public String setPassword(String originPassword,String newPassword,Model model)
    {
        User user = hostHolder.getUser();
        if(user==null)
            return "redirect:/index";

        String currentPassword = user.getPassword();
        originPassword = CommunityUtil.md5(originPassword+user.getSalt());
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        if(!originPassword.equals(currentPassword))
        {
            model.addAttribute("originPasswordMsg","原密码不正确");
            return "/site/setting";
        }
        if(newPassword.equals(originPassword))
        {
            model.addAttribute("newPasswordMsg","新密码与原密码相同");
            return "/site/setting";
        }
        userService.updatePassword(user.getTicket(),user.getId(),newPassword);

        model.addAttribute("msg","密码修改成功，将为您跳转到登录页面");
        model.addAttribute("path","/login");
        return "/site/operate-result";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForgetPasswordPage()
    {
        return "/site/forget";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forgetPassword(String email, String code, String newPassword, HttpSession session,Model model)
    {
        String kaptcha = (String) session.getAttribute("kaptchaStr");
        if(!kaptcha.equalsIgnoreCase(code))
        {
            System.out.println(code);
            model.addAttribute("codeMsg","验证码错误");
            model.addAttribute("email",email);
            return "/site/forget";
        }
        userService.forgetPassword(email,newPassword);
        model.addAttribute("msg","密码重置成功");
        model.addAttribute("path","/login");
        return "/site/operate-result";
    }

    @RequestMapping(path = "/forget/kaptcha",method = RequestMethod.GET)
    @ResponseBody
    public String getForgetKapthca(@PathParam("email") String email, Model model, HttpSession session)
    {
        String code = kaptcha.createText();
        session.setAttribute("kaptchaStr",code);
      Map<String,String> map =  userService.getForgetKaptcha(email,code);
        if(map.get("emailMsg")!=null)
        {
          return CommunityUtil.getJsonString(0,null,null);
        }
        return "1";
    }

    @RequestMapping(path = "/profile/{id}",method = RequestMethod.GET)
    public String getProfilePage(Model model,@PathVariable("id") int userId)
    {
        User user = userService.findUserById(userId);
        long likeCount = likeService.findUserLikeCount(user.getId());
        boolean alreadyFollowed = followService.findIsFollowed(userId);
        long followerCount = followService.findFollowerCount(userId);
        long followeeCount = followService.findFolloweeCount(userId);

        model.addAttribute("alreadyFollowed",alreadyFollowed);
        model.addAttribute("userLikeCount",likeCount);
        model.addAttribute("user",user);
        model.addAttribute("followerCount",followerCount);
        model.addAttribute("followeeCount",followeeCount);


        return "/site/profile";
    }

    @RequestMapping(path = "/follower/{id}",method = RequestMethod.GET)
    public String getFollowerPage(Model model, Page page,@PathVariable("id") int userId)
    {
        User user = userService.findUserById(userId);
        page.setLimit(5);
        page.setPath("/user/follower");
        page.setRows((int) followService.findFollowerCount(userId));

       List<User> list = followService.findFollowerList(userId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> lists = new ArrayList<>();
       for(User u : list)

       {    Map<String,Object> map = new HashMap<>();
          Boolean alreadyFollow = followService.findIsFollowed(u.getId());
          map.put("alreadyFollow",alreadyFollow);
          map.put("follower",u);
          lists.add(map);
       }
       model.addAttribute("followers",lists);
       model.addAttribute("user",user);
       return "/site/follower";
    }

    @RequestMapping(path = "followee/{id}",method = RequestMethod.GET)
    public String getFolloweePage(Model model,Page page,@PathVariable("id") int userId)
    {
        User user = userService.findUserById(userId);
        page.setLimit(5);
        page.setPath("/user/followee");
        page.setRows((int) followService.findFollowerCount(userId));

        List<User> list = followService.findFolloweeList(userId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> lists = new ArrayList<>();
        for(User u : list)

        {    Map<String,Object> map = new HashMap<>();
            Boolean alreadyFollow = followService.findIsFollowed(u.getId());
            map.put("alreadyFollow",alreadyFollow);
            map.put("followee",u);
            lists.add(map);
        }
        model.addAttribute("followees",lists);
        model.addAttribute("user",user);
        return "/site/followee";
    }

}
