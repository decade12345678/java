package org.xzx.service;

import org.xzx.entity.dto.*;
import org.xzx.entity.pojo.UserInfo;
import org.xzx.entity.vo.FriendSearchVO;
import org.xzx.entity.vo.RequestedVO;
import org.xzx.entity.vo.UserVO;

import java.util.List;

public interface UserService {
   UserInfo login(UserLoginDTO userLoginDTO);

   List<UserVO> getFriend(Long userId);

    List<FriendSearchVO> SearchFriend(SearchDTO searchDTO);

    void FriendRequests(FriendRequestDTO friendRequestDTO);

    List<RequestedVO> FriendRequestLook(String user_name);

    void FriendRequestHandel(FriendHandleDTO friendHandleDTO);

    void DeleteFriend(String userName);

    List<MessageDTO> initHistoryMessage(Long userId);
}
