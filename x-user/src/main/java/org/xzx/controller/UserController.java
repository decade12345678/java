package org.xzx.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.xzx.Result.Result;
import org.xzx.context.UserContext;
import org.xzx.entity.dto.*;
import org.xzx.entity.pojo.UserInfo;
import org.xzx.properties.JwtProperties;
import org.xzx.service.UserService;
import org.xzx.utils.JwtUtil;
import org.xzx.entity.vo.FriendSearchVO;
import org.xzx.entity.vo.RequestedVO;
import org.xzx.entity.vo.UserLoginVO;
import org.xzx.entity.vo.UserVO;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags="用户相关管理接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private JwtProperties jwtProperties;

    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserInfo userInfo=userService.login(userLoginDTO);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getId());
        String token = JwtUtil.createJWT(
               jwtProperties.getSecretKey(),
                jwtProperties.getLoseTime(),
                claims);
        //获取消息记录
        List<MessageDTO> messageDTOList =userService.initHistoryMessage(userInfo.getId());

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(userInfo.getId())
                .user_name(userInfo.getUser_name())
                .image(userInfo.getImage())
                .chat_name(userInfo.getChat_name())
                .token(token)
                .textDTOList(messageDTOList)
                .build();

        return Result.success(userLoginVO);
    }


    @ApiOperation("根据当前登录用户查询所有好友")
    @GetMapping("/getFriend")
    public Result<List<UserVO>> getFriend() {
        Long userId = UserContext.getCurrentId();
        List<UserVO> friends=userService.getFriend(userId);
        return Result.success(friends);
    }

    @ApiOperation("根据当前用户id，关键字，分类查询对应内容")
    @PostMapping("/searchFriend")
    public Result<List<FriendSearchVO>> SearchFriend(@RequestBody SearchDTO searchDTO) {
        List<FriendSearchVO> contents =userService.SearchFriend(searchDTO);
        return Result.success(contents);
    }
    @ApiOperation("接收用户名和申请信息，发送好友申请")
    @PostMapping("/FriendRequest")
    public Result FriendRequests(@RequestBody FriendRequestDTO friendRequestDTO) {
        userService.FriendRequests(friendRequestDTO);
        return Result.success();
    }

    @ApiOperation("查看好友申请")
    @GetMapping("/FriendRequestLook/{user_name}")
    public Result<List<RequestedVO>>FriendRequestLook(@PathVariable String user_name) {
        List<RequestedVO> requestedVOS =userService.FriendRequestLook(user_name);
        return Result.success(requestedVOS);
    }
    @ApiOperation("处理好友申请")
    @PostMapping("/FriendRequestHandel")
    public Result FriendRequestHandel(@RequestBody FriendHandleDTO friendHandleDTO) {
        userService.FriendRequestHandel(friendHandleDTO);
        return Result.success();
    }

    @ApiOperation("单向删除好友")
    @PostMapping("/DeleteFriend")
    public Result DeleteFriend(@RequestBody DeleteDTO dto) {
         userService.DeleteFriend(dto.getUser_name());
         return Result.success();
    }

}
