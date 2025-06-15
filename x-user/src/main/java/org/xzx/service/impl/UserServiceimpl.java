package org.xzx.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.xzx.constant.ExceptionConstant;
import org.xzx.context.UserContext;
import org.xzx.entity.dto.*;
import org.xzx.exception.BaseException;
import org.xzx.mapper.UserMapper;
import org.xzx.entity.pojo.FriendForm;
import org.xzx.entity.pojo.RequestForm;
import org.xzx.entity.pojo.UserInfo;
import org.xzx.service.UserService;
import org.xzx.entity.vo.FriendSearchVO;
import org.xzx.entity.vo.RequestedVO;
import org.xzx.entity.vo.UserVO;
import org.xzx.utils.RedisUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceimpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtil redisUtil;
    @Override
    public UserInfo login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUser_name();
        String password = userLoginDTO.getPass_word();
        UserInfo user = userMapper.getByUsername(username);
        if (user == null) {
            throw new BaseException(ExceptionConstant.USER_NOTFOUND);
        }
        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPass_word())) {
            //密码错误
            throw new BaseException(ExceptionConstant.PASSWORD_ERROR);
        }
        return user;
    }

    @Override
    public List<UserVO> getFriend(Long userId) {
        return userMapper.getFriends(userId);
    }

    @Override
    public List<FriendSearchVO> SearchFriend(SearchDTO searchDTO) {
        if (searchDTO.getActiveCategory().equals("user")) {
            List<UserInfo> userInfos =  userMapper.getSearchFriend(searchDTO.getSearchKeyword());
            List<FriendSearchVO> friendSearchVOs = new ArrayList<>();
            Long UserId = UserContext.getCurrentId();
            //遍历用户信息表，将需要的数据复制到需要传给前端的视图对象，再查该用户是否为自己好友，把status赋给该对象。
            userInfos.forEach(userInfo ->{
                FriendSearchVO friendSearchVO = new FriendSearchVO();
                BeanUtils.copyProperties(userInfo, friendSearchVO);
                Long judgeId=userInfo.getId();
                Integer status=userMapper.isFriend(UserId, judgeId);
                if(status==null||status==0) {
                    friendSearchVO.setStatus(0);
                }
                else friendSearchVO.setStatus(1);
                friendSearchVOs.add(friendSearchVO);
            });
            return friendSearchVOs;
        }
        else return null;

    }

    @Override
    public void FriendRequests(FriendRequestDTO friendRequestDTO) {
        Integer RequestStatus=userMapper.RequestCount(friendRequestDTO);
        Long id1 =userMapper.getUserId(friendRequestDTO.getUser_name());//本人的用户名
        Long id2 =userMapper.getUserId(friendRequestDTO.getRequested_user_name());//被申请人的用户名
        Integer status=userMapper.isFriend(id1,id2);//我的好友列表有没有对方，如果没有:即为0
        //(从未发过申请，或者被拒绝了，或者同意之后又删除了
        if(RequestStatus==null||RequestStatus==2||(RequestStatus==1&&status==0)) {
            RequestForm requestForm= RequestForm.builder()
                .chat_name(friendRequestDTO.getChat_name())
                .user_name(friendRequestDTO.getUser_name())
                .image(friendRequestDTO.getImage())
                .requested_user_name(friendRequestDTO.getRequested_user_name())
                .request_time(LocalDateTime.now())
                .request_message(friendRequestDTO.getMessage())
                .build();
            userMapper.CreateForm(requestForm);
        }
        else throw new BaseException(ExceptionConstant.REQUEST_ERROR);
    }

    @Override
    public List<RequestedVO> FriendRequestLook(String user_name) {
        return userMapper.FriendRequestLook(user_name);
    }

    @Override
    @Transactional
    public void FriendRequestHandel(FriendHandleDTO friendHandleDTO) {
        //只更新申请表状态最新的那条
        userMapper.updateRequestHandel(friendHandleDTO);
        int status=friendHandleDTO.getStatus();
        //获取处理状态，1是同意，向好友表中更改已有记录或者插入
        if(status==1)
        {
        Long requested_id = UserContext.getCurrentId();
        Long user_id=userMapper.getUserId(friendHandleDTO.getUser_name());
        Long HaveId=userMapper.HaveBeFriend(user_id,requested_id);
        if(HaveId!=null)//如果好友表曾记录过，就直接更新status即可
        {
               Long HaveId2=userMapper.HaveBeFriend(requested_id,user_id);
               userMapper.updateFriendForm(HaveId,friendHandleDTO.getStatus());
               userMapper.updateFriendForm(HaveId2,friendHandleDTO.getStatus());
        }
        else//否则插入两条好友关系记录
        {
            FriendForm friendForm = FriendForm.builder()
                    .user_id(user_id)
                    .friend_id(requested_id)
                    .created_at(LocalDateTime.now())
                    .updated_at(LocalDateTime.now())
                    .status(status)
                    .chat_method(1)
                    .build();
            userMapper.insertFriend(friendForm);
            friendForm.setUser_id(requested_id);
            friendForm.setFriend_id(user_id);
            userMapper.insertFriend(friendForm);
        }
        }

    }

    @Override
    public void DeleteFriend(String userName) {
        Long user_id=UserContext.getCurrentId();
        Long friend_id=userMapper.getUserId(userName);
        Long HaveId=userMapper.HaveBeFriend(user_id,friend_id);
        Long HaveId2=userMapper.HaveBeFriend(friend_id,user_id);
        userMapper.updateFriendForm(HaveId,0);
        userMapper.updateFriendForm(HaveId2,0);
    }

    @Override
    public List<MessageDTO> initHistoryMessage(Long userId) {
        return redisUtil.getChatHistory(userId);
    }
}
