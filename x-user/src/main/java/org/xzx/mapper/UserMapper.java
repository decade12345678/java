package org.xzx.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.xzx.entity.dto.FriendHandleDTO;
import org.xzx.entity.dto.FriendRequestDTO;
import org.xzx.entity.pojo.FriendForm;
import org.xzx.entity.pojo.RequestForm;
import org.xzx.entity.pojo.UserInfo;
import org.xzx.entity.vo.RequestedVO;
import org.xzx.entity.vo.UserVO;

import java.util.List;

@Mapper
public interface UserMapper {
    //更新用户在线状态
    @Update("update user set status =#{status} where id =#{id}")
    void updateStatus(Long id, int status);
    //根据用户名查用户信息
    @Select("select * from user where user_name = #{user_name}")
    UserInfo getByUsername(String user_name);
    //根据用户名查ID
    @Select("select id from user where user_name=#{user_name}")
    Long getUserId(String user_name);
    //根据用户id查自己好友列表中的好友
    @Select("select u.* from user u join friend f on u.id = f.friend_id where (f.user_id = #{userId} and f.status=1)")
    List<UserVO> getFriends(Long userId);
    //根据关键词查用户
    @Select("select * from user where lower(user_name) like lower(concat('%', #{searchKeyword}, '%')) or lower(chat_name) like lower(concat('%', #{searchKeyword}, '%'))")
    List<UserInfo> getSearchFriend(String searchKeyword);
    //判断是否在好友列表
    @Select("select status from friend where user_id =#{id} and friend_id=#{friend_id}")
    Integer isFriend(Long id, Long friend_id);
    //插入好友申请记录
    @Insert("insert into requestform ( chat_name, user_name, image, requested_user_name, request_time, request_message) VALUES ( #{chat_name}, #{user_name}, #{image},#{requested_user_name},#{request_time},#{request_message})")
    void CreateForm(RequestForm requestForm);
    //查找自己是否有被申请的记录
    @Select("select * from requestform where requested_user_name =#{user_name}")
    List<RequestedVO> FriendRequestLook(String user_name);
    //查看申请表，看自己是发过这条记录还是说没发过，返回null或者发过的记录的status，而且是最新的这条
    @Select("select status from requestform where user_name=#{user_name} and requested_user_name=#{requested_user_name} and request_time = (select max(request_time) from requestform where user_name=#{user_name} and requested_user_name=#{requested_user_name})")
    Integer RequestCount(FriendRequestDTO friendRequestDTO);
    //更新申请表状态，而且是最新的这条，不能把之前处理过的全改了
    @Update("update requestform set status=#{status} where user_name=#{user_name} and requested_user_name=#{requested_user_name} and request_time = (select max_time from (select max(request_time) as max_time from requestform where user_name = #{user_name} and requested_user_name = #{requested_user_name} ) as temp ) ")
    void updateRequestHandel(FriendHandleDTO friendHandleDTO);
    //查看好友表中是否有过记录,返回该记录ID方便后续更改
    @Select("select id from friend where user_id =#{user_id} and friend_id =#{requested_id}")
    Long HaveBeFriend(Long user_id, Long requested_id);
    //更新好友表中两个用户的关系状态
    @Update("update friend set status =#{status} where id =#{haveId}")
    void updateFriendForm(Long haveId,int status);
    //插入好友表记录
    @Insert("insert into friend (user_id,friend_id,created_at,updated_at,status,chat_method) values(#{user_id},#{friend_id},#{created_at},#{updated_at},#{status},#{chat_method})")
    void insertFriend(FriendForm friendForm);
}
