package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 统一的认证接口
 * @date 2024/3/21 17:33
 */
public interface AuthService {

    /**
    * @description 认证方法
    * @Param [com.xuecheng.ucenter.model.dto.AuthParamsDto]
    * @return com.xuecheng.ucenter.model.dto.XcUserExt
    * @author LuoXuanwei
    * @date 2024/3/21 17:34
    */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
