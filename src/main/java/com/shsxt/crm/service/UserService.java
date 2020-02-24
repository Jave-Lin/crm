package com.shsxt.crm.service;

import com.shsxt.base.BaseService;
import com.shsxt.crm.dao.UserMapper;
import com.shsxt.crm.exceptions.ParamsException;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.utils.Md5Util;
import com.shsxt.crm.utils.UserIDBase64;
import com.shsxt.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService<User,Integer> {
@Autowired
    private UserMapper userMapper;
    public UserModel login(String userName, String userPwd){
        checkLoginParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(null==user,"用户不存在");
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(userPwd))),"密码错误");
        return buildUserModelInfo(user);
    }

    private UserModel buildUserModelInfo(User user) {
        return new UserModel(UserIDBase64.encoderUserID(user.getId()),user.getUserName(),user.getTrueName());
    }

    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }

   /* private void checkLoginParams(String userName, String userPwd) {
        if(StringUtils.isBlank(userName)){
            throw new ParamsException("用户名不能为空");
        }

    }*/
   @Transactional(propagation = Propagation.REQUIRED)
   public  void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
       checkParams(userId,oldPassword,newPassword,confirmPassword);
       User user = selectByPrimaryKey(userId);
       user.setUserPwd(Md5Util.encode(newPassword));
       AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"密码更新失败");
   }

    private void checkParams(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
       User user = selectByPrimaryKey(userId);
       AssertUtil.isTrue(null==userId || null==selectByPrimaryKey(userId),"用户不存在");
       AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原密码");
       AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
       AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请确认密码");
       AssertUtil.isTrue(!(newPassword.equals(confirmPassword)),"新密码输入不一致，请重新确认");

       AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确");
       AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码与旧密码相同");


    }

}
